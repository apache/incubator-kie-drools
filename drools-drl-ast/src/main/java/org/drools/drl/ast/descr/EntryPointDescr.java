package org.drools.drl.ast.descr;


/**
 * An entry point descriptor for facts. This is part of the
 * support to multi-stream concurrent event assertion.
 */
public class EntryPointDescr extends PatternSourceDescr {

    private static final long serialVersionUID = 150l;

    public EntryPointDescr() {
    }
    
    public EntryPointDescr( String id ) {
        this.setText( id );
    }
    
    public void setEntryId( String id ) {
        this.setText( id );
    }
    
    public String getEntryId() {
        return this.getText();
    }
    
    @Override
    public String toString() {
        return "from entry-point \""+getEntryId()+"\"";
    }

}
