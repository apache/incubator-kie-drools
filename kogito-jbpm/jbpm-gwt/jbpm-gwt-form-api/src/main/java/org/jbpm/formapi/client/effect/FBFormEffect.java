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
package org.jbpm.formapi.client.effect;

import org.jbpm.formapi.client.form.FBFormItem;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for right click actions. These effects add
 * special characteristics and settings to a given form item
 */
public abstract class FBFormEffect {

    private FBFormItem item;
    private Widget widget;
    private PopupPanel parent;
    
    private final String name;
    private final boolean hasSubMenu;
    
    /**
     * @param name Name of the effect for the drop menu
     * @param hasSubMenu if this effect, on selection, opens a new popup menu
     */
    public FBFormEffect(String name, boolean hasSubMenu) {
        this.name = name;
        this.hasSubMenu = hasSubMenu;
    }

    public String getName() {
        return name;
    }
    
    public void apply(FBFormItem item, PopupPanel panel) {
        item.addEffect(this);
        setParent(panel);
        this.item = item;
        if (!hasSubMenu) {
            createStyles();
        }
    }
    
    public void remove(FBFormItem item) {
        item.removeEffect(this);
    }
    
    /**
     * Classes that implement {@link FBFormEffect} must write
     * this method, and make all actions of the effect in it
     */
    protected abstract void createStyles();

    /**
     * If your instance of {@link FBFormEffect} happens to need
     * a popup menu, override this method and create that menu
     * here
     * @return the popup needed for the given effect
     */
    public PopupPanel createPanel() {
        return null;
    }
    
    /**
     * Use this method to obtain the {@link FBFormItem} 
     * this {@link FBFormEffect} is related to 
     * @return the related {@link FBFormItem}
     */
    protected FBFormItem getItem() {
        return this.item;
    }
    
    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public PopupPanel getParent() {
        return parent;
    }

    public void setParent(PopupPanel parent) {
        this.parent = parent;
    }

    /**
     * Use this method to obtain the UI GWT component
     * represented by the {@link FBFormItem} this 
     * {@link FBFormEffect} is related to
     * @param widget the UI GWT component of the related {@link FBFormItem}
     */
    public Widget getWidget() {
        return widget;
    }
    
    /**
     * Override this method as a safety switch.
     * If your {@link FBFormEffect} only works for 
     * a given set of components, or for components
     * with a given characteristic, and you want to check
     * it, you can override this method to say when it the 
     * makes sense to have this {@link FBFormEffect} related to
     * a given {@link FBFormItem}
     * 
     * Default implementation always returns true
     * 
     * @param item the related {@link FBFormItem} to validate 
     * @return wether its a valid effect for the given item or not
     */
    public boolean isValidForItem(FBFormItem item) {
        return item != null;
    }
}
