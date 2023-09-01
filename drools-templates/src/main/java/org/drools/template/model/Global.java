package org.drools.template.model;

/**
 * Represents an application-data tag (nominally at the rule-set level). The idea of this can
 * be extended to other ruleset level settings.
 */
public class Global extends DRLElement
        implements
        DRLJavaEmitter {

    private String identifier;
    private String className;

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @return Returns the varName.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param clazz The className to set.
     */
    public void setClassName(final String clazz) {
        this.className = clazz;
    }

    /**
     * @param namez The identifier to set.
     */
    public void setIdentifier(final String namez) {
        this.identifier = namez;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine("global " + this.className + " " + this.identifier + ";");
    }
}
