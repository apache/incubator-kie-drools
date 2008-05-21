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
public class DroolsObjectOutputStream implements ObjectOutput, DroolsObjectStreamConstants {
    private static final Class EMPTY_SET_CLASS = Collections.EMPTY_SET.getClass();
    private static final Class EMPTY_MAP_CLASS = Collections.EMPTY_MAP.getClass();
    private static final Class EMPTY_LIST_CLASS = Collections.EMPTY_LIST.getClass();

    private final Map<Object, Integer> handlesByObject = new IdentityHashMap<Object, Integer>();
    private final ObjectOutput    dataOutput;

    public DroolsObjectOutputStream(OutputStream outputStream) throws IOException {
        this((ObjectOutput)new ObjectOutputStream(outputStream));
    }
    public DroolsObjectOutputStream(ObjectOutput dataOutput) throws IOException {
        this.dataOutput   = dataOutput;
        writeStreamHeader();
    }

    private void writePrimitiveArray(Object array, Class clazz) throws IOException {
        if (clazz == Integer.TYPE) {
            writeIntArray(array);
        } else if (clazz == Byte.TYPE) {
            writeByteArray(array);
        } else if (clazz == Long.TYPE) {
            writeLongArray(array);
        } else if (clazz == Float.TYPE) {
            writeFloatArray(array);
        } else if (clazz == Double.TYPE) {
            writeDoubleArray(array);
        } else if (clazz == Short.TYPE) {
            writeShortArray(array);
        } else if (clazz == Character.TYPE) {
            writeCharArray(array);
        } else if (clazz == Boolean.TYPE) {
            writeBooleanArray(array);
        } else {
            throw new NotSerializableException("Unsupported array type: " + clazz);
        }
    }

    private void writeIntArray(Object array) throws IOException {
        int[] ints = (int[]) array;
        int length = ints.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            dataOutput.writeInt(ints[i]);
        }
    }

    private void writeByteArray(Object array) throws IOException {
        byte[] bytes = (byte[]) array;
        int length = bytes.length;
        dataOutput.writeInt(length);
        write(bytes, 0, length);
    }

    private void writeLongArray(Object array) throws IOException {
        long[] longs = (long[]) array;
        int length = longs.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            writeLong(longs[i]);
        }
    }

    private void writeFloatArray(Object array) throws IOException {
        float[] floats = (float[]) array;
        int length = floats.length;
        writeFloat(length);
        for (int i = 0; i < length; ++i) {
            writeFloat(floats[i]);
        }
    }

    private void writeDoubleArray(Object array) throws IOException {
        double[] doubles = (double[]) array;
        int length = doubles.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            writeDouble(doubles[i]);
        }
    }

    private void writeShortArray(Object array) throws IOException {
        short[] shorts = (short[]) array;
        int length = shorts.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            writeShort(shorts[i]);
        }
    }

    private void writeCharArray(Object array) throws IOException {
        char[] chars = (char[]) array;
        int length = chars.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            writeChar(chars[i]);
        }
    }

    private void writeBooleanArray(Object array) throws IOException {
        boolean[] booleans = (boolean[]) array;
        int length = booleans.length;
        dataOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            writeBoolean(booleans[i]);
        }
    }

    private void writeClass(Class clazz, int handle) throws IOException {
        dataOutput.writeByte(RT_CLASS);
        dataOutput.writeInt(handle);
        writeObject(clazz.getName());
    }

    private void writeString(String string, int handle) throws IOException {
        dataOutput.writeByte(RT_STRING);
        dataOutput.writeInt(handle);
        writeUTF(string);
    }

    private void writeStreamHeader() throws IOException {
        writeInt(STREAM_MAGIC);
        writeShort(STREAM_VERSION);
    }

    private int registerObject(Object object) {
        Integer handle = handlesByObject.get(object);
        if (handle == null) {
            handle = handlesByObject.size() + 1;
            handlesByObject.put(object, handle);
            handle = -handle;
        }
        return handle;
    }

    /*==========================================================================
      Implementations of ObjectOutput
    ==========================================================================*/
    public void writeObject(Object object) throws IOException {
        if (object == null) {
            dataOutput.writeByte(RT_NULL);
        } else {
            Class clazz = object.getClass();

            if (clazz == EMPTY_SET_CLASS) {
                dataOutput.writeByte(RT_EMPTY_SET);
            } else if (clazz == EMPTY_LIST_CLASS) {
                dataOutput.writeByte(RT_EMPTY_LIST);
            } else if (clazz == EMPTY_MAP_CLASS) {
                dataOutput.writeByte(RT_EMPTY_MAP);
            } else {
                if (clazz == String.class)
                    object  = ((String)object).intern();
                int handle = registerObject(object);
                if (handle < 0) {
                    handle  = -handle;
                    if (Externalizable.class.isAssignableFrom(clazz)) {
                        dataOutput.writeByte(RT_EXTERNALIZABLE);
                        dataOutput.writeInt(handle);
                        writeObject(clazz);
                        ((Externalizable)object).writeExternal(this);
                    } else if (Map.class.isAssignableFrom(clazz)) {
                        Map map = (Map)object;
                        dataOutput.writeByte(RT_MAP);
                        dataOutput.writeInt(handle);
                        writeObject(clazz);
                        dataOutput.writeInt(map.size());
                        for (Object obj : map.entrySet()) {
                            Map.Entry entry = (Map.Entry) obj;
                            writeObject(entry.getKey());
                            writeObject(entry.getValue());
                        }
                    } else if (clazz.isArray()) {
                        dataOutput.writeByte(RT_ARRAY);
                        dataOutput.writeInt(handle);
                        writeObject(clazz);
                        Class componentType = clazz.getComponentType();
                        if (componentType.isPrimitive()) {
                            writePrimitiveArray(object, componentType);
                        } else {
                            Object[]    array = (Object[])object;
                            int length = array.length;
                            dataOutput.writeInt(length);
                            for (int i = 0; i < length; ++i) {
                                writeObject(array[i]);
                            }
                        }
                    } else if (Collection.class.isAssignableFrom(clazz)) {
                        Collection collection   = (Collection)object;
                        dataOutput.writeByte(RT_COLLECTION);
                        dataOutput.writeInt(handle);
                        writeObject(clazz);
                        dataOutput.writeInt(collection.size());
                        for (Object obj : collection) {
                            writeObject(obj);
                        }
                    } else if (String.class.isAssignableFrom(clazz)) {
                        writeString((String) object, handle);
                    } else if (clazz == Class.class) {
                        writeClass((Class) object, handle);
                    } else if (AtomicReferenceArray.class.isAssignableFrom(clazz)) {
                        AtomicReferenceArray array  = (AtomicReferenceArray)object;
                        dataOutput.writeByte(RT_ATOMICREFERENCEARRAY);
                        dataOutput.writeInt(handle);
                        dataOutput.writeInt(array.length());
                        for (int i = 0; i < array.length(); i++)
                            writeObject(array.get(i));
                    } else if (Serializable.class.isAssignableFrom(clazz)) {
                        dataOutput.writeByte(RT_SERIALIZABLE);
                        dataOutput.writeInt(handle);
                        dataOutput.writeObject(object);
                    } else {
                        throw new NotSerializableException("Unsupported class: " + clazz);
                    }
                } else {
                    dataOutput.writeByte(RT_REFERENCE);
                    dataOutput.writeInt(handle);
                }
            }
        }
        dataOutput.flush();
    }

    public void write(int b) throws IOException {
        dataOutput.write(b);
    }

    public void write(byte b[]) throws IOException {
        dataOutput.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {
        dataOutput.write(b, off, len);
    }

    public void flush() throws IOException {
        dataOutput.flush();
    }

    public void close() throws IOException {
       dataOutput.close();
    }

    public void writeBoolean(boolean v) throws IOException {
        dataOutput.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        dataOutput.writeByte(v);
    }

    public void writeShort(int v) throws IOException {
        dataOutput.writeShort(v);
    }

    public void writeChar(int v) throws IOException {
        dataOutput.writeChar(v);
    }

    public void writeInt(int v) throws IOException {
        dataOutput.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        dataOutput.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        dataOutput.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        dataOutput.writeDouble(v);
    }

    public void writeBytes(String s) throws IOException {
        dataOutput.writeBytes(s);
    }

    public void writeChars(String s) throws IOException {
        dataOutput.writeChars(s);
    }

    public void writeUTF(String str) throws IOException {
        dataOutput.writeUTF(str);
    }
}
