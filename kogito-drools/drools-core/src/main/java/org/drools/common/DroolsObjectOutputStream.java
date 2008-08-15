package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 *
 */
public class DroolsObjectOutputStream extends ObjectOutputStream {
    public DroolsObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
}
