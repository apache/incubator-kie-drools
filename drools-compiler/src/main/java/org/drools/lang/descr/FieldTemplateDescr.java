package org.drools.lang.descr;

public class FieldTemplateDescr extends BaseDescr {

    private static final long serialVersionUID = -5702332394753109979L;
    private String      name;
    private String      classType;
    
    public FieldTemplateDescr() {
        this(null, null);
    }
    
    public FieldTemplateDescr(final String name,
                              final String type) {
        super();
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
    public void setClassType(String classType) {
        this.classType = classType;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
}
