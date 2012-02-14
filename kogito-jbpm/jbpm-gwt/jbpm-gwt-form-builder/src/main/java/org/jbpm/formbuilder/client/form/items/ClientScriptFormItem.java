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
import org.jbpm.formapi.client.form.HasSourceReference;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.ClientScriptRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class ClientScriptFormItem extends FBFormItem implements HasSourceReference {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private ScriptElement script = Document.get().createScriptElement();
    
    private String type = "text/javascript";
    private String src = null;
    
    public ClientScriptFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public ClientScriptFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        Grid border = new Grid(1, 1);
        border.setSize("100px", "20px");
        border.setBorderWidth(1);
        border.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        border.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        border.setWidget(0, 0, new Image(FormBuilderResources.INSTANCE.clientScriptIcon()));
        border.getElement().insertFirst(this.script);
        add(border);
        setSize("100px", "20px");
    }

    @Override
    public void setSourceReference(String sourceReference) {
        script.setSrc(sourceReference);
    }

    @Override
    public String getSourceReference() {
        return script.getSrc();
    }

    @Override
    public List<String> getAllowedTypes() {
        List<String> retval = new ArrayList<String>();
        retval.add("js");
        return retval;
    }
    
    private void populate(ScriptElement script) {
        script.setSrc(src);
        script.setType(type);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", this.type);
        map.put("src", this.src);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.type = extractString(asPropertiesMap.get("type"));
        this.src = extractString(asPropertiesMap.get("src"));
        populate(this.script);
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        ClientScriptRepresentation rep = super.getRepresentation(new ClientScriptRepresentation());
        rep.setType(this.type);
        rep.setSrc(this.src);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof ClientScriptRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "ClientScriptRepresentation"));
        }
        super.populate(rep);
        ClientScriptRepresentation csrep = (ClientScriptRepresentation) rep;
        this.src = csrep.getSrc();
        this.type = csrep.getType();
        populate(this.script);
    }
    
    @Override
    public FBFormItem cloneItem() {
        ClientScriptFormItem clone = super.cloneItem(new ClientScriptFormItem());
        clone.src = this.src;
        clone.type = this.type;
        populate(clone.script);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        MyWidget widget = new MyWidget();
        widget.set(script);
        super.populateActions(widget.getElement());
        return widget; 
    }

    class MyWidget extends Widget {
        
        public MyWidget() {
            setElement(DOM.createElement("script"));
        }
        
        public void set(ScriptElement elem) {
            getElement().setAttribute("src", elem.getSrc());
            getElement().setAttribute("type", elem.getType());
        }
    }
    
}
