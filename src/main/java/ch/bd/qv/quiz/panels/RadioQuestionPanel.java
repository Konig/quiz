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

import ch.bd.qv.quiz.ejb.QuestionBean;
import ch.bd.qv.quiz.entities.RadioQuestion;
import com.google.common.collect.Lists;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 *
 * @author thierry
 */
public class RadioQuestionPanel extends BaseQuestionPanel {
    
    @Inject
    private QuestionBean bean; 
    
    private Model<String> input = new Model<>();
    private final RadioQuestion radioQuestion;
    

    public RadioQuestionPanel(String inner, RadioQuestion bq) {
        super(inner, bq);
        this.radioQuestion =bq; 
        RadioGroup<String> rg = new RadioGroup<>("radiogroup", input);
        rg.add(new ListView<String>("repeater", Lists.newArrayList(bq.getAnswerKeys())) {

            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Radio("radio", item.getModel()));
                item.add(new Label("radio_label", new ResourceModel(item.getModelObject())));
            }
        });
        rg.setRequired(true);
        add(rg);
    }

    @Override
    protected void processQuestion() {
       bean.processRadioQuestion(radioQuestion, input.getObject());
    }
    
}
