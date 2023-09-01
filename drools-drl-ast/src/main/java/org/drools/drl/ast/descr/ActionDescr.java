package org.drools.drl.ast.descr;

public class ActionDescr extends BaseDescr {
    private String text;
    
    public ActionDescr() { }

    public ActionDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
