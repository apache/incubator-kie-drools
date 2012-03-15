package org.jbpm.formbuilder.client.effect.scripthandlers;

import com.google.gwt.user.client.ui.TextArea;

public class PlainTextScriptHelperView extends TextArea {

    public PlainTextScriptHelperView(PlainTextScriptHelper helper) {
        readDataFrom(helper);
        setCharacterWidth(50);
        setVisibleLines(15);    
    }

    public void writeDataTo(PlainTextScriptHelper helper) {
        helper.setScriptPanel(getValue());
    }
    
    public void readDataFrom(PlainTextScriptHelper helper) {
        setValue(helper.getScriptPanel());
    }
    
}
