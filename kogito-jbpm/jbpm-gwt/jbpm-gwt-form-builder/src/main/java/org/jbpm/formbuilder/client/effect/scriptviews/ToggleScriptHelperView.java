package org.jbpm.formbuilder.client.effect.scriptviews;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scripthandlers.ToggleScriptHelper;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class ToggleScriptHelperView extends FlexTable {

	private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
	private final TextBox idField = new TextBox();
	private final ListBox actionOnEvent = new ListBox();
	private final ListBox hidingStrategy = new ListBox();
	
	public ToggleScriptHelperView(ToggleScriptHelper helper) {
		populateActionOnEventList();
		populateHidingStrategyList();
		readDataFrom(helper);
        setWidget(0, 0, new Label(i18n.ToggleScriptHelperIdField()));
        setWidget(0, 1, idField);
        setWidget(1, 0, new Label(i18n.ToggleScriptHelperActionOnEvent()));
        setWidget(1, 1, actionOnEvent);
        setWidget(2, 0, new Label(i18n.ToggleScriptHelperHidingStrategy()));
        setWidget(2, 1, hidingStrategy);
	}
	
	private void populateActionOnEventList() {
		actionOnEvent.addItem(i18n.ToggleScriptHelperToggleAction(), ToggleScriptHelper.TOGGLE);
		actionOnEvent.addItem(i18n.ToggleScriptHelperHideAction(), ToggleScriptHelper.HIDE);
		actionOnEvent.addItem(i18n.ToggleScriptHelperShowAction(), ToggleScriptHelper.SHOW);
		actionOnEvent.setSelectedIndex(0);
	}
	
	private void populateHidingStrategyList() {
		hidingStrategy.addItem(i18n.ToggleScriptHelperHiddenStrategy(), ToggleScriptHelper.HIDING_STRATEGY_HIDDEN);
		hidingStrategy.addItem(i18n.ToggleScriptHelperCollapseStrategy(), ToggleScriptHelper.HIDING_STRATEGY_COLLAPSE);
		hidingStrategy.setSelectedIndex(0);
	}

	public void writeDataTo(ToggleScriptHelper helper) {
		helper.setActionOnEvent(this.actionOnEvent.getValue(this.actionOnEvent.getSelectedIndex()));
		helper.setHidingStrategy(this.hidingStrategy.getValue(this.hidingStrategy.getSelectedIndex()));
		helper.setIdField(this.idField.getValue());
	}

	public void readDataFrom(ToggleScriptHelper helper) {
		for (int index = 0; index < this.hidingStrategy.getItemCount(); index++) {
			if (this.hidingStrategy.getValue(index).equals(helper.getHidingStrategy())) {
				this.hidingStrategy.setSelectedIndex(index);
				break;
			}
		}
		for (int index = 0; index < this.actionOnEvent.getItemCount(); index++) {
			if (this.actionOnEvent.getValue(index).equals(helper.getActionOnEvent())) {
				this.actionOnEvent.setSelectedIndex(index);
				break;
			}
		}
		this.idField.setValue(helper.getIdField());
	}
}
