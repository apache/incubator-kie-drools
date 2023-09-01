package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
 
public class WindowDeclarationDescr extends AnnotatedBaseDescr {

    private static final long            serialVersionUID = 530l;
    private String                       name = null;
    private PatternDescr                 pattern = null;

    public WindowDeclarationDescr() {
        super();
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        this.name = (String) in.readObject();
        this.pattern = (PatternDescr) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( name );
        out.writeObject( pattern );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    public void setPattern( PatternDescr pattern ) {
        this.pattern = pattern;
    }
    
    public PatternDescr getPattern() {
        return this.pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        WindowDeclarationDescr other = (WindowDeclarationDescr) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }

}
