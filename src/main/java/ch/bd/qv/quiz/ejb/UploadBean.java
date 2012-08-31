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

import ch.bd.qv.quiz.entities.BaseQuestion;
import ch.bd.qv.quiz.entities.CheckQuestion;
import ch.bd.qv.quiz.entities.FreeQuestion;
import ch.bd.qv.quiz.entities.RadioQuestion;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.nio.charset.Charset;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UploadBean {

    private static final Logger LOGGER = Logger.getLogger(UploadBean.class);
    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void upload(byte[] bytes) {
        String inputfile = new String(bytes, Charset.forName("UTF-8"));
        Iterable<String> lines = Splitter.on("\n").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).split(inputfile);
        for (String line : lines) {
            LOGGER.debug("LINE: " + line);
            List<String> question = Lists.newArrayList(Splitter.on(";").split(line));
            LOGGER.debug(Joiner.on(":").join(question));

            switch (question.get(0).replaceAll("\\W", "").toUpperCase()) {

                case "FREE":
                    makeFreeQuestion(question);
                    break;
                case "RADIO":
                    makeRadioQuestion(question);
                    break;
                case "CHECK":
                    makeCheckQuestion(question);
                    break;
                default:
                    throw new IllegalArgumentException("type: " + question.get(0) + " is not known. check csv");
            }
        }
    }

    private void makeRadioQuestion(List<String> question) {
        Preconditions.checkArgument(question.size() == 4);
        RadioQuestion rq = new RadioQuestion();
        rq.setAnswerKeys(Sets.newHashSet(Splitter.on(",").split(question.get(2))));
        rq.setRightAnswerKeys(question.get(3));
        commonOperationAndPersist(rq, question.get(1));
    }

    private void makeCheckQuestion(List<String> question) {
        Preconditions.checkArgument(question.size() == 4);
        CheckQuestion cq = new CheckQuestion();
        cq.setAnswerKeys(Sets.newHashSet(Splitter.on(",").split(question.get(2))));
        cq.setRightAnswersKeys(Sets.newHashSet(Splitter.on(",").split(question.get(3))));
        commonOperationAndPersist(cq, question.get(1));
    }

    private void makeFreeQuestion(List<String> question) {
        Preconditions.checkArgument(question.size() == 3);
        FreeQuestion fq = new FreeQuestion();
        fq.setAnswer(question.get(2));
        commonOperationAndPersist(fq, question.get(1));
    }

    private void commonOperationAndPersist(BaseQuestion bq, String key) {
        bq.setQuestionKey(key);
        em.persist(bq);
    }
}
