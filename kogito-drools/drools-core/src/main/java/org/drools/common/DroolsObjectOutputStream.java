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
    /**
     * Write an object to the underlying storage or stream.  The object was written
     * in Drools specific format.
     *
     * @param object the object to be written
     * @exception IOException Any of the usual Input/Output related exceptions.
     */
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

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     * @param b	the byte
     * @exception IOException If an I/O error has occurred.
     */
    public void write(int b) throws IOException {
        dataOutput.write(b);
    }

    /**
     * Writes an array of bytes. This method will block until the bytes
     * are actually written.
     * @param b	the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[]) throws IOException {
        dataOutput.write(b);
    }

    /**
     * Writes a sub array of bytes.
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[], int off, int len) throws IOException {
        dataOutput.write(b, off, len);
    }

    /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @exception IOException If an I/O error has occurred.
     */
    public void flush() throws IOException {
        dataOutput.flush();
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
       dataOutput.close();
    }

    /**
     * Writes a <code>boolean</code> value to this output stream.
     * If the argument <code>v</code>
     * is <code>true</code>, the value <code>(byte)1</code>
     * is written; if <code>v</code> is <code>false</code>,
     * the  value <code>(byte)0</code> is written.
     * The byte written by this method may
     * be read by the <code>readBoolean</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>boolean</code>
     * equal to <code>v</code>.
     *
     * @param      v   the boolean to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeBoolean(boolean v) throws IOException {
        dataOutput.writeBoolean(v);
    }

    /**
     * Writes to the output stream the eight low-
     * order bits of the argument <code>v</code>.
     * The 24 high-order bits of <code>v</code>
     * are ignored. (This means  that <code>writeByte</code>
     * does exactly the same thing as <code>write</code>
     * for an integer argument.) The byte written
     * by this method may be read by the <code>readByte</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>byte</code>
     * equal to <code>(byte)v</code>.
     *
     * @param      v   the byte value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeByte(int v) throws IOException {
        dataOutput.writeByte(v);
    }

    /**
     * Writes two bytes to the output
     * stream to represent the value of the argument.
     * The byte values to be written, in the  order
     * shown, are: <p>
     * <pre><code>
     * (byte)(0xff &amp; (v &gt;&gt; 8))
     * (byte)(0xff &amp; v)
     * </code> </pre> <p>
     * The bytes written by this method may be
     * read by the <code>readShort</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>short</code> equal
     * to <code>(short)v</code>.
     *
     * @param      v   the <code>short</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeShort(int v) throws IOException {
        dataOutput.writeShort(v);
    }

    /**
     * Writes a <code>char</code> value, which
     * is comprised of two bytes, to the
     * output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <p><pre><code>
     * (byte)(0xff &amp; (v &gt;&gt; 8))
     * (byte)(0xff &amp; v)
     * </code></pre><p>
     * The bytes written by this method may be
     * read by the <code>readChar</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>char</code> equal
     * to <code>(char)v</code>.
     *
     * @param      v   the <code>char</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeChar(int v) throws IOException {
        dataOutput.writeChar(v);
    }

    /**
     * Writes an <code>int</code> value, which is
     * comprised of four bytes, to the output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <p><pre><code>
     * (byte)(0xff &amp; (v &gt;&gt; 24))
     * (byte)(0xff &amp; (v &gt;&gt; 16))
     * (byte)(0xff &amp; (v &gt;&gt; &#32; &#32;8))
     * (byte)(0xff &amp; v)
     * </code></pre><p>
     * The bytes written by this method may be read
     * by the <code>readInt</code> method of interface
     * <code>DataInput</code> , which will then
     * return an <code>int</code> equal to <code>v</code>.
     *
     * @param      v   the <code>int</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeInt(int v) throws IOException {
        dataOutput.writeInt(v);
    }

    /**
     * Writes a <code>long</code> value, which is
     * comprised of eight bytes, to the output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <p><pre><code>
     * (byte)(0xff &amp; (v &gt;&gt; 56))
     * (byte)(0xff &amp; (v &gt;&gt; 48))
     * (byte)(0xff &amp; (v &gt;&gt; 40))
     * (byte)(0xff &amp; (v &gt;&gt; 32))
     * (byte)(0xff &amp; (v &gt;&gt; 24))
     * (byte)(0xff &amp; (v &gt;&gt; 16))
     * (byte)(0xff &amp; (v &gt;&gt;  8))
     * (byte)(0xff &amp; v)
     * </code></pre><p>
     * The bytes written by this method may be
     * read by the <code>readLong</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>long</code> equal
     * to <code>v</code>.
     *
     * @param      v   the <code>long</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeLong(long v) throws IOException {
        dataOutput.writeLong(v);
    }

    /**
     * Writes a <code>float</code> value,
     * which is comprised of four bytes, to the output stream.
     * It does this as if it first converts this
     * <code>float</code> value to an <code>int</code>
     * in exactly the manner of the <code>Float.floatToIntBits</code>
     * method  and then writes the <code>int</code>
     * value in exactly the manner of the  <code>writeInt</code>
     * method.  The bytes written by this method
     * may be read by the <code>readFloat</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>float</code>
     * equal to <code>v</code>.
     *
     * @param      v   the <code>float</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeFloat(float v) throws IOException {
        dataOutput.writeFloat(v);
    }

    /**
     * Writes a <code>double</code> value,
     * which is comprised of eight bytes, to the output stream.
     * It does this as if it first converts this
     * <code>double</code> value to a <code>long</code>
     * in exactly the manner of the <code>Double.doubleToLongBits</code>
     * method  and then writes the <code>long</code>
     * value in exactly the manner of the  <code>writeLong</code>
     * method. The bytes written by this method
     * may be read by the <code>readDouble</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>double</code>
     * equal to <code>v</code>.
     *
     * @param      v   the <code>double</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeDouble(double v) throws IOException {
        dataOutput.writeDouble(v);
    }

    /**
     * Writes a string to the output stream.
     * For every character in the string
     * <code>s</code>,  taken in order, one byte
     * is written to the output stream.  If
     * <code>s</code> is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.<p>  If <code>s.length</code>
     * is zero, then no bytes are written. Otherwise,
     * the character <code>s[0]</code> is written
     * first, then <code>s[1]</code>, and so on;
     * the last character written is <code>s[s.length-1]</code>.
     * For each character, one byte is written,
     * the low-order byte, in exactly the manner
     * of the <code>writeByte</code> method . The
     * high-order eight bits of each character
     * in the string are ignored.
     *
     * @param      s   the string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeBytes(String s) throws IOException {
        dataOutput.writeBytes(s);
    }

    /**
     * Writes every character in the string <code>s</code>,
     * to the output stream, in order,
     * two bytes per character. If <code>s</code>
     * is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.  If <code>s.length</code>
     * is zero, then no characters are written.
     * Otherwise, the character <code>s[0]</code>
     * is written first, then <code>s[1]</code>,
     * and so on; the last character written is
     * <code>s[s.length-1]</code>. For each character,
     * two bytes are actually written, high-order
     * byte first, in exactly the manner of the
     * <code>writeChar</code> method.
     *
     * @param      s   the string value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeChars(String s) throws IOException {
        dataOutput.writeChars(s);
    }

    /**
     * Writes two bytes of length information
     * to the output stream, followed
     * by the
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * representation
     * of  every character in the string <code>s</code>.
     * If <code>s</code> is <code>null</code>,
     * a <code>NullPointerException</code> is thrown.
     * Each character in the string <code>s</code>
     * is converted to a group of one, two, or
     * three bytes, depending on the value of the
     * character.<p>
     * If a character <code>c</code>
     * is in the range <code>&#92;u0001</code> through
     * <code>&#92;u007f</code>, it is represented
     * by one byte:<p>
     * <pre>(byte)c </pre>  <p>
     * If a character <code>c</code> is <code>&#92;u0000</code>
     * or is in the range <code>&#92;u0080</code>
     * through <code>&#92;u07ff</code>, then it is
     * represented by two bytes, to be written
     * in the order shown:<p> <pre><code>
     * (byte)(0xc0 | (0x1f &amp; (c &gt;&gt; 6)))
     * (byte)(0x80 | (0x3f &amp; c))
     *  </code></pre>  <p> If a character
     * <code>c</code> is in the range <code>&#92;u0800</code>
     * through <code>uffff</code>, then it is
     * represented by three bytes, to be written
     * in the order shown:<p> <pre><code>
     * (byte)(0xe0 | (0x0f &amp; (c &gt;&gt; 12)))
     * (byte)(0x80 | (0x3f &amp; (c &gt;&gt;  6)))
     * (byte)(0x80 | (0x3f &amp; c))
     *  </code></pre>  <p> First,
     * the total number of bytes needed to represent
     * all the characters of <code>s</code> is
     * calculated. If this number is larger than
     * <code>65535</code>, then a <code>UTFDataFormatException</code>
     * is thrown. Otherwise, this length is written
     * to the output stream in exactly the manner
     * of the <code>writeShort</code> method;
     * after this, the one-, two-, or three-byte
     * representation of each character in the
     * string <code>s</code> is written.<p>  The
     * bytes written by this method may be read
     * by the <code>readUTF</code> method of interface
     * <code>DataInput</code> , which will then
     * return a <code>String</code> equal to <code>s</code>.
     *
     * @param      str   the string value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void writeUTF(String str) throws IOException {
        dataOutput.writeUTF(str);
    }
}
