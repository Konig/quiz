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

import ch.bd.qv.quiz.ejb.UploadBean;
import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author thierry
 */
public class AdminPanel extends Panel 
{
     @Inject
     private UploadBean uploadBean; 
    
    private FileUploadField fup; 
    public AdminPanel(String id)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        Form form = new Form("form");
        form.setMultiPart(true);
        form.add(fup = new FileUploadField("fileupload",new Model()));
        fup.setRequired(true);
        form.add(new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> uploads = fup.getConvertedInput();
                try
                {
                uploadBean.purgeAndUpload(uploads.get(0).getBytes());
                }catch(Exception e)
                {
                    AdminPanel.this.get("feedback").error(Throwables.getStackTraceAsString(e));
                }
                target.add(findParent(AdminPanel.class));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
               target.add(findParent(AdminPanel.class));
            }
        });
        add(form);
    }
    
}
