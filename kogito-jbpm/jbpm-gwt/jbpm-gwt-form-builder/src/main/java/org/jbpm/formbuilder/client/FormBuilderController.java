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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.client.form.FormEncodingClientFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.bus.ui.NotificationsVisibleEvent;
import org.jbpm.formbuilder.client.bus.ui.NotificationsVisibleHandler;
import org.jbpm.formbuilder.client.bus.ui.RepresentationFactoryPopulatedEvent;
import org.jbpm.formbuilder.client.bus.ui.RepresentationFactoryPopulatedHandler;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewEvent;
import org.jbpm.formbuilder.client.bus.ui.UserIsLoggedOutEvent;
import org.jbpm.formbuilder.client.bus.ui.UserIsLoggedOutHandler;
import org.jbpm.formbuilder.client.command.DisposeDropController;
import org.jbpm.formbuilder.client.edition.EditionViewImpl;
import org.jbpm.formbuilder.client.layout.LayoutViewImpl;
import org.jbpm.formbuilder.client.menu.AnimatedMenuViewImpl;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.notification.CompactNotificationsViewImpl;
import org.jbpm.formbuilder.client.options.OptionsViewImpl;
import org.jbpm.formbuilder.client.tasks.IoAssociationViewImpl;
import org.jbpm.formbuilder.client.toolbar.ToolBarViewImpl;
import org.jbpm.formbuilder.client.tree.TreeViewImpl;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class FormBuilderController {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final FormBuilderService model = FormBuilderGlobals.getInstance().getService();
    private final FormBuilderView view;
    
    private final FormExporter formExporter;
    
    /**
     * Initiates gwt-dnd drag controller and sub views and presenters
     * @param fbModel
     * @param fbView
     */
    public FormBuilderController(final RootPanel rootPanel, FormBuilderView fbView) {
        super();
        this.view = fbView;
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable exception) {
                bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ErrorInTheUI(), exception));
            }
        });
        bus.addHandler(UserIsLoggedOutEvent.TYPE, new UserIsLoggedOutHandler() {
            @Override
            public void onEvent(UserIsLoggedOutEvent event) {
                Window.alert("User is login timeout");
                Window.Location.reload();
            }
        });
        new HistoryPresenter();
        RoleUtils.getInstance();
        FormEncodingFactory.register(FormEncodingClientFactory.getEncoder(), FormEncodingClientFactory.getDecoder());
        PickupDragController dragController = new PickupDragController(view, true);
        dragController.registerDropController(new DisposeDropController(view));
        CommonGlobals.getInstance().registerDragController(dragController);
        
        this.formExporter = new FormExporter();
        this.formExporter.start();
        
        view.setNotificationsView(new CompactNotificationsViewImpl());
        view.setMenuView(new AnimatedMenuViewImpl());
        view.setEditionView(new EditionViewImpl());
        view.setTreeView(new TreeViewImpl());
        view.setLayoutView(new LayoutViewImpl());
        view.setOptionsView(new OptionsViewImpl());
        view.setIoAssociationView(new IoAssociationViewImpl());
        view.setToolBarView(new ToolBarViewImpl());
        bus.addHandler(RepresentationFactoryPopulatedEvent.TYPE, new RepresentationFactoryPopulatedHandler() {
            @Override
            public void onEvent(RepresentationFactoryPopulatedEvent event) {
                try {
                    model.getMenuItems();
                    model.getMenuOptions();
                } catch (FormBuilderException e) {
                    //implementation never throws this
                }
                List<GwtEvent<?>> events = setDataPanel(rootPanel);
                setViewPanel(rootPanel);
                //events are fired deferred since they might need that ui components are already attached
                fireEvents(events);
            }
        });
        bus.addHandler(NotificationsVisibleEvent.TYPE, new NotificationsVisibleHandler() {
            @Override
            public void onEvent(NotificationsVisibleEvent event) {
                view.toggleNotifications(event.isVisible());
            }
        });
        populateRepresentationFactory(model);
    }

    private void fireEvents(List<GwtEvent<?>> events) {
        if (events != null) {
            for (GwtEvent<?> event : events) {
                bus.fireEvent(event);
            }
        }
    }
    
    private List<GwtEvent<?>> setDataPanel(RootPanel rootPanel) {
        List<GwtEvent<?>> retval = new ArrayList<GwtEvent<?>>();
        String innerHTML = rootPanel.getElement().getInnerHTML();
        if (innerHTML != null && !"".equals(innerHTML)) {
            try {
                JsonLoadInput input = JsonLoadInput.parse(innerHTML);
                if (input != null) {
                    if (input.getForm() != null) {
                        if (input.getPackage() != null && !"".equals(input.getPackage())) {
                            model.setPackageName(input.getPackage());
                        }
                        retval.add(new UpdateFormViewEvent(input.getForm()));
                        if (input.getTask() == null && hasTaskAssigned(input.getForm())) {
                            model.selectIoAssociation(input.getPackage(), input.getForm().getProcessName(), input.getForm().getTaskId());
                        }
                    }
                    retval.add(new EmbededIOReferenceEvent(input.getTask(), input.getProfile()));
                }
            } catch (Exception e) {
                GWT.log("Problem parsing init content", e);
            }
        }
        return retval;
    }

    private boolean hasTaskAssigned(FormRepresentation form) {
        boolean notNull = form != null && form.getProcessName() != null && form.getTaskId() != null;
        return notNull && !"".equals(form.getProcessName().trim()) && !"".equals(form.getTaskId().trim());
    }
    
    private void setViewPanel(RootPanel rootPanel) {
        rootPanel.getElement().setInnerHTML("");
        rootPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
        rootPanel.add(view);
    }
    
    private void populateRepresentationFactory(FormBuilderService model) {
        try {
            model.populateRepresentationFactory();
        } catch (FormBuilderException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ProblemLoadingRepresentationFactory(), e));
        }
    }
}
