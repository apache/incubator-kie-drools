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
package org.jbpm.formbuilder.client.tree;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.FormItemSelectionEvent;
import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.menu.EffectsPopupPanel;
import org.jbpm.formapi.common.handler.RightClickEvent;
import org.jbpm.formapi.common.handler.RightClickHandler;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Representation of a {@link FBFormItem} on the tree view.
 * Has either a folder or a leaf and a name
 */
public class TreeElement extends FocusPanel {

    private final FBFormItem item;
    private final Image img;
    private final Label itemName;
    
    private List<RightClickHandler> rclickHandlers = new ArrayList<RightClickHandler>();
    
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final HorizontalPanel panel = new HorizontalPanel();
    
    public TreeElement() {
        panel.setSpacing(0);
        panel.setBorderWidth(0);
        this.item = null;
        this.img = new Image(FormBuilderResources.INSTANCE.treeFolder());
        this.itemName = new HTML("<strong>form</strong>");
        panel.add(this.img);
        panel.add(this.itemName);
        add(panel);
    }
    
    public TreeElement(FBFormItem formItem) {
        panel.setSpacing(0);
        panel.setBorderWidth(0);
        this.item = formItem;
        if (formItem != null) {
            this.itemName = new Label(formItem.getRepresentation().getTypeId());
            if (formItem instanceof FBCompositeItem) {
                this.img = new Image(FormBuilderResources.INSTANCE.treeFolder());
            } else {
                this.img = new Image(FormBuilderResources.INSTANCE.treeLeaf());
            }
        } else {
            throw new IllegalArgumentException(
                FormBuilderGlobals.getInstance().getI18n().FormItemShouldntBeNull());
        }
        panel.add(this.img);
        panel.add(this.itemName);
        sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (item != null) {
                    bus.fireEvent(new FormItemSelectionEvent(item, true));
                }
            }
        });
        add(panel);
        addRightClickHandler(new RightClickHandler() {
            @Override
            public void onRightClick(RightClickEvent event) {
                if (item != null) {
                    EffectsPopupPanel popupPanel = new EffectsPopupPanel(item, true);
                    if (item.getFormEffects() != null && !item.getFormEffects().isEmpty()) {
                        popupPanel.setPopupPosition(event.getX(), event.getY());
                        popupPanel.show();
                    }
                } 
            }
        });
    }
    
    public HandlerRegistration addRightClickHandler(final RightClickHandler handler) {
        HandlerRegistration reg = new HandlerRegistration() {
            @Override
            public void removeHandler() {
                TreeElement.this.rclickHandlers.remove(handler);
            }
        };
        this.rclickHandlers.add(handler);
        return reg;
    }
    
    @Override
    public void onBrowserEvent(Event event) {
      event.stopPropagation();
      event.preventDefault();
      switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEUP:
          if (DOM.eventGetButton(event) == Event.BUTTON_LEFT) {
              ClickEvent cevent = new ClickEvent() {
                  @Override
                  public Object getSource() {
                      return item;
                  }
              };
              cevent.setNativeEvent(event);
              fireEvent(cevent);
            super.onBrowserEvent(event);
          } else if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
            for (RightClickHandler handler : rclickHandlers) {
                handler.onRightClick(new RightClickEvent(event));
            }
          }
          break;
        case Event.ONDBLCLICK:
          break;
        case Event.ONCONTEXTMENU:
          break;
        default:
          break; // Do nothing
      }//end switch
    }
    
    public boolean represents(FBFormItem item) {
        return this.item != null && this.item == item;
    }
    
    public boolean represents(FBCompositeItem item) {
        return this.item != null && this.item == item;
    }
}
