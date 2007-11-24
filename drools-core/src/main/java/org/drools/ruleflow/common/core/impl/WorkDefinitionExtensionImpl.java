package org.drools.ruleflow.common.core.impl;

import org.drools.ruleflow.common.core.WorkDefinitionExtension;

public class WorkDefinitionExtensionImpl extends WorkDefinitionImpl implements WorkDefinitionExtension {
    
    private String displayName;
    private String explanationText;
    private String icon;
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExplanationText() {
        return explanationText;
    }
    
    public void setExplanationText(String explanationText) {
        this.explanationText = explanationText;
    }

    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
}
