package org.drools.lang.descr;

public class ActionDescr extends BaseDescr {
    private String text;
    
    public ActionDescr() {
        
    }
    
    public ActionDescr(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
