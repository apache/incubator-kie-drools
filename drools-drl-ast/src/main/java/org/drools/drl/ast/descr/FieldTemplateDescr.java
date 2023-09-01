package org.drools.drl.ast.descr;

public class FieldTemplateDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;
    private String            name;
    private String            classType;

    public FieldTemplateDescr() {
        this( null,
              null );
    }

    public FieldTemplateDescr(final String name,
                              final String type) {
        this.name = name;
        this.classType = type;
    }

    public String getName() {
        return this.name;
    }

    public String getClassType() {
        return this.classType;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(final String classType) {
        this.classType = classType;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
