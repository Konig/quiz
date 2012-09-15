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
package ch.bd.qv.quiz.panels;

import ch.bd.qv.quiz.app.QuizSessionData;
import ch.bd.qv.quiz.ejb.QuestionBean;
import ch.bd.qv.quiz.entities.FreeQuestion;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

/**
 * displays the question and a textfield for the answer. 
 * @author thierry
 */
public class FreeQuestionPanel extends BaseQuestionPanel{
    
    @Inject
    private QuestionBean questionBean; 
    @Inject
    private QuizSessionData data; 
    private  FreeQuestion freeQuestion;
    private Model<String> model = new Model<>(); 

    public FreeQuestionPanel(String inner, FreeQuestion bq) {
        super(inner, bq);
        this.freeQuestion = bq; 
        add(new TextField<>("field", model).setRequired(true));
        
        
    }

    @Override
    protected void processQuestion() {
        questionBean.processFreeQuestion(data, freeQuestion, model.getObject());
    }
    
}
