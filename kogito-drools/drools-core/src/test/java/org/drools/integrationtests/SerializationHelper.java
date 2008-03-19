package org.drools.integrationtests;

import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.DroolsObjectInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException, ClassNotFoundException {
        return (T)serializeIn(serializeOut(obj));
    }

    public static byte[] serializeOut(Object obj) throws IOException {
        ByteArrayOutputStream   out = new ByteArrayOutputStream();
        new DroolsObjectOutputStream(out).writeObject(obj);
        out.close();
        return out.toByteArray();
    }

    public static Object serializeIn(byte[] bytes)
            throws IOException, ClassNotFoundException {
        return new DroolsObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }
}
