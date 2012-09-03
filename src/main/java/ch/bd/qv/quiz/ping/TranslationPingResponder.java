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
package ch.bd.qv.quiz.ping;

import ch.bd.qv.quiz.SimplePropertyResourceLoader;
import ch.bd.qv.quiz.config.ConfigKeys;
import ch.bd.qv.quiz.config.ConfigValue;
import ch.bd.qv.quiz.entities.BaseQuestion;
import ch.bd.qv.quiz.entities.CheckQuestion;
import ch.bd.qv.quiz.entities.RadioQuestion;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import static ch.bd.qv.quiz.config.ConfigKeys.*;
import ch.bd.qv.quiz.ejb.QuestionBean;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

/**
 * checks if any key is missing
 *
 * @author thierry
 */
public class TranslationPingResponder implements IPingResponder {
    
    private static final Logger LOGGER = Logger.getLogger(TranslationPingResponder.class);
    //resources
    @Inject
    private QuestionBean questionBean;
    @Inject
    @ConfigValue(NO_OF_RADIO_QUESTION)
    int radio;
    @Inject
    @ConfigValue(NO_OF_CHECK_QUESTION)
    int check;
    @Inject
    @ConfigValue(NO_OF_FREE_QUESTION)
    int free;
    @Inject
    private List<Locale> supportedLocales;
    @Inject
    @ConfigValue(ConfigKeys.RESOURCES_FOLDER)
    private String pathToResourceFolders;
    //member
    private List<String> values = new ArrayList<>();
    private boolean failed = false;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public boolean isValid() {
        List<BaseQuestion> questions = questionBean.getAllQuestions();
        //no further check if any of these two apply
        if (questions.isEmpty()) {
            values.add("No Questions loaded. This application is doomed.");
            failed = true;
            return false;
        }
        if (radio + check + free > questions.size()) {
            values.add("Not enough questions for a total size of " + (radio + check + free));
            failed = true;
            return false;
        }
        IStringResourceLoader resLoader = new SimplePropertyResourceLoader(pathToResourceFolders);
        
        List<String> keys = new ArrayList<>();
        for (BaseQuestion bq : questions) {
            keys.add(bq.getQuestionKey());
            if (bq instanceof RadioQuestion) {
                RadioQuestion rq = (RadioQuestion) bq;
                keys.addAll(rq.getAnswerKeys());
                keys.add(rq.getRightAnswerKeys());
                if (!rq.getAnswerKeys().contains(rq.getRightAnswerKeys())) {
                    failed = true;
                    values.add(rq.getRightAnswerKeys() + " is not present in RadioQuestion");
                }
            }
            if (bq instanceof CheckQuestion) {
                CheckQuestion cq = (CheckQuestion) bq;
                keys.addAll(cq.getAnswerKeys());
                keys.addAll(cq.getRightAnswersKeys());
                if (!cq.getAnswerKeys().containsAll(cq.getRightAnswersKeys())) {
                    failed = true;
                    values.add(Joiner.on(":").join(cq.getRightAnswersKeys()) + " one or more are not present in CheckQuestion");
                }
            }
        }
        for (String key : keys) {
            for (Locale loc : supportedLocales) {
                LOGGER.debug("key: " + key + " locale: " + loc);
                if (null == resLoader.loadStringResource(getClass(), key, loc, null, null)) {
                    
                    values.add("Key: " + key + " is not translated in lang: " + loc);
                    failed = true;
                } else {
                    LOGGER.debug("...found");
                }
            }
        }
        return !failed;
        
    }
    
    @Override
    public List<String> getMessage() {
        if (failed) {
            LOGGER.debug("error occured..");
            return values;
        } else {
            LOGGER.debug("all fine");
            return Lists.newArrayList("Translations are complete");
        }
    }
}
