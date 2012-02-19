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
package org.jbpm.formbuilder.client.effect.scripthandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HeaderViewPanel extends SimplePanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    private final Grid headerView = new Grid(0, 3);
    private final Label noHeaders = new Label(i18n.NoHeadersLoadedLabel());
    
    public HeaderViewPanel() {
        add(noHeaders);
    }
    
    public void addHeaderRow(String keyValue, String valueValue) {
        headerView.resizeRows(headerView.getRowCount() + 1);
        checkTable();
        final int lastRow = headerView.getRowCount() - 1;
        final TextBox headerName = new TextBox();
        headerName.setValue(keyValue);
        final TextBox headerValue = new TextBox();
        headerValue.setValue(valueValue);
        headerView.setWidget(lastRow, 0, headerName);
        headerView.setWidget(lastRow, 1, headerValue);
        headerView.setWidget(lastRow, 2, new Button(i18n.RemoveButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int rowToRemove = -1;
                for (int index = 0; index < headerView.getRowCount(); index++) {
                    Widget widget = headerView.getWidget(index, 0);
                    if (widget != null && widget.equals(headerName)) {
                        rowToRemove = index;
                        break;
                    }
                }
                headerView.removeRow(rowToRemove);
                checkTable();
            }
        }));
    }

    private void checkTable() {
        if (headerView.getRowCount() > 0) {
            this.remove(noHeaders);
            this.add(headerView);
        } else {
            this.remove(headerView);
            this.add(noHeaders);
        }
    }

    public Set<Map.Entry<String, String>> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        for (int index = 0; index < headerView.getRowCount(); index++) {
            TextBox headerName = (TextBox) headerView.getWidget(index, 0);
            TextBox headerValue = (TextBox) headerView.getWidget(index, 1);
            headers.put(headerName.getValue(), headerValue.getValue());
        }
        return headers.entrySet();
    }
}
