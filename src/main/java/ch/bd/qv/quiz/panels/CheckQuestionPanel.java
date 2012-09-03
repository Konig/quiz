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
import ch.bd.qv.quiz.entities.CheckQuestion;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * displays a checkboxmultiplechoice field (multiple possible answers)
 * @author thierry
 */
public class CheckQuestionPanel extends BaseQuestionPanel {

    // resources
    @Inject
    private QuestionBean questionBean;
    @Inject
    private QuizSessionData data; 
    //members
    private CheckQuestion checkQuestion;
    private IModel<Set<String>> model = new Model();

    public CheckQuestionPanel(String inner, CheckQuestion bq) {
        super(inner, bq);
        this.checkQuestion = bq;
        add(new CheckBoxMultipleChoice<>("checkboxes", model, Lists.newArrayList(
                checkQuestion.getAnswerKeys().iterator()), new IChoiceRenderer<String>() {
            @Override
            public Object getDisplayValue(String object) {
                return new StringResourceModel(object, CheckQuestionPanel.this, null).getObject();
            }

            @Override
            public String getIdValue(String object, int index) {
                return new StringResourceModel(object, CheckQuestionPanel.this, null).getObject();
            }
        }).setRequired(true));
    }

    @Override
    protected void processQuestion() {
        questionBean.processCheckQuestion(data, checkQuestion, Sets.newHashSet(model.getObject()));
    }
}
