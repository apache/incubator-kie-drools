package org.drools.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.drools.builder.KnowledgeBuilderResult;
import org.drools.builder.KnowledgeBuilderResults;

public class PackageBuilderResults extends ArrayList<KnowledgeBuilderResult>
    implements
    KnowledgeBuilderResults,
    Externalizable {
    private BaseKnowledgeBuilderResultImpl[] errors;

    public PackageBuilderResults() {
        super();
    }

    public PackageBuilderResults(BaseKnowledgeBuilderResultImpl[] errors) {
        super( errors.length );
        this.errors = errors;

        for ( BaseKnowledgeBuilderResultImpl error : errors ) {
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
            for ( BaseKnowledgeBuilderResultImpl error : this.errors ) {
                temp[i] = new SerializableDroolsError( error.getMessage(),
                                                       error.getLines(),
                                                       error.getClass().getName() );
            }
            out.writeObject( temp );
        } else {
            out.writeObject( this.errors );
        }
    }

    public BaseKnowledgeBuilderResultImpl[] getErrors() {
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
