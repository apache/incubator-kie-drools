package org.drools.lang.descr;

public class ProcessDescr extends BaseDescr {
    private String name;
    private String className;

    
    public ProcessDescr() {
        
    }
    
    public ProcessDescr(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }        
    
    

}
