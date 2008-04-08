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

import org.drools.base.ClassFieldExtractorCache;
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
    private ClassFieldExtractorCache extractorFactory;

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

        public ClassFieldExtractorCache getExtractorFactory() {
            return DroolsObjectInputStream.this.getExtractorFactory();
        }

        public void setExtractorFactory(ClassFieldExtractorCache extractorFactory) {
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
        extractorFactory = ClassFieldExtractorCache.getInstance();
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

    public ClassFieldExtractorCache getExtractorFactory() {
        return extractorFactory;
    }

    public void setExtractorFactory(ClassFieldExtractorCache extractorFactory) {
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

    private static InvalidClassException newInvalidClassException(Class clazz, Throwable cause) {
        InvalidClassException exception = new InvalidClassException(clazz.getName());
        exception.initCause(cause);
        return exception;
    }

    /**
     * Using the ClassLoader from the top of the stack to load the class specified by the given class name.
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
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
    /**
     * Reads a byte of data. This method will block if no input is
     * available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read() throws IOException {
        return dataInput.read();
    }

    /**
     * Reads into an array of bytes.  This method will
     * block until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[]) throws IOException {
        return dataInput.read(b);
    }

    /**
     * Reads into an array of bytes.  This method will
     * block until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int len) throws IOException {
        return dataInput.read(b, off, len);
    }

    /**
     * Skips n bytes of input.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public long skip(long n) throws IOException {
        return dataInput.skip(n);
    }

    /**
     * Returns the number of bytes that can be read
     * without blocking.
     * @return the number of available bytes.
     * @exception IOException If an I/O error has occurred.
     */
    public int available() throws IOException {
        return dataInput.available();
    }

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
        dataInput.close();
    }

    /**
     * Reads some bytes from an input
     * stream and stores them into the buffer
     * array <code>b</code>. The number of bytes
     * read is equal
     * to the length of <code>b</code>.
     * <p>
     * This method blocks until one of the
     * following conditions occurs:<p>
     * <ul>
     * <li><code>b.length</code>
     * bytes of input data are available, in which
     * case a normal return is made.
     *
     * <li>End of
     * file is detected, in which case an <code>EOFException</code>
     * is thrown.
     *
     * <li>An I/O error occurs, in
     * which case an <code>IOException</code> other
     * than <code>EOFException</code> is thrown.
     * </ul>
     * <p>
     * If <code>b</code> is <code>null</code>,
     * a <code>NullPointerException</code> is thrown.
     * If <code>b.length</code> is zero, then
     * no bytes are read. Otherwise, the first
     * byte read is stored into element <code>b[0]</code>,
     * the next one into <code>b[1]</code>, and
     * so on.
     * If an exception is thrown from
     * this method, then it may be that some but
     * not all bytes of <code>b</code> have been
     * updated with data from the input stream.
     *
     * @param     b   the buffer into which the data is read.
     * @exception  IOException   if an I/O error occurs.
     */
    public void readFully(byte b[]) throws IOException {
        dataInput.readFully(b);
    }

    /**
     *
     * Reads <code>len</code>
     * bytes from
     * an input stream.
     * <p>
     * This method
     * blocks until one of the following conditions
     * occurs:<p>
     * <ul>
     * <li><code>len</code> bytes
     * of input data are available, in which case
     * a normal return is made.
     *
     * <li>End of file
     * is detected, in which case an <code>EOFException</code>
     * is thrown.
     *
     * <li>An I/O error occurs, in
     * which case an <code>IOException</code> other
     * than <code>EOFException</code> is thrown.
     * </ul>
     * <p>
     * If <code>b</code> is <code>null</code>,
     * a <code>NullPointerException</code> is thrown.
     * If <code>off</code> is negative, or <code>len</code>
     * is negative, or <code>off+len</code> is
     * greater than the length of the array <code>b</code>,
     * then an <code>IndexOutOfBoundsException</code>
     * is thrown.
     * If <code>len</code> is zero,
     * then no bytes are read. Otherwise, the first
     * byte read is stored into element <code>b[off]</code>,
     * the next one into <code>b[off+1]</code>,
     * and so on. The number of bytes read is,
     * at most, equal to <code>len</code>.
     *
     * @param     b   the buffer into which the data is read.
     * @param off  an int specifying the offset into the data.
     * @param len  an int specifying the number of bytes to read.
     * @exception  IOException   if an I/O error occurs.
     */
    public void readFully(byte b[], int off, int len) throws IOException {
        dataInput.readFully(b, off, len);
    }

    /**
     * Makes an attempt to skip over
     * <code>n</code> bytes
     * of data from the input
     * stream, discarding the skipped bytes. However,
     * it may skip
     * over some smaller number of
     * bytes, possibly zero. This may result from
     * any of a
     * number of conditions; reaching
     * end of file before <code>n</code> bytes
     * have been skipped is
     * only one possibility.
     * This method never throws an <code>EOFException</code>.
     * The actual
     * number of bytes skipped is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes actually skipped.
     * @exception  IOException   if an I/O error occurs.
     */
    public int skipBytes(int n) throws IOException {
        return dataInput.skipBytes(n);
    }

    /**
     * Reads one input byte and returns
     * <code>true</code> if that byte is nonzero,
     * <code>false</code> if that byte is zero.
     * This method is suitable for reading
     * the byte written by the <code>writeBoolean</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the <code>boolean</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public boolean readBoolean() throws IOException {
        return dataInput.readBoolean();
    }

    /**
     * Reads and returns one input byte.
     * The byte is treated as a signed value in
     * the range <code>-128</code> through <code>127</code>,
     * inclusive.
     * This method is suitable for
     * reading the byte written by the <code>writeByte</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the 8-bit value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public byte readByte() throws IOException {
        return dataInput.readByte();
    }

    /**
     * Reads one input byte, zero-extends
     * it to type <code>int</code>, and returns
     * the result, which is therefore in the range
     * <code>0</code>
     * through <code>255</code>.
     * This method is suitable for reading
     * the byte written by the <code>writeByte</code>
     * method of interface <code>DataOutput</code>
     * if the argument to <code>writeByte</code>
     * was intended to be a value in the range
     * <code>0</code> through <code>255</code>.
     *
     * @return     the unsigned 8-bit value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readUnsignedByte() throws IOException {
        return dataInput.readUnsignedByte();
    }

    /**
     * Reads two input bytes and returns
     * a <code>short</code> value. Let <code>a</code>
     * be the first byte read and <code>b</code>
     * be the second byte. The value
     * returned
     * is:
     * <p><pre><code>(short)((a &lt;&lt; 8) | (b &amp; 0xff))
     * </code></pre>
     * This method
     * is suitable for reading the bytes written
     * by the <code>writeShort</code> method of
     * interface <code>DataOutput</code>.
     *
     * @return     the 16-bit value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public short readShort() throws IOException {
        return dataInput.readShort();
    }

    /**
     * Reads two input bytes and returns
     * an <code>int</code> value in the range <code>0</code>
     * through <code>65535</code>. Let <code>a</code>
     * be the first byte read and
     * <code>b</code>
     * be the second byte. The value returned is:
     * <p><pre><code>(((a &amp; 0xff) &lt;&lt; 8) | (b &amp; 0xff))
     * </code></pre>
     * This method is suitable for reading the bytes
     * written by the <code>writeShort</code> method
     * of interface <code>DataOutput</code>  if
     * the argument to <code>writeShort</code>
     * was intended to be a value in the range
     * <code>0</code> through <code>65535</code>.
     *
     * @return     the unsigned 16-bit value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readUnsignedShort() throws IOException {
        return dataInput.readUnsignedShort();
    }

    /**
     * Reads an input <code>char</code> and returns the <code>char</code> value.
     * A Unicode <code>char</code> is made up of two bytes.
     * Let <code>a</code>
     * be the first byte read and <code>b</code>
     * be the second byte. The value
     * returned is:
     * <p><pre><code>(char)((a &lt;&lt; 8) | (b &amp; 0xff))
     * </code></pre>
     * This method
     * is suitable for reading bytes written by
     * the <code>writeChar</code> method of interface
     * <code>DataOutput</code>.
     *
     * @return     the Unicode <code>char</code> read.
     * @exception  IOException   if an I/O error occurs.
     */
    public char readChar() throws IOException {
        return dataInput.readChar();
    }

    /**
     * Reads four input bytes and returns an
     * <code>int</code> value. Let <code>a</code>
     * be the first byte read, <code>b</code> be
     * the second byte, <code>c</code> be the third
     * byte,
     * and <code>d</code> be the fourth
     * byte. The value returned is:
     * <p><pre>
     * <code>
     * (((a &amp; 0xff) &lt;&lt; 24) | ((b &amp; 0xff) &lt;&lt; 16) |
     * &#32;((c &amp; 0xff) &lt;&lt; 8) | (d &amp; 0xff))
     * </code></pre>
     * This method is suitable
     * for reading bytes written by the <code>writeInt</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the <code>int</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readInt() throws IOException {
        return dataInput.readInt();
    }

    /**
     * Reads eight input bytes and returns
     * a <code>long</code> value. Let <code>a</code>
     * be the first byte read, <code>b</code> be
     * the second byte, <code>c</code> be the third
     * byte, <code>d</code>
     * be the fourth byte,
     * <code>e</code> be the fifth byte, <code>f</code>
     * be the sixth byte, <code>g</code> be the
     * seventh byte,
     * and <code>h</code> be the
     * eighth byte. The value returned is:
     * <p><pre> <code>
     * (((long)(a &amp; 0xff) &lt;&lt; 56) |
     *  ((long)(b &amp; 0xff) &lt;&lt; 48) |
     *  ((long)(c &amp; 0xff) &lt;&lt; 40) |
     *  ((long)(d &amp; 0xff) &lt;&lt; 32) |
     *  ((long)(e &amp; 0xff) &lt;&lt; 24) |
     *  ((long)(f &amp; 0xff) &lt;&lt; 16) |
     *  ((long)(g &amp; 0xff) &lt;&lt;  8) |
     *  ((long)(h &amp; 0xff)))
     * </code></pre>
     * <p>
     * This method is suitable
     * for reading bytes written by the <code>writeLong</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the <code>long</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public long readLong() throws IOException {
        return dataInput.readLong();
    }

    /**
     * Reads four input bytes and returns
     * a <code>float</code> value. It does this
     * by first constructing an <code>int</code>
     * value in exactly the manner
     * of the <code>readInt</code>
     * method, then converting this <code>int</code>
     * value to a <code>float</code> in
     * exactly the manner of the method <code>Float.intBitsToFloat</code>.
     * This method is suitable for reading
     * bytes written by the <code>writeFloat</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the <code>float</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public float readFloat() throws IOException {
        return dataInput.readFloat();
    }

    /**
     * Reads eight input bytes and returns
     * a <code>double</code> value. It does this
     * by first constructing a <code>long</code>
     * value in exactly the manner
     * of the <code>readlong</code>
     * method, then converting this <code>long</code>
     * value to a <code>double</code> in exactly
     * the manner of the method <code>Double.longBitsToDouble</code>.
     * This method is suitable for reading
     * bytes written by the <code>writeDouble</code>
     * method of interface <code>DataOutput</code>.
     *
     * @return     the <code>double</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    public double readDouble() throws IOException {
        return dataInput.readDouble();
    }

    /**
     * Reads the next line of text from the input stream.
     * It reads successive bytes, converting
     * each byte separately into a character,
     * until it encounters a line terminator or
     * end of
     * file; the characters read are then
     * returned as a <code>String</code>. Note
     * that because this
     * method processes bytes,
     * it does not support input of the full Unicode
     * character set.
     * <p>
     * If end of file is encountered
     * before even one byte can be read, then <code>null</code>
     * is returned. Otherwise, each byte that is
     * read is converted to type <code>char</code>
     * by zero-extension. If the character <code>'\n'</code>
     * is encountered, it is discarded and reading
     * ceases. If the character <code>'\r'</code>
     * is encountered, it is discarded and, if
     * the following byte converts &#32;to the
     * character <code>'\n'</code>, then that is
     * discarded also; reading then ceases. If
     * end of file is encountered before either
     * of the characters <code>'\n'</code> and
     * <code>'\r'</code> is encountered, reading
     * ceases. Once reading has ceased, a <code>String</code>
     * is returned that contains all the characters
     * read and not discarded, taken in order.
     * Note that every character in this string
     * will have a value less than <code>&#92;u0100</code>,
     * that is, <code>(char)256</code>.
     *
     * @return the next line of text from the input stream,
     *         or <CODE>null</CODE> if the end of file is
     *         encountered before a byte can be read.
     * @exception  IOException  if an I/O error occurs.
     */
    public String readLine() throws IOException {
        return dataInput.readLine();
    }

    /**
     * Reads in a string that has been encoded using a
     * <a href="#modified-utf-8">modified UTF-8</a>
     * format.
     * The general contract of <code>readUTF</code>
     * is that it reads a representation of a Unicode
     * character string encoded in modified
     * UTF-8 format; this string of characters
     * is then returned as a <code>String</code>.
     * <p>
     * First, two bytes are read and used to
     * construct an unsigned 16-bit integer in
     * exactly the manner of the <code>readUnsignedShort</code>
     * method . This integer value is called the
     * <i>UTF length</i> and specifies the number
     * of additional bytes to be read. These bytes
     * are then converted to characters by considering
     * them in groups. The length of each group
     * is computed from the value of the first
     * byte of the group. The byte following a
     * group, if any, is the first byte of the
     * next group.
     * <p>
     * If the first byte of a group
     * matches the bit pattern <code>0xxxxxxx</code>
     * (where <code>x</code> means "may be <code>0</code>
     * or <code>1</code>"), then the group consists
     * of just that byte. The byte is zero-extended
     * to form a character.
     * <p>
     * If the first byte
     * of a group matches the bit pattern <code>110xxxxx</code>,
     * then the group consists of that byte <code>a</code>
     * and a second byte <code>b</code>. If there
     * is no byte <code>b</code> (because byte
     * <code>a</code> was the last of the bytes
     * to be read), or if byte <code>b</code> does
     * not match the bit pattern <code>10xxxxxx</code>,
     * then a <code>UTFDataFormatException</code>
     * is thrown. Otherwise, the group is converted
     * to the character:<p>
     * <pre><code>(char)(((a&amp; 0x1F) &lt;&lt; 6) | (b &amp; 0x3F))
     * </code></pre>
     * If the first byte of a group
     * matches the bit pattern <code>1110xxxx</code>,
     * then the group consists of that byte <code>a</code>
     * and two more bytes <code>b</code> and <code>c</code>.
     * If there is no byte <code>c</code> (because
     * byte <code>a</code> was one of the last
     * two of the bytes to be read), or either
     * byte <code>b</code> or byte <code>c</code>
     * does not match the bit pattern <code>10xxxxxx</code>,
     * then a <code>UTFDataFormatException</code>
     * is thrown. Otherwise, the group is converted
     * to the character:<p>
     * <pre><code>
     * (char)(((a &amp; 0x0F) &lt;&lt; 12) | ((b &amp; 0x3F) &lt;&lt; 6) | (c &amp; 0x3F))
     * </code></pre>
     * If the first byte of a group matches the
     * pattern <code>1111xxxx</code> or the pattern
     * <code>10xxxxxx</code>, then a <code>UTFDataFormatException</code>
     * is thrown.
     * <p>
     * If end of file is encountered
     * at any time during this entire process,
     * then an <code>EOFException</code> is thrown.
     * <p>
     * After every group has been converted to
     * a character by this process, the characters
     * are gathered, in the same order in which
     * their corresponding groups were read from
     * the input stream, to form a <code>String</code>,
     * which is returned.
     * <p>
     * The <code>writeUTF</code>
     * method of interface <code>DataOutput</code>
     * may be used to write data that is suitable
     * for reading by this method.
     * @return     a Unicode string.
     * @exception  IOException             if an I/O error occurs.
     */
    public String readUTF() throws IOException {
        return dataInput.readUTF();
    }
}