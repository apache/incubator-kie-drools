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
package org.jbpm.formbuilder.client.form.items;

import gwtupload.client.SingleUploader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.UploadWithProgressBarRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class UploadWithProgressBarFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private boolean enabled = true;
    private boolean avoidRepeatFiles = false;
    private boolean autoSubmit = false;
    private String cssClassName;
    
    private final SingleUploader uploader = new SingleUploader();
    
    public UploadWithProgressBarFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public UploadWithProgressBarFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        String uploadAction = FormBuilderGlobals.getInstance().getService().getUploadActionURL();
        uploader.setServletPath(uploadAction);
        add(uploader);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("enabled", this.enabled);
        map.put("avoidRepeatFiles", this.avoidRepeatFiles);
        map.put("autoSubmit", this.autoSubmit);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        this.enabled = extractBoolean(asPropertiesMap.get("enabled"));
        this.avoidRepeatFiles = extractBoolean(asPropertiesMap.get("avoidRepeatFiles"));
        this.autoSubmit = extractBoolean(asPropertiesMap.get("autoSubmit"));
        
        populate(this.uploader);
    }

    private void populate(SingleUploader uploader) {
        if (getWidth() != null && !"".equals(getWidth())) {
            uploader.setWidth(getWidth());
        }
        if (getHeight() != null && !"".equals(getHeight())) {
            uploader.setHeight(getHeight());
        }
        uploader.setEnabled(this.enabled);
        uploader.setAvoidRepeatFiles(this.avoidRepeatFiles);
        uploader.setAutoSubmit(this.autoSubmit);
        if (this.cssClassName != null) {
            uploader.setStyleName(this.cssClassName);
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        UploadWithProgressBarRepresentation urep = super.getRepresentation(new UploadWithProgressBarRepresentation());
        urep.setAutoSubmit(this.autoSubmit);
        urep.setAvoidRepeatFiles(this.avoidRepeatFiles);
        urep.setCssClassName(this.cssClassName);
        urep.setEnabled(this.enabled);
        return urep;
    }
    
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof UploadWithProgressBarRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "UploadWithProgressBarRepresentation"));
        }
        super.populate(rep);
        UploadWithProgressBarRepresentation urep = (UploadWithProgressBarRepresentation) rep;
        this.autoSubmit = urep.isAutoSubmit();
        this.avoidRepeatFiles = urep.isAvoidRepeatFiles();
        this.cssClassName = urep.getCssClassName();
        this.enabled = urep.isEnabled();
        
        populate(this.uploader);
    }
    
    @Override
    public FBFormItem cloneItem() {
        UploadWithProgressBarFormItem clone = super.cloneItem(new UploadWithProgressBarFormItem());
        clone.autoSubmit = this.autoSubmit;
        clone.avoidRepeatFiles = this.avoidRepeatFiles;
        clone.cssClassName = this.cssClassName;
        clone.enabled = this.enabled;
        clone.populate(clone.uploader);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> formData) {
        SingleUploader uploader = new SingleUploader();
        uploader.setServletPath("upload");
        populate(uploader);
        if (getOutput() != null && getOutput().getName() != null) {
            uploader.getFileInput().setName(getOutput().getName());
        }
        super.populateActions(((FileUpload) uploader.getFileInput()).getElement());
        return uploader;
    }

}
