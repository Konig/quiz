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

import ch.bd.qv.quiz.ejb.PersonBean;
import ch.bd.qv.quiz.entities.Person;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author thierry
 */
public class DataPanel extends Panel {

    private static final Logger LOGGER = Logger.getLogger(DataPanel.class);
    @Inject
    private PersonBean personBean;

    public DataPanel(String id) {
        super(id, new CompoundPropertyModel<>(new Person()));
        final EmailTextField email = new EmailTextField("email", new Model());
        email.setRequired(true);
        email.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Person p = personBean.getPersonByEmail(email.getDefaultModelObjectAsString());
                if (p != null) {
                    LOGGER.debug("person found: " + p.getEmail() + " replacing model ");
                    setDefaultModelObject(p);
                    target.add(findParent(BasePanel.class));
                }
                ((Person) getDefaultModelObject()).setEmail(email.getModelObject());
            }
        });
        add(new FcBorder("emailcont",email));

        add(new FcBorder("namecont",new TextField<String>("name").setRequired(true)));
        add(new FcBorder("vornamecont",new TextField("vorname").setRequired(true)));
        add(new FcBorder("firmacont",new TextField("firma").setRequired(true)));
        add(new FcBorder("telcont",new TextField("tel")));

    }

    /**
     * displays the label in red, if the fc is invalid. the border does not 
     * provide any explicit markup
     */
    public static class FcBorder extends Border {
        
        private FormComponent fc; 
        
        public FcBorder(String id,FormComponent fc)
        {
            super(id);
//            add(new Label("desc", new ResourceModel("data."+fc.getId())));
            add(fc);
            this.fc = fc; 
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if(!fc.isValid())
            {
                tag.put("class", "invalid_fc");
            }
        }
        
    }
}
