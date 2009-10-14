package org.drools.vsm.mina;

import org.apache.mina.core.session.IoSession;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;

public class MinaIoWriter
    implements
    GenericIoWriter {
    private IoSession session;

    public MinaIoWriter(IoSession session) {
        this.session = session;
    }

    public void write(Message message) {
        this.session.write( message );
    }

}
