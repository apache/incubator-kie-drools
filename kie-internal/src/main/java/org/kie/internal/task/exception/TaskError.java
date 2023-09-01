/**
 *
 */
package org.kie.internal.task.exception;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TaskError
    implements
    Externalizable {
    String msg;

    public TaskError() {

    }

    public TaskError(String msg) {
        this.msg = msg;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( msg != null ) {
            out.writeBoolean( true );
            out.writeUTF( msg );
        } else {
            out.writeBoolean( false );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        if ( in.readBoolean() ) {
            msg = in.readUTF();
        }

    }

    public String getMessage() {
        return msg;
    }
}