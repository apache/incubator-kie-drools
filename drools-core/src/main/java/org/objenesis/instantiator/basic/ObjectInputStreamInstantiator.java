package org.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.Serializable;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by using a dummy input stream that always feeds data for an empty object of
 * the same kind. NOTE: This instantiator may not work properly if the class being instantiated
 * defines a "readResolve" method, since it may return objects that have been returned previously
 * (i.e., there's no guarantee that the returned object is a new one), or even objects from a
 * completely different class.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class ObjectInputStreamInstantiator
    implements
    ObjectInstantiator {
    private static class MockStream extends InputStream {

        private int                pointer;
        private byte[]             data;
        private int                sequence;
        private static final int[] NEXT = new int[]{1, 2, 2};
        private byte[][]           buffers;

        private final byte[]       FIRST_DATA;
        private static byte[]      HEADER;
        private static byte[]      REPEATING_DATA;

        static {
            initialize();
        }

        private static void initialize() {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream( byteOut );
                dout.writeShort( ObjectStreamConstants.STREAM_MAGIC );
                dout.writeShort( ObjectStreamConstants.STREAM_VERSION );
                HEADER = byteOut.toByteArray();

                byteOut = new ByteArrayOutputStream();
                dout = new DataOutputStream( byteOut );

                dout.writeByte( ObjectStreamConstants.TC_OBJECT );
                dout.writeByte( ObjectStreamConstants.TC_REFERENCE );
                dout.writeInt( ObjectStreamConstants.baseWireHandle );
                REPEATING_DATA = byteOut.toByteArray();
            } catch ( final IOException e ) {
                throw new Error( "IOException: " + e.getMessage() );
            }

        }

        public MockStream(final Class clazz) {
            this.pointer = 0;
            this.sequence = 0;
            this.data = HEADER;

            // (byte) TC_OBJECT
            // (byte) TC_CLASSDESC
            // (short length)
            // (byte * className.length)
            // (long)serialVersionUID
            // (byte) SC_SERIALIZABLE
            // (short)0 <fields>
            // TC_ENDBLOCKDATA
            // TC_NULL
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream( byteOut );
            try {
                dout.writeByte( ObjectStreamConstants.TC_OBJECT );
                dout.writeByte( ObjectStreamConstants.TC_CLASSDESC );
                dout.writeUTF( clazz.getName() );
                dout.writeLong( ObjectStreamClass.lookup( clazz ).getSerialVersionUID() );
                dout.writeByte( ObjectStreamConstants.SC_SERIALIZABLE );
                dout.writeShort( (short) 0 ); // Zero fields
                dout.writeByte( ObjectStreamConstants.TC_ENDBLOCKDATA );
                dout.writeByte( ObjectStreamConstants.TC_NULL );
            } catch ( final IOException e ) {
                throw new Error( "IOException: " + e.getMessage() );
            }
            this.FIRST_DATA = byteOut.toByteArray();
            this.buffers = new byte[][]{HEADER, this.FIRST_DATA, REPEATING_DATA};
        }

        private void advanceBuffer() {
            this.pointer = 0;
            this.sequence = NEXT[this.sequence];
            this.data = this.buffers[this.sequence];
        }

        public int read() throws IOException {
            final int result = this.data[this.pointer++];
            if ( this.pointer >= this.data.length ) {
                advanceBuffer();
            }

            return result;
        }

        public int available() throws IOException {
            return Integer.MAX_VALUE;
        }

        public int read(final byte[] b,
                        int off,
                        final int len) throws IOException {
            int left = len;
            int remaining = this.data.length - this.pointer;

            while ( remaining <= left ) {
                System.arraycopy( this.data,
                                  this.pointer,
                                  b,
                                  off,
                                  remaining );
                off += remaining;
                left -= remaining;
                advanceBuffer();
                remaining = this.data.length - this.pointer;
            }
            if ( left > 0 ) {
                System.arraycopy( this.data,
                                  this.pointer,
                                  b,
                                  off,
                                  left );
                this.pointer += left;
            }

            return len;
        }
    }

    private ObjectInputStream inputStream;

    public ObjectInputStreamInstantiator(final Class clazz) {
        if ( Serializable.class.isAssignableFrom( clazz ) ) {
            try {
                this.inputStream = new ObjectInputStream( new MockStream( clazz ) );
            } catch ( final IOException e ) {
                throw new Error( "IOException: " + e.getMessage() );
            }
        } else {
            throw new ObjenesisException( new NotSerializableException( clazz + " not serializable" ) );
        }
    }

    public Object newInstance() {
        try {
            return this.inputStream.readObject();
        } catch ( final ClassNotFoundException e ) {
            throw new Error( "ClassNotFoundException: " + e.getMessage() );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }
}
