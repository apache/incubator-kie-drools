package org.jbpm.formbuilder.client.effect.scriptviews;

import java.util.Map;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scripthandlers.HeaderViewPanel;
import org.jbpm.formbuilder.client.effect.scripthandlers.RestServiceScriptHelper;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class RestServiceScriptHelperView extends FlexTable {

    private final TextBox url = new TextBox();
    private final ListBox method = new ListBox();
    private final ListBox resultStatus = new ListBox();
    private final TextBox resultXPath = new TextBox();
    private final TextBox exportVariableName = new TextBox();
    private final ListBox responseLanguage = new ListBox();
    
    private final HeaderViewPanel headerViewPanel = new HeaderViewPanel();

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

	public RestServiceScriptHelperView(RestServiceScriptHelper helper) {
		populateMethodList();
		populateResultStatusList();
		populateResponseLanguageList();
		readDataFrom(helper);
        setWidget(0, 0, new Label(i18n.RestServiceScriptHelperUrl()));
        setWidget(0, 1, url);
        setWidget(1, 0, new Label(i18n.RestServiceScriptHelperMethod()));
        setWidget(1, 1, method);
        setWidget(2, 0, new Label(i18n.RestServiceScriptHelperResultStatus()));
        setWidget(2, 1, resultStatus);
        setWidget(3, 0, new Label(i18n.RestServiceScriptHelperResultPath()));
        setWidget(3, 1, resultXPath);
        setWidget(4, 0, new Label(i18n.RestServiceScriptHelperExportVariable()));
        setWidget(4, 1, exportVariableName);
        setWidget(5, 0, new Label(i18n.RestServiceScriptHelperResponseLanguage()));
        setWidget(5, 1, responseLanguage);
        setWidget(6, 0, new Label(i18n.RestServiceScriptHelperSendHeaders()));
        setWidget(6, 1, new Button(i18n.RestServiceScriptHelperAddHeader(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                headerViewPanel.addHeaderRow("", "");
            }
        }));
        setWidget(7, 0, headerViewPanel);
        getFlexCellFormatter().setColSpan(7, 0, 2);
	}

    private void populateResponseLanguageList() {
        responseLanguage.addItem("xml");
        responseLanguage.addItem("json");
    }
    
    private void populateResultStatusList() {
        resultStatus.addItem("200 - OK", "200");
        resultStatus.addItem("201 - Created", "201");
        resultStatus.addItem("404 - Not found", "404");
        resultStatus.addItem("500 - Server error", "500");
    }

    private void populateMethodList() {
        method.addItem("GET");
        method.addItem("POST");
        method.addItem("PUT");
        method.addItem("DELETE");
    }

    public void readDataFrom(RestServiceScriptHelper helper) {
    	this.url.setValue(helper.getUrl());
        for (int index = 0; index < this.method.getItemCount(); index++) {
            if (this.method.getValue(index).equals(helper.getMethod())) {
                this.method.setSelectedIndex(index);
                break;
            }
        }
        for (int index = 0; index < this.resultStatus.getItemCount(); index++) {
            if (this.resultStatus.getValue(index).equals(helper.getResultStatus())) {
                this.resultStatus.setSelectedIndex(index);
                break;
            }
        }
        this.resultXPath.setValue(helper.getResultXPath());
        this.exportVariableName.setValue(helper.getExportVariableName());
        for (int index = 0; index < this.responseLanguage.getItemCount(); index++) {
            if (this.responseLanguage.getValue(index).equals(helper.getResponseLanguage())) {
                this.responseLanguage.setSelectedIndex(index);
                break;
            }
        }
        headerViewPanel.clear();
        if (helper.getHeaders() != null) {
            for (Map.Entry<String, String> entry : helper.getHeaders().entrySet()) {
                headerViewPanel.addHeaderRow(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public void writeDataTo(RestServiceScriptHelper helper) {
    	
    }
    
}
