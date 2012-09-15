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
package ch.bd.qv.quiz;

import ch.bd.qv.quiz.panels.AdminPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * administrator page. 
 * @author thierry
 */
public class AdminPage extends HomePage {
    
     private static final long serialVersionUID = 1L;
     


    public AdminPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final AdminPanel wmc = new AdminPanel("content");
        wmc.setOutputMarkupPlaceholderTag(true);
        addOrReplace(wmc);
    }
}
