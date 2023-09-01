package org.drools.drl.ast.descr;

/**
 * A pattern source descriptor for windows
 */
public class WindowReferenceDescr extends PatternSourceDescr {

    private static final long serialVersionUID = 150l;
    
    public WindowReferenceDescr() {
    }
    
    public WindowReferenceDescr( String name ) {
        this.setText( name );
    }
    
    public void setName( String name ) {
        this.setText( name );
    }
    
    public String getName() {
        return this.getText();
    }
    
    @Override
    public String toString() {
        return "from window \""+getName()+"\"";
    }

}
