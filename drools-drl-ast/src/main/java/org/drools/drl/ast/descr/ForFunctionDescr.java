package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * A descr class for for functions
 */
public class ForFunctionDescr extends BaseDescr {

    private static final long serialVersionUID = 520l;

    private String            id;
    private String            label;
    private List<String>      arguments;

    public ForFunctionDescr() { }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        id = (String) in.readObject();
        label = (String) in.readObject();
        arguments = (List<String>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( id );
        out.writeObject( label );
        out.writeObject( arguments );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String toString() {
        return "[ForFunctionDescr: " + label + " : " + id + "( " + arguments + " )]";
    }

}
