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
package ch.bd.qv.quiz.app;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import ch.bd.qv.quiz.entities.BaseQuestion;
import ch.bd.qv.quiz.entities.QuizResult;
import java.util.List;
import javax.annotation.PreDestroy;
import org.apache.log4j.Logger;

/**
 * data object for one run
 * contains the navigation, the questions and the result
 * @author thierry
 */
@SessionScoped
public class QuizSessionData implements Serializable {


    private static final Logger LOGGER = Logger.getLogger(QuizSessionData.class);
    @Inject
    private List<BaseQuestion> list;
    private Integer questionNo = 0;
    private NavigationEnum navigation = NavigationEnum.LANG;
    private QuizResult result;

    public QuizResult getResult() {
        return result;
    }

    public void setResult(QuizResult result) {
        this.result = result;
    }

    public List<BaseQuestion> getQuestions() {
        return list;
    }

    public Integer getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(Integer questionNo) {
        this.questionNo = questionNo;
    }

    public NavigationEnum getNavigation() {
        return navigation;
    }

    public void setNavigation(NavigationEnum navigation) {
        this.navigation = navigation;
    }

    @PreDestroy
    public void destroy() {
        LOGGER.debug("session is going down, removing quizdata");
    }
}
