package org.drools.common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 */
public class DroolsObjectOutputStream extends ObjectOutputStream {
    public DroolsObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
}
