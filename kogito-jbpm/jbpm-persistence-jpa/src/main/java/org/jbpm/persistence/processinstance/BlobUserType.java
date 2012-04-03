package org.jbpm.persistence.processinstance;

import gnu.trove.list.linked.TByteLinkedList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**:HIB4 REMOVE ME:
import org.hibernate.engine.spi.SessionImplementor;
:HIB4 REMOVE ME:**/


/**
 * Thanks to 
 * http://i-proving.ca/space/Technologies/Hibernate/Blob+User+Type+in+Hibernate
 * 
 */
public class BlobUserType implements UserType {

    /**
     * {@inheritDoc}
     */
    public int[] sqlTypes() {
        return new int[] { Types.BLOB };
    }

    /**
     * {@inheritDoc}
     */
    public Class returnedClass() {
        return byte[].class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && Arrays.equals((byte[]) x, (byte[]) y));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode(Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }
        if (x instanceof byte[]) {
            byte[] xArr = (byte[]) x;
            return Arrays.hashCode(xArr);
        } else {
            return x.hashCode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        Blob blob = resultSet.getBlob(names[0]);
        if (blob == null) {
            return null;
        } else {
            return blob.getBytes(1, (int) blob.length());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value != null) {
            Blob blob = new BlobImpl((byte[]) value);
            st.setBlob(index, blob);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object deepCopy(Object value) {
        if (value == null) {
            return null;
        } else {
            byte[] bytes = (byte[]) value;
            byte[] result = new byte[bytes.length];
            System.arraycopy(bytes, 0, result, 0, bytes.length);
            return result;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMutable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    /**
     * {@inheritDoc}
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    /**
     * {@inheritDoc}
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    private class BlobImpl implements Blob {

        private TByteLinkedList blobInfo = new TByteLinkedList();

        public BlobImpl() {
            // Default constructor for ORM's, among other things
        }

        BlobImpl(byte[] bytes) {
            blobInfo.add(bytes);
        }

        public long length() throws SQLException {
            return this.blobInfo.size();
        }

        public byte[] getBytes(long pos, int length) throws SQLException {
            byte[] result = new byte[length];
            this.blobInfo.toArray(result, (int) pos - 1, length);
            return result;
        }

        public int setBytes(long pos, byte[] bytes) throws SQLException {
            if (pos + bytes.length > this.blobInfo.size()) {
                if (pos > this.blobInfo.size()) {
                    this.blobInfo.add(new byte[(int) pos - this.blobInfo.size()]);
                }
                this.blobInfo.add(new byte[(int) (pos + bytes.length) - blobInfo.size()]);
            }
            this.blobInfo.set((int) pos, bytes);
            return bytes.length;
        }

        public InputStream getBinaryStream(long offset, long length) throws SQLException {
            byte[] output = new byte[(int) length];
            System.arraycopy(this.blobInfo.toArray(), (int) offset, output, 0, (int) length);
            return new ByteArrayInputStream(output);
        }

        public InputStream getBinaryStream() throws SQLException {
            return new ByteArrayInputStream(this.blobInfo.toArray());
        }

        public long position(byte[] pattern, long start) throws SQLException {
            int pos = blobInfo.indexOf(pattern[0]);
            while (pos != -1) {
                int pat = 0;
                while (blobInfo.get(pos + pat) == pattern[pat] && pat < pattern.length) {
                    ++pat;
                }
                if (pat == pattern.length) {
                    return pos;
                } else {
                    int lastPos = pos;
                    pos = blobInfo.subList(pos, blobInfo.size()).indexOf(pattern[0]);
                    if (pos > 0) {
                        pos += lastPos;
                    }
                }
            }
            return pos;
        }

        public long position(Blob pattern, long start) throws SQLException {
            byte[] patternBytes = toByteArray(pattern);
            return position(patternBytes, start);
        }

        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
            if (blobInfo.size() < pos + len) {
                int size = (int) (pos + len) - blobInfo.size();
                blobInfo.add(new byte[size]);
            }
            blobInfo.set(offset, bytes, offset, len);
            return len;
        }

        public OutputStream setBinaryStream(long pos) throws SQLException {
            throw new UnsupportedOperationException("Unable to create binary stream for writing to blob");
        }

        public void truncate(long len) throws SQLException {
            byte[] contents = blobInfo.toArray();
            blobInfo.clear();
            byte[] newContents = new byte[(int) len];
            System.arraycopy(contents, 0, newContents, 0, (int) len);
            blobInfo.add(newContents);
        }

        public void free() throws SQLException {
            this.blobInfo.clear();
            this.blobInfo = null;
        }

        private byte[] toByteArray(Blob blob) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                return toByteArrayImpl(blob, baos);
            } catch (Exception e) {
                // do nothing
            }
            return null;
        }

        private byte[] toByteArrayImpl(Blob fromImageBlob, ByteArrayOutputStream baos) throws SQLException, IOException {
            byte buf[] = new byte[4096];
            int dataSize;
            InputStream is = fromImageBlob.getBinaryStream();

            try {
                while ((dataSize = is.read(buf)) != -1) {
                    baos.write(buf, 0, dataSize);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }

            return baos.toByteArray();
        }

    }

    /**:HIB4 REMOVE ME:

    // 
    // The following are Hibernate 4/JPA 2 specific methods 
    // -- and this class should only be used with Hibernate 3 
    // due to Hibernate 3 specific problems.
    // 
    // DO NOT MODIFY OR REMOVE THIS COMMENT
    // THIS ALLOWS THIS MODULE TO BE COMPILE WITH BOTH HIBERNATE 3 AND 4
    //

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException,
            SQLException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " should only be used with Hibernate 3.");
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException,
            SQLException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " should only be used with Hibernate 3.");
    }

    :HIB4 REMOVE ME:**/

}
