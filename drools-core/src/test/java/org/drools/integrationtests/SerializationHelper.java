package org.drools.integrationtests;

import org.drools.util.DroolsStreamUtils;

import java.io.IOException;

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
