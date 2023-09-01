package org.drools.drl.ast.descr;

import java.util.List;

/**
 * A super class for all Behavior Descriptors like
 * time window, event window, distinct, etc
 */
public class BehaviorDescr extends BaseDescr {
    
    private String subtype;
    private List<String> params;
    
    /**
     * @param type
     */
    public BehaviorDescr() { }
    
    /**
     * @param type
     */
    public BehaviorDescr(String type) {
        setText(type);
    }

    /**
     * @return the type
     */
    public String getType() {
        return getText();
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        setText( type );
    }

    public void setSubType( String subtype ) {
        this.subtype = subtype;
    }
    
    public void setParameters( List<String> params ) {
        this.params = params;
    }

    public String getSubType() {
        return subtype;
    }

    public List<String> getParameters() {
        return params;
    }

}
