package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EnumLiteralDescr extends AnnotatedBaseDescr
    implements
    Comparable<EnumLiteralDescr> {

    private static final long            serialVersionUID = 510l;
    private int                          index            = -1;
    private String                       name;
    private List<String>                 constructorArgs  = Collections.emptyList();

    public EnumLiteralDescr() {
        this( null );
    }

    public EnumLiteralDescr( final String name ) {
        this.name = name;
    }


    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        index = in.readInt();
        name = (String) in.readObject();
        constructorArgs = (List<String>) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal(out);
        out.writeInt(index);
        out.writeObject( name );
        out.writeObject( constructorArgs );
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(List<String> constructorArgs) {
        this.constructorArgs = constructorArgs;
    }
    
    public void addConstructorArg( String arg ) {
        if ( constructorArgs == Collections.EMPTY_LIST ) {
            constructorArgs = new ArrayList<>();
        }
        constructorArgs.add( arg );
    }

    @Override
    public String toString() {
        return "EnumLiteralDescr{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", constructorArgs='" + constructorArgs + '\'' +
                "} " + super.toString();
    }

    public int compareTo( EnumLiteralDescr other ) {
        return (this.index - other.index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }


}
