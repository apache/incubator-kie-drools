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
package org.jbpm.formbuilder.client.notification;

import java.util.Collection;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.ui.NotificationsVisibleEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompactNotificationsViewImpl extends SimplePanel implements NotificationsView {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private final VerticalPanel panel = new VerticalPanel();
    private final ScrollPanel scroll = new ScrollPanel(panel);
    private final Image arrowImage = new Image();
    
    public CompactNotificationsViewImpl() {
        Button show = new Button();
        DOM.setStyleAttribute(show.getElement(), "align", "left");
        DOM.setStyleAttribute(show.getElement(), "textAlign", "left");
        show.setWidth("100%");
        show.setStylePrimaryName("fbStackPanel");
        show.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scroll.setVisible(!scroll.isVisible());
                ImageResource down = FormBuilderResources.INSTANCE.arrowDown();
                ImageResource up = FormBuilderResources.INSTANCE.arrowUp();
                arrowImage.setResource(scroll.isVisible() ? down : up);
                bus.fireEvent(new NotificationsVisibleEvent(scroll.isVisible()));
            }
        });
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        scroll.addStyleName("notificationsView");
        scroll.setSize("100%", "200px");
        arrowImage.setResource(FormBuilderResources.INSTANCE.arrowDown());
        VerticalPanel vPanel = new VerticalPanel();
        vPanel.setWidth("100%");
        Grid table = getButtonText();
        show.setHTML(new SafeHtmlBuilder().appendHtmlConstant(table.toString()).toSafeHtml());
        scroll.setVisible(false);
        vPanel.add(show);
        vPanel.add(scroll);
        setWidget(vPanel);
        new NotificationsPresenter(this);
    }

    private Grid getButtonText() {
        Grid buttonText = new Grid(1, 2);
        buttonText.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        buttonText.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        buttonText.setWidget(0, 0, new HTML("<strong>" + i18n.Notifications() + "</strong>"));
        buttonText.setWidth("100%");
        buttonText.setBorderWidth(0);
        buttonText.setCellPadding(0);
        buttonText.setCellSpacing(0);
        buttonText.setWidget(0, 1, arrowImage);
        return buttonText;
    }
    
    @Override
    public String getColorCss(String name) {
        String colorCss = "greenNotification";
        if ("WARN".equals(name)) {
            colorCss = "orangeNotification";
        } else if ("ERROR".equals(name)) {
            colorCss = "redNotification";
        }
        return colorCss;
    }

    @Override
    public void append(String colorCss, String message, Throwable error) {
        HTML html = new HTML();
        if (colorCss != null) {
            html.setStyleName(colorCss);
        }
        StringBuilder msg = new StringBuilder(message).append("<br/>");
        while (error != null) {
            msg.append(stringStackTrace(error));
            if (error instanceof UmbrellaException) {
                Collection<Throwable> causes = ((UmbrellaException) error).getCauses();
                if (causes != null) {
                    for (Throwable cause : causes) {
                        msg.append(stringStackTrace(cause));
                    }
                }
            } 
            if (error.getCause() != null && !error.equals(error.getCause())) {
                error = error.getCause();
                msg.append(i18n.CausedBy());
            } else {
                error = null;
            }
        }
        html.setHTML(msg.toString());
        panel.add(html);
        if (!scroll.isVisible()) {
            scroll.setVisible(true);
        }
    }

    private String stringStackTrace(Throwable error) {
        StringBuilder msg = new StringBuilder();
        msg.append(error.getClass().getName()).append(": ").append(error.getLocalizedMessage()).append("<br/>");
        StackTraceElement[] trace = error.getStackTrace();
        for (int index = 0; trace != null && index < trace.length; index++) {
            msg.append(i18n.StackTraceLine(trace[index].getClassName(), 
                    trace[index].getMethodName(), 
                    trace[index].getFileName(), 
                    String.valueOf(trace[index].getLineNumber()))).
                append("<br/>");
        }
        return msg.toString();
    }
}
