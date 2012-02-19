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
package org.jbpm.formapi.common.panels;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class MovablePanel extends AbsolutePanel implements HasDragHandle {

    private FocusPanel shim = new FocusPanel();
    
    private final Widget mainWidget;
    private final Widget oldContainer;
    
    public MovablePanel(Widget widget, Widget oldContainer) {
        super();
        this.mainWidget = widget;
        this.oldContainer = oldContainer;
        setPixelSize(this.mainWidget.getOffsetWidth(), this.mainWidget.getOffsetHeight());
        add(this.mainWidget, 0, 0);
    }

    public Widget getMainWidget() {
        return mainWidget;
    }
    
    public Widget getOldContainer() {
        return oldContainer;
    }
    
    @Override
    public Widget getDragHandle() {
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
      shim.setPixelSize(getOffsetWidth(), getOffsetHeight());
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
}
