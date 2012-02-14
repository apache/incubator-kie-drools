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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.FileInputRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a file input
 */
@Reflectable
public class FileInputFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private FileUpload fileUpload = new FileUpload();
    
    private String name;
    private String id;
    private String accept;

    public FileInputFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public FileInputFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(fileUpload);
        setHeight("27px");
        setWidth("100px");
        fileUpload.setSize(getWidth(), getHeight());
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("accept", this.accept);
        return map;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.name = extractString(asPropertiesMap.get("name"));
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        this.id = extractString(asPropertiesMap.get("id"));
        this.accept = extractString(asPropertiesMap.get("accept"));

        populate(this.fileUpload);
    }

    private void populate(FileUpload fileUpload) {
        if (this.name != null) {
            fileUpload.setName(this.name);
        }
        if (getWidth() != null) {
            fileUpload.setWidth(getWidth());
        }
        if (getHeight() != null) {
            fileUpload.setHeight(getHeight());
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        FileInputRepresentation rep = super.getRepresentation(new FileInputRepresentation());
        rep.setId(this.id);
        rep.setName(this.name);
        rep.setAccept(this.accept);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof FileInputRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "FileInputRepresentation"));
        }
        super.populate(rep);
        FileInputRepresentation frep = (FileInputRepresentation) rep;
        this.id = frep.getId();
        this.name = frep.getName();
        this.accept = frep.getAccept();
        populate(this.fileUpload);
    }
    
    @Override
    public FBFormItem cloneItem() {
        FileInputFormItem clone = new FileInputFormItem(getFormEffects());
        clone.accept = this.accept;
        clone.setHeight(this.getHeight());
        clone.id = this.id;
        clone.name = this.name;
        clone.setWidth(this.getWidth());
        clone.populate(clone.fileUpload);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        FileUpload fu = new FileUpload();
        populate(fu);
        if (getOutput() != null && getOutput().getName() != null) {
            fu.setName(getOutput().getName());
        }
        super.populateActions(fu.getElement());
        return fu;
    }
}
