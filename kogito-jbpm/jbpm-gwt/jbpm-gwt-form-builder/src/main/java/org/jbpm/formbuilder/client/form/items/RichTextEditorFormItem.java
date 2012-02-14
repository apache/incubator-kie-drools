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
import org.jbpm.formapi.common.panels.RichTextEditor;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.RichTextEditorRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a rich text editor
 */
@Reflectable
public class RichTextEditorFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private RichTextEditor editor = new RichTextEditor();
    private String html = "";
    
    public RichTextEditorFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public RichTextEditorFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        editor.getEditorToolbar().setHeight("50px");
        editor.setSize("400px", "200px");
        setSize("400px", "200px");
        editor.setHTML(this.html);
        add(editor);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("html", editor.getHTML());
        map.put("width", getWidth());
        map.put("height", getHeight());
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.html = extractString(asPropertiesMap.get("html"));
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        populate(this.editor);
    }
    
    private void populate(RichTextEditor editor) {
        if (getHeight() != null && !"".equals(getHeight())) {
            editor.setHeight(getHeight());
        }
        if (getWidth() != null && !"".equals(getWidth())) {
            editor.setWidth(getWidth());
        }
        if (this.html != null && !"".equals(this.html)) {
            editor.setHTML(this.html);
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        RichTextEditorRepresentation rep = super.getRepresentation(new RichTextEditorRepresentation());
        rep.setHtml(this.html);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof RichTextEditorRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "RichTextEditorRepresentation"));
        }
        super.populate(rep);
        RichTextEditorRepresentation rrep = (RichTextEditorRepresentation) rep;
        this.html = rrep.getHtml();
        if (rrep.getWidth() != null && !"".equals(rrep.getWidth())) {
            setWidth(rrep.getWidth());
        }
        if (rrep.getHeight() != null && !"".equals(rrep.getHeight())) {
            setHeight(rrep.getHeight());
        }
        populate(this.editor);
    }
    
    @Override
    public FBFormItem cloneItem() {
        RichTextEditorFormItem clone = super.cloneItem(new RichTextEditorFormItem(getFormEffects()));
        clone.html = this.html;
        populate(clone.editor);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        RichTextEditor display = new RichTextEditorFormItem().editor;
        populate(display);
        Object input = getInputValue(data);
        if (input != null) {
            display.setHTML(input.toString());
        }
        if (getOutput() != null && getOutput().getName() != null) {
            display.setName(getOutput().getName());
        }
        super.populateActions(display.getElement());
        return display;
    }

}
