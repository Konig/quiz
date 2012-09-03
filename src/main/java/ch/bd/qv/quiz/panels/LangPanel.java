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

import com.google.common.base.Joiner;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * displays dynamically a list of configurated languages
 * @author thierry
 */
public class LangPanel extends Panel {

    private static final Logger LOGGER = Logger.getLogger(LangPanel.class);
    
    @Inject
    private List<Locale> supportedLocales;

    public LangPanel(String id) {
        super(id);
        LOGGER.debug("lang received on panel: "+Joiner.on(":").join(supportedLocales.iterator()));
        add(new ListView<Locale>("list", supportedLocales) {
            @Override
            protected void populateItem(final ListItem<Locale> item) {
                
                AjaxLink<Void> langLink = new AjaxLink<Void>("lang") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getSession().setLocale(item.getModelObject());
                        BasePanel bp = findParent(BasePanel.class);
                        bp.driveNavigation();
                        target.add(bp);
                    }
                };
                langLink.add(new Label("label", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        LOGGER.debug("item: "+item.getModelObject());
                        return item.getModelObject().getLanguage().toUpperCase();
                    }
                }));
                item.add(langLink);
            }
        });

    }
}
