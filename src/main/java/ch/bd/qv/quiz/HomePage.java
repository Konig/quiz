package ch.bd.qv.quiz;

import ch.bd.qv.quiz.panels.BasePanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * basic page for the quiz
 * @author thierry
 */
public class HomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final BasePanel wmc = new BasePanel("content");
        wmc.setOutputMarkupPlaceholderTag(true);
        addOrReplace(wmc);
    }
}
