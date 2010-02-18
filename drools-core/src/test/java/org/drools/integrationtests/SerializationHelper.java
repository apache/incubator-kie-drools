package org.drools.integrationtests;

import java.io.IOException;

import org.drools.core.util.DroolsStreamUtils;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException, ClassNotFoundException {
        return serializeObject(obj, null);
    }

    public static <T> T serializeObject(T obj, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        return (T)DroolsStreamUtils.streamIn(DroolsStreamUtils.streamOut(obj), classLoader);
    }
}
