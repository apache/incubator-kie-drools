/**
 *
 */
package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.rule.DialectDatas;
import org.drools.rule.Package;

public class DroolsObjectInputStream
        implements DroolsObjectInput, DroolsObjectStreamConstants {

    private final Map<Integer, Object> objectsByHandle = new HashMap<Integer, Object>();
    private DroolsObjectInput dataInput;

    private static final Map<String, Class>     primClasses = new HashMap<String, Class>( 8, 1.0F );
    static {
        primClasses.put( "boolean",
                         boolean.class );
        primClasses.put( "byte",
                         byte.class );
        primClasses.put( "char",
                         char.class );
        primClasses.put( "short",
                         short.class );
        primClasses.put( "int",
                         int.class );
        primClasses.put( "long",
                         long.class );
        primClasses.put( "float",
                         float.class );
        primClasses.put( "double",
                         double.class );
        primClasses.put( "void",
                         void.class );
    }

    private ClassLoader classLoader;
    private InternalRuleBase         ruleBase;
    private InternalWorkingMemory    workingMemory;
    private Package                  pkg;
    private DialectDatas             dialectDatas;
    private ClassFieldAccessorCache extractorFactory;

    /**
     * Created this inner class to handle un-Externalizable objects just in case.
     */
    private class DroolsInternalInputStream
            extends ObjectInputStream
            implements DroolsObjectInput {

        private DroolsInternalInputStream(InputStream in, ClassLoader classLoader) throws IOException {
            super(in);
            setClassLoader(classLoader);
        }

        public ClassLoader getClassLoader() {
            return DroolsObjectInputStream.this.getClassLoader();
        }

        public void setClassLoader(ClassLoader classLoader) {
            DroolsObjectInputStream.this.setClassLoader(classLoader);
        }

        public InternalRuleBase getRuleBase() {
            return DroolsObjectInputStream.this.getRuleBase();
        }
        public void setRuleBase(InternalRuleBase ruleBase) {
            DroolsObjectInputStream.this.setRuleBase(ruleBase);
        }

        public InternalWorkingMemory getWorkingMemory() {
            return DroolsObjectInputStream.this.getWorkingMemory();
        }
        public void setWorkingMemory(InternalWorkingMemory workingMemory) {
            DroolsObjectInputStream.this.setWorkingMemory(workingMemory);
        }

        public Package getPackage() {
            return DroolsObjectInputStream.this.getPackage();
        }
        public void setPackage(Package pkg) {
            DroolsObjectInputStream.this.setPackage(pkg);
        }

        public DialectDatas getDialectDatas() {
            return DroolsObjectInputStream.this.getDialectDatas();
        }
        public void setDialectDatas(DialectDatas dialectDatas) {
            DroolsObjectInputStream.this.setDialectDatas(dialectDatas);
        }

        public ClassFieldAccessorCache getExtractorFactory() {
            return DroolsObjectInputStream.this.getExtractorFactory();
        }

        public void setExtractorFactory(ClassFieldAccessorCache extractorFactory) {
            DroolsObjectInputStream.this.setExtractorFactory(extractorFactory);
        }

        protected Class resolveClass(ObjectStreamClass desc) throws IOException,
                                                                    ClassNotFoundException {
            if ( getClassLoader() == null ) {
                return super.resolveClass( desc );
            } else {
                try {
                    return DroolsObjectInputStream.this.resolveClass(desc.getName());
                }
                catch (ClassNotFoundException cnf) {
                    return  super.resolveClass( desc );
                }
            }
        }
    }

    public DroolsObjectInputStream(InputStream inputStream) throws IOException {
        this(inputStream, null);
    }

    public DroolsObjectInputStream(InputStream inputStream, ClassLoader classLoader) throws IOException {
        dataInput    = new DroolsInternalInputStream(inputStream, classLoader);
        extractorFactory = ClassFieldAccessorCache.getInstance();
        readStreamHeader();
    }

    public DroolsObjectInputStream(ObjectInput inputStream) throws IOException {
        this(inputStream, null);
    }

    public DroolsObjectInputStream(ObjectInput inputStream, ClassLoader classLoader) throws IOException {
        this((InputStream)inputStream, classLoader);
    }

    public InternalRuleBase getRuleBase() {
        return ruleBase;
    }
    public void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase   = ruleBase;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }
    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory  = workingMemory;
    }

    public Package getPackage() {
        return pkg;
    }
    public void setPackage(Package pkg) {
        this.pkg    = pkg;
    }

    public DialectDatas getDialectDatas() {
        return dialectDatas;
    }
    public void setDialectDatas(DialectDatas dialectDatas) {
        this.dialectDatas   = dialectDatas;
    }

    public ClassFieldAccessorCache getExtractorFactory() {
        return extractorFactory;
    }

    public void setExtractorFactory(ClassFieldAccessorCache extractorFactory) {
        this.extractorFactory   = extractorFactory;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }
        this.classLoader    = classLoader;
    }

    protected void readStreamHeader() throws IOException {
        int magic   = readInt();
        short version = readShort();
        if (magic != STREAM_MAGIC || version != STREAM_VERSION) {
            throw new StreamCorruptedException("Invalid stream header: "+magic+'|'+version);
        }
    }

    public Object readObject() throws ClassNotFoundException, IOException {
        byte type = readRecordType();

        switch (type) {
            case RT_REFERENCE:
                return objectsByHandle.get(dataInput.readInt());
            case RT_NULL:
                return null;
            case RT_EXTERNALIZABLE: {
                int handle = dataInput.readInt();
                Class clazz = (Class) readObject();
                Externalizable externalizable;
                try {
                    externalizable = (Externalizable) clazz.newInstance();
                } catch (InstantiationException e) {
                    throw newInvalidClassException(clazz, e);
                } catch (IllegalAccessException e) {
                    throw newInvalidClassException(clazz, e);
                }
                registerObject(handle, externalizable);
                externalizable.readExternal(this);
                return externalizable;
            }
            case RT_MAP: {
                int handle = dataInput.readInt();
                Class clazz = (Class) readObject();
                int size = dataInput.readInt();
                Map<Object, Object> map = (Map<Object, Object>) newCollection(handle, clazz, size);
                while (size-- > 0) {
                    Object key = readObject();
                    Object value = readObject();
                    map.put(key, value);
                }
                return map;
            }
            case RT_ARRAY: {
                int handle = dataInput.readInt();
                Class clazz = (Class) readObject();
                int length = dataInput.readInt();
                Class componentType = clazz.getComponentType();
                Object array = Array.newInstance(componentType, length);
                registerObject(handle, array);
                if (componentType.isPrimitive()) {
                    readPrimitiveArray(array, length, componentType);
                } else {
                    Object[] objects    = (Object[])array;
                    for (int i = 0; i < length; ++i) {
                        objects[i] = readObject();
                    }
                }
                return array;
            }
            case RT_COLLECTION: {
                int handle = dataInput.readInt();
                Class clazz = (Class) readObject();
                int size = dataInput.readInt();
                Collection<Object> collection = (Collection<Object>) newCollection(handle, clazz, size);
                while (size-- > 0) {
                    collection.add(readObject());
                }
                return collection;
            }
            case RT_STRING:
                return readString(dataInput.readInt());
            case RT_CLASS:
                return readClass(dataInput.readInt());
            case RT_EMPTY_SET:
                return readEmptySet();
            case RT_EMPTY_LIST:
                return readEmptyList();
            case RT_EMPTY_MAP:
                return readEmptyMap();
            default:
                int handle = dataInput.readInt();

                switch (type) {
                    case RT_ATOMICREFERENCEARRAY: {
                        int length  = dataInput.readInt();
                        AtomicReferenceArray<Object>    array   = new AtomicReferenceArray<Object>(length);
                        registerObject(handle, array);
                        for (int i = 0; i < length; ++i) {
                            array.set(i, readObject());
                        }
                        return array;
                    }
                    case RT_SERIALIZABLE: {
                        Object  object  =  dataInput.readObject();
                        registerObject(handle, object);
                        return object;
                    }
                    default:
                        throw new StreamCorruptedException("Unsupported object type: " + type);
                }
        }
    }

    private void readPrimitiveArray(Object array, int length, Class clazz) throws IOException {
        if (clazz == Integer.TYPE) {
            readIntArray((int[]) array, length);
        } else if (clazz == Byte.TYPE) {
            readByteArray((byte[]) array, length);
        } else if (clazz == Long.TYPE) {
            readLongArray((long[]) array, length);
        } else if (clazz == Float.TYPE) {
            readFloatArray((float[]) array, length);
        } else if (clazz == Double.TYPE) {
            readDoubleArray((double[]) array, length);
        } else if (clazz == Short.TYPE) {
            readShortArray((short[]) array, length);
        } else if (clazz == Character.TYPE) {
            readCharArray((char[]) array, length);
        } else if (clazz == Boolean.TYPE) {
            readBooleanArray((boolean[]) array, length);
        } else {
            throw new StreamCorruptedException("Unsupported array type: " + clazz);
        }
    }

    private void readIntArray(int[] ints, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            ints[i] = dataInput.readInt();
        }
    }

    private void readByteArray(byte[] bytes, int length) throws IOException {
        dataInput.readFully(bytes, 0, length);
    }

    private void readLongArray(long[] longs, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            longs[i] = dataInput.readLong();
        }
    }

    private void readFloatArray(float[] floats, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            floats[i] = dataInput.readFloat();
        }
    }

    private void readDoubleArray(double[] doubles, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            doubles[i] = dataInput.readDouble();
        }
    }

    private void readShortArray(short[] shorts, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            shorts[i] = dataInput.readShort();
        }
    }

    private void readCharArray(char[] chars, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            chars[i] = dataInput.readChar();
        }
    }

    private void readBooleanArray(boolean[] booleans, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            booleans[i] = dataInput.readBoolean();
        }
    }

    private static Set readEmptySet() {
        return Collections.EMPTY_SET;
    }

    private static List readEmptyList() {
        return Collections.EMPTY_LIST;
    }

    private static Map readEmptyMap() {
        return Collections.EMPTY_MAP;
    }

    private String readString(int handle) throws IOException {
        String string = dataInput.readUTF();
        registerObject(handle, string);
        return string;
    }

    private Object newCollection(int handle, Class clazz, int size) throws IOException {
        Object collection;
        try {
            Constructor constructor = clazz.getConstructor(Integer.TYPE);
            collection = constructor.newInstance(size);
        } catch (InstantiationException e) {
            throw newInvalidClassException(clazz, e);
        } catch (IllegalAccessException e) {
            throw newInvalidClassException(clazz, e);
        } catch (InvocationTargetException e) {
            throw newInvalidClassException(clazz, e);
        } catch (NoSuchMethodException e) {
            try {
                Constructor constructor = clazz.getConstructor();
                collection  = constructor.newInstance();
            } catch (Exception e2) {
                throw newInvalidClassException(clazz, e2);
            }
        }
        registerObject(handle, collection);
        return collection;
    }

    private Class readClass(int handle) throws IOException, ClassNotFoundException {
        String className = (String) readObject();
        Class clazz = resolveClass(className);
        registerObject(handle, clazz);
        return clazz;
    }

    private byte readRecordType() throws IOException {
        return dataInput.readByte();
    }

    private void registerObject(int handle, Object object) {
        objectsByHandle.put(handle, object);
    }

    public static InvalidClassException newInvalidClassException(Class clazz, Throwable cause) {
        InvalidClassException exception = new InvalidClassException(clazz.getName());
        exception.initCause(cause);
        return exception;
    }

    protected Class resolveClass(String className) throws ClassNotFoundException{
        try {
            Class clazz = primClasses.get( className );
            if ( clazz == null ) {
                clazz = Class.forName(className, true, getClassLoader());
                if (clazz == null) {
                  clazz = Class.forName(className, true, getClass().getClassLoader());
                }
            }
            return clazz;
        }
        catch (ClassNotFoundException e) {
            return Class.forName(className, true, getClass().getClassLoader());
        }
    }

    /*=================================================================================
    ObjectInput implementations
    =================================================================================*/
    public int read() throws IOException {
        return dataInput.read();
    }

    public int read(byte b[]) throws IOException {
        return dataInput.read(b);
    }

    public int read(byte b[], int off, int len) throws IOException {
        return dataInput.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return dataInput.skip(n);
    }

    public int available() throws IOException {
        return dataInput.available();
    }

    public void close() throws IOException {
        dataInput.close();
    }

    public void readFully(byte b[]) throws IOException {
        dataInput.readFully(b);
    }

    public void readFully(byte b[], int off, int len) throws IOException {
        dataInput.readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        return dataInput.skipBytes(n);
    }

    public boolean readBoolean() throws IOException {
        return dataInput.readBoolean();
    }

    public byte readByte() throws IOException {
        return dataInput.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return dataInput.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return dataInput.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return dataInput.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return dataInput.readChar();
    }

    public int readInt() throws IOException {
        return dataInput.readInt();
    }

    public long readLong() throws IOException {
        return dataInput.readLong();
    }

    public float readFloat() throws IOException {
        return dataInput.readFloat();
    }

    public double readDouble() throws IOException {
        return dataInput.readDouble();
    }

    public String readLine() throws IOException {
        return dataInput.readLine();
    }

    public String readUTF() throws IOException {
        return dataInput.readUTF();
    }
}