package org.drools.rule;


public class Query extends Rule {

    public Query(String name) {
        super( name );
    }

    /** 
     * Override this as Queries will NEVER have a consequence, and it should
     * not be taken into account when deciding if it is valid.
     */
    public boolean isValid() {
        return super.isSemanticallyValid();
    }
    

}
