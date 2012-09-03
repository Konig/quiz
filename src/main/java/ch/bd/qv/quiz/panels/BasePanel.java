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

import ch.bd.qv.quiz.HomePage;
import ch.bd.qv.quiz.app.NavigationEnum;
import ch.bd.qv.quiz.app.QuizSessionData;
import ch.bd.qv.quiz.ejb.PersonBean;
import ch.bd.qv.quiz.ejb.QuestionBean;
import ch.bd.qv.quiz.entities.BaseQuestion;
import ch.bd.qv.quiz.entities.CheckQuestion;
import ch.bd.qv.quiz.entities.FreeQuestion;
import ch.bd.qv.quiz.entities.Person;
import ch.bd.qv.quiz.entities.RadioQuestion;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author thierry
 */
public class BasePanel extends Panel {
    //logger

    private static final Logger LOGGER = Logger.getLogger(BasePanel.class);
    //dependencies
    @Inject
    private QuizSessionData data;
    @Inject
    private QuestionBean questionBean;
    @Inject
    private PersonBean personBean;
    //comp var
    private Form form;

    public BasePanel(String id) {
        super(id);
        add(new FeedbackPanel("feedback"));

        form = new Form("form");
        add(form);
        form.add(new LangPanel("swapPanel"));
        final AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("submit") {
            @Override
            public boolean isVisible() {
                return isVisibleNotOnLangAndEnd(); 
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                driveNavigation();
                target.add(findParent(BasePanel.class));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(findParent(BasePanel.class));
            }
        };
        final AjaxLink cancelLink = new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().invalidateNow();
                setResponsePage(HomePage.class);
                target.add(findParent(BasePanel.class));
            }

            @Override
            public boolean isVisible() {
                return isVisibleNotOnLangAndEnd(); 
            }
        };
        ajaxSubmitLink.add(new Label("label", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                if (data.getNavigation() == NavigationEnum.QUESTION && data.getQuestionNo() == data.getQuestions().size()) {
                    LOGGER.debug("last question, display end. ");
                    return new StringResourceModel("button.end", BasePanel.this, null).getObject();
                } else {

                    return new StringResourceModel("button.weiter", BasePanel.this, null).getObject();
                }
            }
        }));

        form.add(ajaxSubmitLink, cancelLink);
    }

    private boolean isVisibleNotOnLangAndEnd() {
        switch (data.getNavigation()) {
            case LANG:
            case END:
                return false;
            default:
                return true;
        }
    }

    /**
     * drives the navigation from panel to panel. each panel has a enum, each question a number 0-x
     */
    public void driveNavigation() {
        switch (data.getNavigation()) {
            case LANG:
                LOGGER.debug("NAVIGATION:  lang => data");
                data.setNavigation(NavigationEnum.BASE_DATA);
                form.addOrReplace(new DataPanel("swapPanel"));
                break;
            case BASE_DATA:
                personBean.storePersonAndInitQuizResult(data,(Person) ((DataPanel) form.get("swapPanel")).getDefaultModelObject());
                LOGGER.debug("NAVIGATION:  data => question 0");
                data.setNavigation(NavigationEnum.QUESTION);
                driveQuestionNavigation(data.getQuestions().get(0));
                data.setQuestionNo(1);
                break;
            case QUESTION:
                ((BaseQuestionPanel) form.get("swapPanel")).processQuestion();
                if (data.getQuestionNo() == data.getQuestions().size()) {
                    questionBean.endQuiz(data);
                    LOGGER.debug("NAVIGATION:  Question: " + data.getQuestionNo() + " => END");
                    data.setNavigation(NavigationEnum.END);
                    form.addOrReplace(new EndPanel("swapPanel"));
                } else {
                    LOGGER.debug("NAVIGATION:  Question: " + data.getQuestionNo() + " => QUESTION " + data.getQuestionNo() + 1);
                    driveQuestionNavigation(data.getQuestions().get(data.getQuestionNo()));
                    data.setQuestionNo(data.getQuestionNo() + 1);
                }
                break;
            case END:
                LOGGER.debug("END:  Removing session..");
                break;
            default:
                throw new IllegalArgumentException("state " + data.getNavigation() + " unsupported");
        }
    }

    private void driveQuestionNavigation(BaseQuestion bq) {
        if (bq instanceof RadioQuestion) {
            LOGGER.debug("NAVIGATION: RadioQuestion found. ");
            form.addOrReplace(new RadioQuestionPanel("swapPanel", (RadioQuestion) bq));
        }
        if (bq instanceof CheckQuestion) {
            LOGGER.debug("NAVIGATION: CheckQuestion found. ");
            form.addOrReplace(new CheckQuestionPanel("swapPanel", (CheckQuestion) bq));
        }
        if (bq instanceof FreeQuestion) {
            LOGGER.debug("NAVIGATION: FreeQuestion found. ");
            form.addOrReplace(new FreeQuestionPanel("swapPanel", (FreeQuestion) bq));
        }
    }
}
