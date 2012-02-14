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
package org.jbpm.formbuilder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
public class FormBuilderView extends AbsolutePanel {

    private static FBUiBinder uiBinder = GWT.create(FBUiBinder.class);

    interface FBUiBinder extends UiBinder<Widget, FormBuilderView> {
    }
    
    @UiField(provided=true) ScrollPanel treeView;
    @UiField(provided=true) SimplePanel optionsView;
    @UiField(provided=true) ScrollPanel menuView;
    @UiField(provided=true) ScrollPanel editionView;
    @UiField(provided=true) ScrollPanel layoutView;
    @UiField(provided=true) Panel toolBarView;
    @UiField(provided=true) Panel ioAssociationView;
    @UiField(provided=true) Panel notificationsView;

    protected final void checkBinding() {
        if (timeToBind()) {
            Widget widget = uiBinder.createAndBindUi(this);
            setSize("100%", "100%");
            widget.setSize("100%", "100%");
            add(widget);
            int fullHeight = Window.getClientHeight();
            String height = "" + (fullHeight - 80) + "px";
            String smallerHeight = "" + (fullHeight - 105) + "px";
            treeView.setHeight(height);
            menuView.setHeight(height);
            editionView.setHeight(height);
            ioAssociationView.setHeight(height);
            layoutView.setHeight(smallerHeight);
        }
    }

    protected boolean timeToBind() {
        return getWidgetCount() == 0 &&
            treeView != null && optionsView != null &&
            menuView != null && editionView != null &&
            layoutView != null && toolBarView != null &&
            ioAssociationView != null && notificationsView != null;
    }

    public void setMenuView(ScrollPanel menuView) {
        this.menuView = menuView;
        checkBinding();
    }
    
    public void setEditionView(ScrollPanel editionView) {
        this.editionView = editionView;
        checkBinding();
    }

    public void setLayoutView(ScrollPanel layoutView) {
        this.layoutView = layoutView;
        checkBinding();
    }
    
    public void setOptionsView(SimplePanel optionsView) {
        this.optionsView = optionsView;
        checkBinding();
    }
    
    public void setIoAssociationView(Panel ioAssociationView) {
        this.ioAssociationView = ioAssociationView;
        checkBinding();
    }
    
    public void setToolBarView(Panel toolBarView) {
        this.toolBarView = toolBarView;
        checkBinding();
    }
    
    public void setNotificationsView(Panel notificationsView) {
        this.notificationsView = notificationsView;
        checkBinding();
    }
    
    public void setTreeView(ScrollPanel treeView) {
        this.treeView = treeView;
        checkBinding();
    }
    
    public void toggleNotifications(boolean visibleNotifications) {
        int fullHeight = Window.getClientHeight();
        if (visibleNotifications) {
            String sheight = "" + (fullHeight - 275) + "px";
            String smallerHeight = "" + (fullHeight - 300) + "px";
            this.notificationsView.setHeight("225px");
            Document.get().getElementById("mainRowWrapper").setPropertyString("height", sheight);
            Document.get().getElementById("notificationsRowWrapper").setPropertyString("height", "225px");
            treeView.setHeight(sheight);
            menuView.setHeight(sheight);
            editionView.setHeight(sheight);
            ioAssociationView.setHeight(sheight);
            layoutView.setHeight(smallerHeight);
            visibleNotifications = false;
        } else {
            this.notificationsView.setHeight("25px");
            String sheight = "" + (fullHeight - 80) + "px";
            String smallerHeight = "" + (fullHeight - 105) + "px";
            Document.get().getElementById("mainRowWrapper").setPropertyString("height", sheight);
            Document.get().getElementById("notificationsRowWrapper").setPropertyString("height", "25px");
            treeView.setHeight(sheight);
            menuView.setHeight(sheight);
            editionView.setHeight(sheight);
            ioAssociationView.setHeight(sheight);
            layoutView.setHeight(smallerHeight);
            visibleNotifications = true;
        }
    }
}
