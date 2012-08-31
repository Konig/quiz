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
package ch.bd.qv.quiz.base;

import ch.bd.qv.quiz.config.ConfigValue;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import static ch.bd.qv.quiz.config.ConfigKeys.*;
import ch.bd.qv.quiz.ejb.QuestionBean;
import ch.bd.qv.quiz.entities.BaseQuestion;
import java.util.List;
import javax.enterprise.inject.Produces;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
@SessionScoped
public class QuestionListProducer implements Serializable {

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
    private QuestionBean questionBean;
    private static final Logger LOGGER = Logger.getLogger(QuestionListProducer.class); 

    @Produces
    public List<BaseQuestion> produce() {

        LOGGER.debug("new quizdata built, argument: radio: " + radio + " check: " + check + " free: " + free);
        return questionBean.getRandomizedQuestions(radio, check, free);
    }
}
