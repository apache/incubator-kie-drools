package org.jbpm.services.task.impl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableUtil {

    private SerializableUtil() {
        // static methods
    }

    public static byte[] serialize( Serializable object ) throws IOException {
        return serialize(object, Thread.currentThread().getContextClassLoader());
    }

    public static byte[] serialize( Serializable object, ClassLoader classLoader ) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] result;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(object);
            result = outputStream.toByteArray();
        } finally {
            try {
                if( out != null ) {
                    out.close();
                }
            } catch( IOException ex ) {
                // ignore close exception
            }
            try {
                outputStream.close();
            } catch( IOException ex ) {
                // ignore close exception
            }
        }
        return result;
    }

    public static Serializable deserialize( byte[] byteArray ) throws IOException, ClassNotFoundException {
        return deserialize(byteArray, Thread.currentThread().getContextClassLoader());
    }

    public static Serializable deserialize( byte[] byteArray, ClassLoader classLoader ) throws IOException, ClassNotFoundException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(byteArray);
        ObjectInput objectIn = null;
        Serializable result;
        try {
            objectIn = new ObjectInputStream(byteArrayIn);
            result = (Serializable) objectIn.readObject();
        } finally {
            try {
                byteArrayIn.close();
            } catch( IOException ex ) {
                // ignore close exception
            }
            try {
                if( objectIn != null ) {
                    objectIn.close();
                }
            } catch( IOException ex ) {
                // ignore close exception
            }
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return result;
    }

}
