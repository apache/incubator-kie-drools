package org.jbpm.formbuilder.client.effect.scriptviews;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scripthandlers.HeaderViewPanel;
import org.jbpm.formbuilder.client.effect.scripthandlers.PopulateComboBoxScriptHelper;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class PopulateComboBoxScriptHelperView extends FlexTable {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private final TextBox url = new TextBox();
    private final ListBox method = new ListBox();
    private final ListBox resultStatus = new ListBox();
    private final ListBox responseLanguage = new ListBox();
    private final TextBox resultXPath = new TextBox();
    private final TextBox subPathForKeys = new TextBox();
    private final TextBox subPathForValues = new TextBox();
    private final TextBox checkBoxId = new TextBox();

    private final HeaderViewPanel headerViewPanel = new HeaderViewPanel();
    
	public PopulateComboBoxScriptHelperView(PopulateComboBoxScriptHelper helper) {
		populateMethodList();
		populateResultStatusList();
		populateResponseLanguageList();
        setWidget(0, 0, new Label(i18n.PopulateComboBoxScriptHelperUrl()));
        setWidget(0, 1, url);
        setWidget(1, 0, new Label(i18n.PopulateComboBoxScriptHelperMethod()));
        setWidget(1, 1, method);
        setWidget(2, 0, new Label(i18n.PopulateComboBoxScriptHelperResultStatus()));
        setWidget(2, 1, resultStatus);
        setWidget(3, 0, new Label(i18n.PopulateComboBoxScriptHelperResponseLanguage()));
        setWidget(3, 1, responseLanguage);
        setWidget(4, 0, new Label(i18n.PopulateComboBoxScriptHelperResultPath()));
        setWidget(4, 1, resultXPath);
        setWidget(5, 0, new Label(i18n.PopulateComboBoxScriptHelperSubPathForKeys()));
        setWidget(5, 1, subPathForKeys);
        setWidget(6, 0, new Label(i18n.PopulateComboBoxScriptHelperSubPathForValues()));
        setWidget(6, 1, subPathForValues);
        setWidget(7, 0, new Label(i18n.PopulateComboBoxScriptHelperSendHeaders()));
        setWidget(7, 1, new Button(i18n.PopulateComboBoxScriptHelperAddHeader(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                headerViewPanel.addHeaderRow("", "");
            }
        }));
        setWidget(8, 0, headerViewPanel);
        getFlexCellFormatter().setColSpan(8, 0, 2);
        setWidget(9, 0, new Label(i18n.PopulateComboBoxScriptHelperCheckBoxId()));
        setWidget(9, 1, checkBoxId);
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
    
    public void readDataFrom(PopulateComboBoxScriptHelper helper) {
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
        this.subPathForKeys.setValue(helper.getSubPathForKeys());
        this.subPathForValues.setValue(helper.getSubPathForValues());
        this.checkBoxId.setValue(helper.getCheckBoxId());
    }

	public void writeDataTo(PopulateComboBoxScriptHelper helper) {
		helper.setUrl(this.url.getValue());
		helper.setMethod(this.method.getValue(this.method.getSelectedIndex()));
		helper.setResultStatus(this.resultStatus.getValue(this.resultStatus.getSelectedIndex()));
		helper.setResultXPath(this.resultXPath.getValue());
		helper.setResponseLanguage(this.responseLanguage.getValue(this.responseLanguage.getSelectedIndex()));
		Map<String, String> headersMap = new HashMap<String, String>();
		for (Map.Entry<String, String> header : this.headerViewPanel.getHeaders()) {
			headersMap.put(header.getKey(), header.getValue());
		}
		helper.setHeaders(headersMap);
		helper.setSubPathForKeys(this.subPathForKeys.getValue());
		helper.setSubPathForValues(this.subPathForValues.getValue());
		helper.setCheckBoxId(this.checkBoxId.getValue());
	}
}
