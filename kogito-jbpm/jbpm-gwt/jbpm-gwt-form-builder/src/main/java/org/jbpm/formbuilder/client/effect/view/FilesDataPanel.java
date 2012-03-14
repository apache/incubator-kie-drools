package org.jbpm.formbuilder.client.effect.view;

import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.common.panels.ConfirmDialog;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilesDataPanel extends ScrollPanel {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    private boolean isEmpty = true;
    
    FlexTable table = new FlexTable();
    
    public FilesDataPanel() {
        setHeight("200px");
        setWidget(table);
    }
    
    public void setFiles(List<String> urls) {
        table.clear();
        if (urls != null && !urls.isEmpty()) {
            isEmpty = false;
            for (int row = 0; row < urls.size(); row++) {
                final String url = urls.get(row);
                final FocusPanel labelPanel = createLabelPanel(url);
                table.setWidget(row, 0, labelPanel);
                Element rowElem = table.getRowFormatter().getElement(row);
                table.setWidget(row, 1, createDeleteButton(rowElem, url));
            }
        } else {
            table.setWidget(0, 0, new Label(i18n.NoFilesFound()));
        }
    }

    private FocusPanel createLabelPanel(final String url) {
        final FocusPanel panel = new FocusPanel();
        panel.setStyleName("fbFilesDataPanel");
        panel.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (panel.getStyleName().equals("fbFilesDataPanelSelected")) {
                    panel.setStyleName("fbFilesDataPanel");
                    setSelection(null);
                } else {
                    deselectAllLabels();
                    panel.setStyleName("fbFilesDataPanelSelected");
                    setSelection(url);
                }
            }
        });
        panel.setWidget(new Label(toFileName(url)));
        return panel;
    }
    
    private void deselectAllLabels() {
        for (int row = 0; row < table.getRowCount(); row++) {
            Widget widget = table.getWidget(row, 0);
            if (widget != null && widget.getStyleName().equals("fbFilesDataPanelSelected")) {
                widget.setStyleName("fbFilesDataPanel");
            }
        }
    }
    
    private Button createDeleteButton(final Element rowElem, final String url) {
        return new Button(i18n.RemoveButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ConfirmDialog dialog = new ConfirmDialog(i18n.WarningDeleteFile());
                dialog.addOkButtonHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        try {
                            server.deleteFile(toFileName(url));
                            RowFormatter formatter = table.getRowFormatter();
                            int rowNumber = 0;
                            for (; rowNumber < table.getRowCount(); rowNumber++) {
                                if (formatter.getElement(rowNumber).equals(rowElem)) {
                                    break;
                                }
                            }
                            table.removeRow(rowNumber);
                        } catch (FormBuilderException e) {
                            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.Error(e.getMessage()), e));
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private String selection = null;
    
    public String getSelection() {
        return selection;
    }
    
    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String toFileName(String url) {
        return url.substring(url.lastIndexOf('/'));
    }
    
    public void addNewFile(String url) {
        if (!contains(url)) {
            final FocusPanel labelPanel = createLabelPanel(url);
            if (isEmpty) {
                table.clear();
            }
            int row = table.getRowCount();
            table.setWidget(row, 0, labelPanel);
            Element rowElem = table.getRowFormatter().getElement(row);
            table.setWidget(row, 1, createDeleteButton(rowElem, url));
            labelPanel.setFocus(true);
            setSelection(url);
        }
    }
    
    private boolean contains(String url) {
        String fileName = toFileName(url);
        for (int row = 0; row < table.getRowCount(); row++) {
            FocusPanel labelPanel = (FocusPanel) table.getWidget(row, 0);
            if (labelPanel != null && labelPanel.getWidget() != null) {
                Label label = (Label) labelPanel.getWidget();
                if (label.getText().equals(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
