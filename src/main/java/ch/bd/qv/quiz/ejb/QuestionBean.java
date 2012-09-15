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
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 * exposes all operations for manipulating the questions
 * @author thierry
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class QuestionBean {

    private static final Logger LOGGER = Logger.getLogger(QuestionBean.class);
    @PersistenceContext
    private EntityManager em;
    private Random random = new SecureRandom();

    /**
     * produces a randomized question list 
     * @param radio no of radio questions
     * @param check no of check questions
     * @param free no of free questions
     * @return a list of questions objects
     */
    public List<BaseQuestion> getRandomizedQuestions(int radio, int check, int free) {
        List<BaseQuestion> result = new ArrayList<>();
        result.addAll(getRadioQuestions(radio));
        result.addAll(getCheckQuestions(check));
        result.addAll(getFreeQuestions(free));
        Collections.shuffle(result, random);
        LOGGER.debug("result list: " + Joiner.on(":").join(result));
        return result;
    }

    /**
     * facade for radioQuestions. returns a specified number of radioQuestions
     * @param radio
     * @return 
     */
    public List<RadioQuestion> getRadioQuestions(int radio) {
        return getQuestions(RadioQuestion.class, radio);
    }

    /**
     * facade for checkQuestion. returns a specified number of checkQuestions
     */
    public List<CheckQuestion> getCheckQuestions(int check) {
        return getQuestions(CheckQuestion.class, check);
    }

    /**
     * facade for freeQuestions, returns a specified
     * @param free
     * @return 
     */
    public List<FreeQuestion> getFreeQuestions(int free) {
        return getQuestions(FreeQuestion.class, free);
    }

    /**
     * generic methodc to obtain questions. 
     * Contract: a simpleName of a Type must have a corresponding NamedQuery 
     * <ul>
     * <li>countSimpleName which returns the totalnumber found of this type</li> 
     * <li>getSimpleName which returns one question based on the index</li>
     * </ul>
     * @param <T> the concrete class type: radioQuestion, free etc. 
     * @param type the class
     * @param noOfQuestions number of questions desired
     * @return a list of questions or a illegalargumentexception for maxresult > integer.max
     */
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

    /**
     * generates a unique numberset from 0 - max with a size of amount
     * @param amount the size of the numberset
     * @param max the upper bound 
     */
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
    public void processRadioQuestion(QuizSessionData data, RadioQuestion radio, String chosen) {
        if (!radio.getRightAnswerKeys().contains(chosen)) {
            addWrongAnswer(data, radio, chosen);
        }
        storeQuizResult(data);
    }

    /**
     * checks the freeQuestion, adds a wrong answer in case of a failure
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processFreeQuestion(QuizSessionData data, FreeQuestion free, String chosen) {
        if (!free.getAnswer().equalsIgnoreCase(chosen.trim())) {
            LOGGER.debug("wrong input:|" + chosen + "| right: |" + free.getAnswer() + "|");
            addWrongAnswer(data,free, chosen);
        }
        storeQuizResult(data);
    }

    /**
     * checks the checkQuestion, adds a wrong answer in case of a failure
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processCheckQuestion(QuizSessionData data, CheckQuestion check, Set<String> chosenElements) {
        if (!check.getRightAnswersKeys().containsAll(chosenElements)) {
            LOGGER.debug("chosen: " + Joiner.on(":").join(chosenElements));
            LOGGER.debug("right: " + Joiner.on(":").join(check.getRightAnswersKeys()));
            addWrongAnswer(data,check, chosenElements.toArray(new String[chosenElements.size()]));
        }
        storeQuizResult(data);
    }

    /**
     * add a wrong answer with one or more given solutions
     */
    private void addWrongAnswer(QuizSessionData data, BaseQuestion bq, String... items) {
        WrongAnswer wa = new WrongAnswer();
        wa.getChosenAnswer().addAll(Arrays.asList(items));
        wa.setQuestion(bq);
        data.getResult().getWrongAnswers().add(wa);
        em.persist(wa);
    }

    /**
     * sets the state on started after each processed question
     */
    private void storeQuizResult(QuizSessionData data) {
        data.getResult().setQuizState(QuizStateEnum.STARTED);
        data.setResult(em.merge(data.getResult()));
    }

    /**
     * ends the quiz. set the finishdate and the state
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void endQuiz(QuizSessionData data) {
        data.getResult().setQuizState(QuizStateEnum.FINISHED);
        data.getResult().setEndTime(Calendar.getInstance());
        data.setResult(em.merge(data.getResult()));
    }

    /**
     * starts a quiz. set start date, state, person and questions
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void startQuiz(QuizSessionData data, Person person) {
        QuizResult qr = new QuizResult();
        qr.setPerson(person);
        data.setResult(qr);
        qr.setQuestions(data.getQuestions());
        em.persist(qr);
        data.getResult().setQuizState(QuizStateEnum.NOT_STARTED);
        data.getResult().setStartTime(Calendar.getInstance());
        data.setResult(em.merge(data.getResult()));
    }
    
    /**
     * loads all questions. use with care
     */
    public List<BaseQuestion> getAllQuestions()
    {
        return em.createNamedQuery("getAllQuestions", BaseQuestion.class).getResultList(); 
    }
}
