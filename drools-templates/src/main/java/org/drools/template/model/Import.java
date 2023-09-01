package org.drools.template.model;

/**
 * Represents an import (nominally at the rule-set level). The idea of this can
 * be extended to other ruleset level settings.
 */
public class Import extends DRLElement
        implements
        DRLJavaEmitter {

    private String className;

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @param clazz The className to set.
     */
    public void setClassName(final String clazz) {
        this.className = clazz;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine("import " + this.className + ";");
    }
}
