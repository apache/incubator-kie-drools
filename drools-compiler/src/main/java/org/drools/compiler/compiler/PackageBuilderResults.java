package org.drools.compiler.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;

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

        this.addAll(Arrays.asList(errors));
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        SerializableDroolsError[] temp = (SerializableDroolsError[]) in.readObject();
        this.errors = temp;
        this.addAll(Arrays.asList(temp));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( !this.errors.getClass().getComponentType().equals( SerializableDroolsError.class ) ) {
            SerializableDroolsError[] temp = new SerializableDroolsError[this.errors.length];
            int i = 0;
            for ( BaseKnowledgeBuilderResultImpl error : this.errors ) {
                temp[i] = new SerializableDroolsError( error );
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
