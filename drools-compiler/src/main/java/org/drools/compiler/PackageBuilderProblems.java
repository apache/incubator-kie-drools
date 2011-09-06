package org.drools.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderProblem;
import org.drools.builder.KnowledgeBuilderProblems;

public class PackageBuilderProblems extends ArrayList<KnowledgeBuilderProblem>
    implements
    KnowledgeBuilderProblems,
    Externalizable {
    private DroolsProblem[] errors;

    public PackageBuilderProblems() {
        super();
    }

    public PackageBuilderProblems(DroolsProblem[] errors) {
        super( errors.length );
        this.errors = errors;

        for ( DroolsProblem error : errors ) {
            add( error );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        SerializableDroolsError[] temp = (SerializableDroolsError[]) in.readObject();
        this.errors = temp;
        for ( DroolsError error : temp ) {
            add( error );
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( !this.errors.getClass().getComponentType().equals( SerializableDroolsError.class ) ) {
            SerializableDroolsError[] temp = new SerializableDroolsError[this.errors.length];
            int i = 0;
            for ( DroolsProblem error : this.errors ) {
                temp[i] = new SerializableDroolsError( error.getMessage(),
                                                       error.getErrorLines(),
                                                       error.getClass().getName() );
            }
            out.writeObject( temp );
        } else {
            out.writeObject( this.errors );
        }
    }

    public DroolsProblem[] getErrors() {
        return errors;
    }

    public boolean isEmpty() {
        return this.errors.length == 0;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        for ( int i = 0, length = this.errors.length; i < length; i++ ) {
            buf.append( errors[i] );
            buf.append( "\n" );
        }
        return buf.toString();
    }
}
