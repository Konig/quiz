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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * the final panel.
 * @author thierry
 */
public class EndPanel extends Panel {

    private static final Logger LOGGER = Logger.getLogger(EndPanel.class);

    public EndPanel(String id) {
        super(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        // refresh the page after 7 seconds
        // first try was to insert this js into the markup, didn't work. gkw
        super.renderHead(response);
        String js =
                "function timeout(){"
                + "window.setTimeout(\"reload()\",7000)}"
                + "function reload(){"
                + "window.location.reload(true);"
                + "return}";
        response.renderJavaScript(js, "refresh-function");
        response.renderJavaScript("window.onload=timeout();", "quiz-refresh_loader");
    }

    @Override
    protected void onAfterRender() {
        //after rendering, we remove the session. next interaction will display the langpanel
        getSession().invalidateNow();
        super.onAfterRender();
    }
}
