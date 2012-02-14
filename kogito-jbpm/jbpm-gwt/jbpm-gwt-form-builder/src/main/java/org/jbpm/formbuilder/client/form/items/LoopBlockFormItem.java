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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formapi.shared.api.items.LoopBlockRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * server-side form item. Represents a loop block
 */
@Reflectable
public class LoopBlockFormItem extends LayoutFormItem {

    private String variableName;
    private SimplePanel loopBlock = new SimplePanel();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    public LoopBlockFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public LoopBlockFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        loopBlock.setStyleName("loopBlockBorder");
        loopBlock.setSize("100%", "50px");
        add(loopBlock);
        setSize("100%", "50px");
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("variableName", this.variableName);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.variableName = extractString(asPropertiesMap.get("variableName"));
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        LoopBlockRepresentation rep = getRepresentation(new LoopBlockRepresentation());
        rep.setInputName(getInput() == null ? null : getInput().getName());
        FBFormItem loopItem = (FBFormItem) this.loopBlock.getWidget();
        if (loopItem != null) {
            rep.setLoopBlock(loopItem.getRepresentation());
        }
        rep.setVariableName(this.variableName);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof LoopBlockRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "LoopBlockRepresentation"));
        }
        super.populate(rep);
        LoopBlockRepresentation lrep = (LoopBlockRepresentation) rep;
        this.variableName = lrep.getVariableName();
        this.loopBlock.clear();
        if (lrep.getInputName() != null && !"".equals(lrep.getInputName())) {
            InputData input = new InputData();
            input.setName(lrep.getInputName());
            lrep.setInput(input);
        }
        if (lrep.getLoopBlock() != null) {
            FBFormItem child = super.createItem(lrep.getLoopBlock());
            this.loopBlock.add(child);
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        LoopBlockFormItem clone = super.cloneItem(new LoopBlockFormItem(getFormEffects()));
        FBFormItem loopItem = (FBFormItem) this.loopBlock.getWidget();
        clone.add(loopItem);
        clone.variableName = this.variableName;
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        FlowPanel display = new FlowPanel();
        FBFormItem subItem = (FBFormItem) loopBlock.getWidget();
        Object input = getInputValue(data);
        String inputName = getInput() == null ? null : getInput().getName(); 
        if (subItem != null && input != null && inputName != null) {
            Map<String, Object> subData = new HashMap<String, Object>();
            if (input.getClass().isArray()) {
                Object[] arr = (Object[]) input;
                for (Object obj : arr) {
                    subData.put(inputName, obj);
                    display.add(subItem.cloneDisplay(subData));
                }
            } else if (input instanceof Collection) {
                Collection<?> col = (Collection<?>) input;
                for (Object obj : col) {
                    subData.put(inputName, obj);
                    display.add(subItem.cloneDisplay(subData));
                }
            } else if (input instanceof Map) {
                Map<?,?> map = (Map<?,?>) input;
                for (Object obj : map.entrySet()) {
                    subData.put(inputName, obj);
                    display.add(subItem.cloneDisplay(subData));
                }
            }
        }
        display.setSize(getWidth(), getHeight());
        super.populateActions(display.getElement());
        return display;
    }

    @Override
    public HasWidgets getPanel() {
        return loopBlock;
    }

    @Override
    public boolean add(FBFormItem item) {
        if (loopBlock.getWidget() == null) {
            loopBlock.setWidget(item);
            return super.add(item);
        } else {
            bus.fireEvent(new NotificationEvent(Level.WARN, i18n.LoopBlockFull()));
            return false;
        }
    }
    
    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        if (loopBlock.getWidget() == null) {
            loopBlock.setWidget(phantom);
        }
    }

    @Override
    public void replacePhantom(FBFormItem item) {
        if (loopBlock.getWidget() == null || loopBlock.getWidget() instanceof PhantomPanel) {
            loopBlock.remove(loopBlock.getWidget());
            loopBlock.setWidget(item);
        }
    }
}
