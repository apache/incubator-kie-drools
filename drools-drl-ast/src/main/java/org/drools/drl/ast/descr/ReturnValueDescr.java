package org.drools.drl.ast.descr;

/**
 * This Descr is used in jBPM code (jbpm-flow-builder) as part of the syntax tree
 * for the scripts used in BPMN2 definitions.
 */
public class ReturnValueDescr extends BaseDescr {
    private String text;

    public ReturnValueDescr() { }

    public ReturnValueDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}