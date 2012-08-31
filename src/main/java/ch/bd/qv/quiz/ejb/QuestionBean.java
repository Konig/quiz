/*
 * Copyright 2012 thierry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.bd.qv.quiz.ejb;

import ch.bd.qv.quiz.app.QuizSessionData;
import ch.bd.qv.quiz.entities.BaseQuestion;
import ch.bd.qv.quiz.entities.CheckQuestion;
import ch.bd.qv.quiz.entities.FreeQuestion;
import ch.bd.qv.quiz.entities.Person;
import ch.bd.qv.quiz.entities.QuizResult;
import ch.bd.qv.quiz.entities.QuizStateEnum;
import ch.bd.qv.quiz.entities.RadioQuestion;
import ch.bd.qv.quiz.entities.WrongAnswer;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
//muss stateful sein wegen cdi injection (QuizResult ist ein state)
@Stateful
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class QuestionBean {

    private static final Logger LOGGER = Logger.getLogger(QuestionBean.class);
    @PersistenceContext
    private EntityManager em;
    @Inject
    private QuizSessionData data;
    private Random random = new SecureRandom();

    public List<BaseQuestion> getRandomizedQuestions(int radio, int check, int free) {
        List<BaseQuestion> result = new ArrayList<>();
        result.addAll(getRadioQuestions(radio));
        result.addAll(getCheckQuestions(check));
        result.addAll(getFreeQuestions(free));
        Collections.shuffle(result);
        LOGGER.debug("result list: " + Joiner.on(":").join(result));
        return result;
    }

    public List<RadioQuestion> getRadioQuestions(int radio) {
        return getQuestions(RadioQuestion.class, radio);
    }

    public List<CheckQuestion> getCheckQuestions(int check) {
        return getQuestions(CheckQuestion.class, check);
    }

    public List<FreeQuestion> getFreeQuestions(int free) {
        return getQuestions(FreeQuestion.class, free);
    }

    private <T extends BaseQuestion> List<T> getQuestions(Class<T> type, int noOfQuestions) {
        if (noOfQuestions == 0) {
            return new ArrayList<>();
        }
        long temp = em.createNamedQuery("count" + type.getSimpleName(), Long.class).getSingleResult();
        if (temp > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(temp + " cannot be cast to int without changing its value.");
        }
        int totalcount = (int) temp;
        //size() als index wollen wir nicht. obviously
        Set<Integer> randomNumbers = getUniqueRandomNumbers(noOfQuestions, totalcount - 1);
        List<T> resultList = new ArrayList<>();
        for (Integer rand : randomNumbers) {
            resultList.add(em.createNamedQuery("get" + type.getSimpleName(), type).setFirstResult(rand).setMaxResults(1).getSingleResult());
        }
        return resultList;
    }

    private Set<Integer> getUniqueRandomNumbers(int amount, int max) {
        Preconditions.checkArgument(max >= amount, "amount: " + amount + " max: " + max);
        Set<Integer> results = new HashSet<>();
        while (results.size() != amount) {
            results.add(random.nextInt(max));
        }
        LOGGER.debug("random integer: " + Joiner.on(":").join(results));
        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processRadioQuestion(RadioQuestion radio, String chosen) {
        if (!radio.getRightAnswerKeys().contains(chosen)) {
            addWrongAnswer(radio, chosen);
        }
        storeQuizResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processFreeQuestion(FreeQuestion free, String chosen) {
        if (!free.getAnswer().equalsIgnoreCase(chosen.trim())) {
            LOGGER.debug("wrong input:|" + chosen + "| right: |" + free.getAnswer() + "|");
            addWrongAnswer(free, chosen);
        }
        storeQuizResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processCheckQuestion(CheckQuestion check, Set<String> chosenElements) {
        if (!check.getRightAnswersKeys().containsAll(chosenElements)) {
            LOGGER.debug("chosen: " + Joiner.on(":").join(chosenElements));
            LOGGER.debug("right: " + Joiner.on(":").join(check.getRightAnswersKeys()));
            addWrongAnswer(check, chosenElements.toArray(new String[chosenElements.size()]));
        }
        storeQuizResult();
    }

    private void addWrongAnswer(BaseQuestion bq, String... items) {
        WrongAnswer wa = new WrongAnswer();
        wa.getChosenAnswer().addAll(Arrays.asList(items));
        wa.setQuestion(bq);
        data.getResult().getWrongAnswers().add(wa);
        em.persist(wa);
    }

    private void storeQuizResult() {
        data.getResult().setQuizState(QuizStateEnum.STARTED);
        data.setResult(em.merge(data.getResult()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void endQuiz() {
        data.getResult().setQuizState(QuizStateEnum.FINISHED);
        data.getResult().setEndTime(Calendar.getInstance());
        data.setResult(em.merge(data.getResult()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void startQuiz(Person person) {
        QuizResult qr = new QuizResult();
        qr.setPerson(person);
        data.setResult(qr);
        qr.setQuestions(data.getQuestions());
        em.persist(qr);
        data.getResult().setQuizState(QuizStateEnum.NOT_STARTED);
        data.getResult().setStartTime(Calendar.getInstance());
        data.setResult(em.merge(data.getResult()));
    }
}
