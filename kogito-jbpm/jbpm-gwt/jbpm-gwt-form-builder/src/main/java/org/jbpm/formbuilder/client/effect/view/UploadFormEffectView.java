/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.client.effect.view;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.SingleUploader;

import java.util.List;

import org.jbpm.formbuilder.client.FilesLoadedHandler;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.effect.UploadFormEffect;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UploadFormEffectView extends PopupPanel {
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    
    private final UploadFormEffect effect;
    private final SingleUploader uploader  = new SingleUploader();
    private final VerticalPanel panel = new VerticalPanel();
    private final FilesDataPanel dataTable = new FilesDataPanel();
    
    public UploadFormEffectView(UploadFormEffect formEffect) {
        this.effect = formEffect;
        
        HorizontalPanel uploadPanel = new HorizontalPanel();
        uploadPanel.add(new Label(i18n.UploadAFile()));
        startUploader();
        uploadPanel.add(uploader);
        
        VerticalPanel selectPanel = new VerticalPanel();
        selectPanel.add(new Label(i18n.SelectAFile()));
        selectPanel.add(dataTable);
        
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(createConfirmButton());
        buttonPanel.add(createCancelButton());
        
        panel.add(uploadPanel);
        panel.add(selectPanel);
        panel.add(buttonPanel);
        
        this.server.getFiles(this.effect.getAllowedTypes(), new FilesLoadedHandler() {
            @Override
            public void onFilesLoaded(List<String> files) {
                dataTable.setFiles(files);
            }
        });
        setWidget(panel);
    }

    private void startUploader() {
        this.uploader.setAutoSubmit(true);
        this.uploader.setServletPath(server.getUploadFileURL());
        this.uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
            @Override
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus() == Status.SUCCESS) {
                    String url = uploader.getServerInfo().message;
                    dataTable.addNewFile(url);
                }
            }
        });
    }
    
    private Button createCancelButton() {
        Button cancelButton = new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        return cancelButton;
    }

    private Button createConfirmButton() {
        Button confirmButton = new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String url = dataTable.getSelection();
                if (url == null) {
                    Window.alert(i18n.YouMustSelectAnItem(i18n.ConfirmButton(), i18n.CancelButton()));
                } else {
                    effect.setSrcUrl(url);
                    effect.createStyles();
                    hide();
                }
            }
        });
        return confirmButton;
    }
    
/*
    private final UploadFormEffect effect;
    private final FileUpload fileInput = new FileUpload();
    private final FormPanel form = new FormPanel();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    public UploadFormEffectView(UploadFormEffect formEffect) {
        this.effect = formEffect;
        InputElement.as(fileInput.getElement()).setAccept(toString(this.effect.getAllowedTypes()));
        VerticalPanel content = new VerticalPanel();
        form.setAction(server.getUploadFileURL());
        form.setMethod(FormPanel.METHOD_POST);
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
                String srcUrl = removePre(event.getResults());
                if (srcUrl == null || "".equals(srcUrl)) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntUploadFile()));
                } else {
                    effect.setSrcUrl(srcUrl);
                    effect.createStyles();
                }
                RootPanel.get().remove(form);
                hide();
            }
        });
        fileInput.setName("uploadFile");

        HorizontalPanel inputPanel = new HorizontalPanel();
        inputPanel.add(new Label(i18n.SelectAFile()));
        inputPanel.add(fileInput);
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.add(createConfirmButton());
        buttonsPanel.add(createCancelButton());
        content.add(inputPanel);
        content.add(buttonsPanel);
        form.add(content);
        setWidget(form);
    }

    private String toString(List<String> styles) {
        StringBuilder builder = new StringBuilder();
        if (styles != null) {
            String type = null;
            for (Iterator<String> it = styles.iterator(); it.hasNext(); type = it.next()) {
                builder.append(type);
                if (it.hasNext()) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }
    
    private String removePre(String srcUrl) {
        if (srcUrl.startsWith("<pre>")) {
            srcUrl = srcUrl.replace("<pre>", "");
        }
        if (srcUrl.startsWith("<PRE>")) {
            srcUrl = srcUrl.replace("<PRE>", "");
        }
        if (srcUrl.endsWith("</pre>")) {
            srcUrl = srcUrl.replace("</pre>", "");
        }
        if (srcUrl.endsWith("</PRE>")) {
            srcUrl = srcUrl.replace("</PRE>", "");
        }
        return srcUrl;
    }

    private Button createCancelButton() {
        Button cancelButton = new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        return cancelButton;
    }

    private Button createConfirmButton() {
        Button confirmButton = new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.submit();
            }
        });
        return confirmButton;
    }
*/
}
