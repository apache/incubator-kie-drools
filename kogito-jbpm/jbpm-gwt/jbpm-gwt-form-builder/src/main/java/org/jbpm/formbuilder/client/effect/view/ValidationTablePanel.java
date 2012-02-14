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
package org.jbpm.formbuilder.client.effect.view;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ValidationTablePanel extends VerticalPanel implements HasSelectionHandlers<FBValidationItem> {

    private List<SelectionHandler<FBValidationItem>> tableHandlers = new ArrayList<SelectionHandler<FBValidationItem>>();
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Grid validationsTable = new Grid(1,4);
    private final List<FBValidationItem> currentValidations = new ArrayList<FBValidationItem>();
    
    private FBValidationItem selectedValidation = null;
    
    public ValidationTablePanel() {
        validationsTable.setWidget(0, 0, new HTML("<strong>" + i18n.CurrentValidations() + "</strong>"));
        validationsTable.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (Widget widget : validationsTable) {
                    widget.removeStyleName("selectedValidationRow");
                }
                int row = getSelectedRow(event);
                if (row > 0) {
                    Widget selectedWidget = validationsTable.getWidget(row, 0);
                    if (selectedWidget.getStyleName().contains("selectedValidationRow")) {
                        selectedWidget.removeStyleName("selectedValidationRow");
                        setCurrentValidation(null);
                        fireSelectedValidation();
                    } else {
                        selectedWidget.addStyleName("selectedValidationRow");
                        setCurrentValidation(currentValidations.get(row - 1));
                        fireSelectedValidation();
                    }
                }
            }
        });
        add(validationsTable);
    }
    
    public void setCurrentValidation(FBValidationItem validation) {
        this.selectedValidation = validation;
    }
    
    @Override
    public HandlerRegistration addSelectionHandler(final SelectionHandler<FBValidationItem> handler) {
        HandlerRegistration handlerRegistration = new HandlerRegistration() {
            @Override
            public void removeHandler() {
                if (tableHandlers.contains(handler)) {
                    tableHandlers.remove(handler);
                }
            }
        };
        if (!tableHandlers.contains(handler)) {
            tableHandlers.add(handler);
        }
        return handlerRegistration;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void fireEvent(GwtEvent<?> event) {
        for (SelectionHandler<FBValidationItem> handler : tableHandlers) {
            handler.onSelection((SelectionEvent<FBValidationItem>) event);
        }
    }
    
    protected void fireSelectedValidation() {
        SelectionEvent.fire(this, this.selectedValidation);
    }
    
    public void addValidation(final FBValidationItem validation) {
        if (!currentValidations.contains(validation)) {
            int rowCount = validationsTable.getRowCount();
            validationsTable.resizeRows(rowCount + 1);
            validationsTable.setWidget(rowCount, 0, new Label(validation.getName()));
            final Image upLink = new Image(FormBuilderResources.INSTANCE.arrowUp());
            upLink.getElement().getStyle().setCursor(Cursor.POINTER);
            final Image downLink = new Image(FormBuilderResources.INSTANCE.arrowDown());
            downLink.getElement().getStyle().setCursor(Cursor.POINTER);
            final Image removeLink = new Image(FormBuilderResources.INSTANCE.removeSmallIcon());
            removeLink.getElement().getStyle().setCursor(Cursor.POINTER);
            removeLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    int row = currentValidations.indexOf(validation) + 1;
                    removeValidation(validation, row);
                }
            });
            upLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    moveSelectedValidation(true);
                    downLink.setVisible(currentValidations.indexOf(validation) > 0);
                    upLink.setVisible(currentValidations.indexOf(validation) < currentValidations.size());
                }
            });
            downLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    moveSelectedValidation(false);
                    downLink.setVisible(currentValidations.indexOf(validation) > 0);
                    upLink.setVisible(currentValidations.indexOf(validation) < currentValidations.size());
                }
            });
            validationsTable.setWidget(rowCount, 1, upLink);
            validationsTable.setWidget(rowCount, 2, downLink);
            validationsTable.setWidget(rowCount, 3, removeLink);
            currentValidations.add(validation);
            if (!isVisible()) {
                setVisible(true);
            }
        }
    }
    
    public int getSelectedRow(ClickEvent event) {
        return validationsTable.getCellForEvent(event).getRowIndex();
    }
    
    public void removeValidation(FBValidationItem validation, int row) {
        if (row >= 0) {
            currentValidations.remove(validation);
            validationsTable.removeRow(row);
        }
    }
    
    public void moveSelectedValidation(boolean up) {
        int index = currentValidations.indexOf(selectedValidation);
        if (index >= 0) {
            if (up && index + 1 < currentValidations.size()) {
                FBValidationItem supValidation = currentValidations.get(index + 1);
                currentValidations.set(index + 1, selectedValidation);
                currentValidations.set(index, supValidation);
            } else if (!up && index -1 > 0) {
                FBValidationItem subValidation = currentValidations.get(index - 1);
                currentValidations.set(index - 1, selectedValidation);
                currentValidations.set(index, subValidation);
            }
            moveValidationOnTable(index, up);
        }
    }

    private void moveValidationOnTable(int index, boolean up) {
        if (up && index + 2 < validationsTable.getRowCount()) {
            for (int i = 0; i < validationsTable.getColumnCount(); i++) {
                Widget move = validationsTable.getWidget(index + 1, i);
                Widget res = validationsTable.getWidget(index + 2, i);
                validationsTable.remove(move);
                validationsTable.remove(res);
                validationsTable.setWidget(index + 1, i, res);
                validationsTable.setWidget(index + 2, i, move);
            }
        } else if (!up && index > 0) {
            for (int i = 0; i < validationsTable.getColumnCount(); i++) {
                Widget move = validationsTable.getWidget(index + 1, i);
                Widget res = validationsTable.getWidget(index, i);
                validationsTable.remove(move);
                validationsTable.remove(res);
                validationsTable.setWidget(index + 1, i, res);
                validationsTable.setWidget(index, i, move);
            }
        }
    }
    
    public List<FBValidationItem> getCurrentValidations() {
        return currentValidations;
    }
}
