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
            // Blob blob = new BlobImpl((byte[]) value);
            // st.setBlob(index, blob);
            /** 
             * The two lines above will NOT work with Oracle, 
             *  because of irregularities in Oracle Blob handling (in the Oracle jdbc driver). 
             * See https://hibernate.onjira.com/browse/EJB-24 for a little bit more info. 
             * However, we can get around it by using setBinaryStream(). 
             * Thanks to http://www.herongyang.com/JDBC/MySQL-BLOB-setBinaryStream.html 
             *  for inspiration.
             */
            byte [] valueByteArr = (byte []) value;
            ByteArrayInputStream bais = new ByteArrayInputStream(valueByteArr);
            st.setBinaryStream(index, bais, valueByteArr.length);
            
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
