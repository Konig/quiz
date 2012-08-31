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
import ch.bd.qv.quiz.entities.BaseQuestion;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author thierry
 */
public abstract class BaseQuestionPanel extends Panel {
    
    @Inject
    private QuizSessionData data; 

    public BaseQuestionPanel(String id, BaseQuestion bq) {
        super(id);
        add(new Label("legend", new StringResourceModel("legend.frage",BaseQuestionPanel.this,null,data.getQuestionNo()+1,data.getQuestions().size())));
        add(new Label("question", new ResourceModel(bq.getQuestionKey())));

    }

    protected abstract void processQuestion();
}
