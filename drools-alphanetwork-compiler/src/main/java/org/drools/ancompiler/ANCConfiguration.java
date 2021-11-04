package org.drools.ancompiler;

public class ANCConfiguration {

    private boolean disableContextEntry = false;
    private boolean prettyPrint = false;
    private boolean enableModifyObject = true;

    public boolean getDisableContextEntry() {
        return disableContextEntry;
    }

    public void setDisableContextEntry(boolean disableContextEntry) {
        this.disableContextEntry = disableContextEntry;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public boolean isEnableModifyObject() {
        return enableModifyObject;
    }

    public void setEnableModifyObject(boolean enableModifyObject) {
        this.enableModifyObject = enableModifyObject;
    }
}
