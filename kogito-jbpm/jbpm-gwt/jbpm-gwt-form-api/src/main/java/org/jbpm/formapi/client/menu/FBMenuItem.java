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
package org.jbpm.formapi.client.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.shared.api.FBScript;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for all menu items.
 * It's in charge of creating a {@link FBFormItem}
 */
public abstract class FBMenuItem extends AbsolutePanel implements HasDragHandle {
    
    private FocusPanel shim = new FocusPanel();
    private final List<FBFormEffect> formEffects;
    private final List<String> allowedEvents = new ArrayList<String>();
    
    public FBMenuItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public FBMenuItem(List<FBFormEffect> formEffects) {
        this.formEffects = formEffects;
        repaint();
    }
    
    public void repaint() {
        if (getWidgetCount() > 0) {
            remove(0);
        }
        Panel panel = new HorizontalPanel();
        panel.add(new Image(getIconUrl().getURL()));
        panel.add(new HTML("&nbsp;"));
        panel.add(getDescription());
        add(panel);
    }
    
    public List<FBFormEffect> getFormEffects() {
        return formEffects;
    }
    
    public String getItemId() {
        return getClass().getName();
    }
    
    @Override
    public Widget getDragHandle() {
        if (!shim.isAttached() && isAttached()) {
            shim.setPixelSize(getOffsetWidth(), getOffsetHeight());
            add(shim, 0, 0);
        }
        return shim;
    }
    
    /**
     * Let shim size match our size.
     * 
     * @param width the desired pixel width
     * @param height the desired pixel height
     */
    @Override
    public void setPixelSize(int width, int height) {
      super.setPixelSize(width, height);
      shim.setPixelSize(width, height);
    }

    /**
     * Let shim size match our size.
     * 
     * @param width the desired CSS width
     * @param height the desired CSS height
     */
    @Override
    public void setSize(String width, String height) {
      super.setSize(width, height);
      shim.setSize(width, height);
    }

    /**
     * Adjust the shim size and attach once our widget dimensions are known.
     */
    @Override
    protected void onLoad() {
      super.onLoad();
      int height = getOffsetHeight();
      int width = getOffsetWidth();
      if (height == 0 && width == 0) {
          height = 18;
          width = 210;
      }
      shim.setPixelSize(width, height);
      add(shim, 0, 0);
    }
    
    /**
     * Remove the shim to allow the widget to size itself when reattached.
     */
    @Override
    protected void onUnload() {
      super.onUnload();
      shim.removeFromParent();
    }
    
    public void addEffect(FBFormEffect effect) {
        this.formEffects.add(effect);
    }

    /**
     * This method returns an icon that visually represents 
     * the UI component this menu item creates.
     * @return an icon
     */
    protected abstract ImageResource getIconUrl();
    
    /**
     * This method returns a description that represents
     * the UI component this menu item creates.
     * @return a description label
     */
    public abstract Label getDescription();

    protected <T extends FBMenuItem> T clone(T item) {
        if (getFormEffects() != null) {
            for (FBFormEffect effect : getFormEffects()) {
                item.addEffect(effect);
            }
        }
        if (getAllowedEvents() != null) {
            for (String allowedEvent : getAllowedEvents()) {
                item.addAllowedEvent(allowedEvent);
            }
        }
        return item;
    }
    
    protected <T extends FBFormItem> T build(T item) {
        if (getFormEffects() != null) {
            for (FBFormEffect effect : getFormEffects()) {
                item.addEffect(effect);
            }
        }
        if (getAllowedEvents() != null) {
            item.setEventActions(getAllowedEventsAsMap());
        }
        return item;
    }
    
    /**
     * This method is like {@link #clone()}, but returns
     * the proper instance and forces implementation
     * @return a copy of this {@link FBMenuItem} object
     */
    public abstract FBMenuItem cloneWidget();
    
    /**
     * Builds a {@link FBFormItem}. Remember to assign
     * all {@link FBFormEffect} it will need. See
     * {@link #getFormEffects()} and {@link #FBMenuItem(List)}
     * to see how to assign and get these to and from your instance.
     * @return a {@link FBFormItem} instance.
     */
    public abstract FBFormItem buildWidget();

    public void addAllowedEvent(String allowedEventName) {
        allowedEvents.add(allowedEventName);
    }
    
    public List<String> getAllowedEvents() {
        return allowedEvents;
    }
    
    public Map<String, FBScript> getAllowedEventsAsMap() {
        Map<String, FBScript> map = new HashMap<String, FBScript>();
        for (String evtName : allowedEvents) {
            map.put(evtName, null);
        }
        return map;
    }
}
