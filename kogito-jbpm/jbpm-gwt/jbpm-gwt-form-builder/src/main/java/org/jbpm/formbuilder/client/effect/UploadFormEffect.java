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
package org.jbpm.formbuilder.client.effect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.HasSourceReference;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.effect.view.UploadFormEffectView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class UploadFormEffect extends FBFormEffect {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private String srcUrl;
    
    public UploadFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().UploadEffectLabel(), true);
    }

    @Override
    public void createStyles() {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        HasSourceReference item = (HasSourceReference) getItem();
        dataSnapshot.put("item", item);
        dataSnapshot.put("oldSrcUrl", item.getSourceReference());
        dataSnapshot.put("newSrcUrl", getSrcUrl());
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override
            public void undoAction(UndoableEvent event) {
                HasSourceReference item = (HasSourceReference) event.getData("item");
                String srcRef = (String) event.getData("oldSrcUrl");
                item.setSourceReference(srcRef);
            }
            @Override
            public void doAction(UndoableEvent event) {
                HasSourceReference item = (HasSourceReference) event.getData("item");
                String srcRef = (String) event.getData("newSrcUrl");
                item.setSourceReference(srcRef);
            }
        }));
    }

    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && item instanceof HasSourceReference;
    }
    
    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }
    
    public String getSrcUrl() {
        return srcUrl;
    }

    @Override
    public PopupPanel createPanel() {
        return new UploadFormEffectView(this);
    }

    public List<String> getAllowedTypes() {
        HasSourceReference item = (HasSourceReference) getItem();
        return item.getAllowedTypes();
    }
}
