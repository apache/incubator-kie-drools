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
package org.jbpm.formbuilder.client.tasks;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchFilterView extends VerticalPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final SimpleSearchView simple = new SimpleSearchView();
    private final AdvancedSearchView advanced = new AdvancedSearchView();
    
    private HorizontalPanel tooglePanel = new HorizontalPanel();
    private final Anchor toogleAnchor;
    
    public SearchFilterView() {
        setSize("100%", "90px");
        toogleAnchor = new Anchor(i18n.SimpleSearch());
        toogleAnchor.setHref("javascript:void(0);");
        toogleAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getWidget(0) == simple) {
                    remove(simple);
                    toogleAnchor.setText(i18n.SimpleSearch());
                    insert(advanced, getWidgetIndex(tooglePanel));
                } else {
                    remove(advanced);
                    toogleAnchor.setText(i18n.AdvancedSearch());
                    insert(simple, getWidgetIndex(tooglePanel));
                }
            }
        });
        tooglePanel.setWidth("100%");
        tooglePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        tooglePanel.add(toogleAnchor);
        add(advanced);
        add(tooglePanel);
    }

    public AdvancedSearchView getAdvancedView() {
        return advanced;
    }
    
    public SimpleSearchView getSimpleView() {
        return simple;
    }
}
