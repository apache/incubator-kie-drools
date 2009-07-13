package org.drools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.util.DroolsStreamUtils;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 3, 2008 Time: 11:19:44 AM To change this template use File |
 * Settings | File Templates.
 */
public class DroolsObjectIOTest  extends TestCase {

    private static final String TEST_FILE   = "test.dat";
    private static final GroupElement   testGroupElement    = new GroupElement();

    static class Test implements Serializable {
        String  str = TEST_FILE;
    }
    
    public DroolsObjectIOTest() {
        
    }
    
    public void testFileIO() throws Exception {
        File    file    = new File(getClass().getResource("DroolsObjectIOTest.class").getFile());
        ByteArrayOutputStream   bytes   = new ByteArrayOutputStream();
        new ObjectOutputStream(bytes).writeObject(new Test());
        Test    t   = (Test)new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        String  str = TEST_FILE;
        file    = new File(file.getParent().replaceAll("%20", " "), str);

        DroolsStreamUtils.streamOut(new FileOutputStream(file), testGroupElement);

        InputStream         fis = getClass().getResourceAsStream(TEST_FILE);
        System.out.println(fis.available());

        GroupElement    that    = (GroupElement)DroolsStreamUtils.streamIn(fis);
        assertEquals(that, testGroupElement);
    }

    public static class ExternalizableObject extends SerializableObject implements Externalizable {

        public ExternalizableObject() {
            super("ExternalizableObject");
        }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            value   = in.readInt();
            name    = (String)in.readObject();
        }
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(value);
            out.writeObject(name);
        }
    }

    public static class SerializableObject implements Serializable {
        protected int value = 123;
        protected String    name;

        public SerializableObject() {
            this("SerializableObject");
        }
        public SerializableObject(String name) {
            this.name   = name;
        }

        public boolean equals(Object obj) {
            if (obj instanceof SerializableObject) {
                return value == ((SerializableObject)obj).value;
            }
            return false;
        }
        public String toString() {
            return new StringBuilder(name).append('|').append(value).toString();
        }
    }

    public void testObject() throws Exception {
        SerializableObject    obj = new ExternalizableObject();

        byte[]  buf = serialize(obj);
        assertEquals(deserialize(buf), obj);

        obj = new SerializableObject();
        buf = serialize(obj);
        assertEquals(deserialize(buf), obj);
    }

    private static Object deserialize(byte[] buf) throws Exception {
        return new DroolsObjectInputStream(new ByteArrayInputStream(buf)).readObject();
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream   bytes   = new ByteArrayOutputStream();
        ObjectOutput            out = new DroolsObjectOutputStream(bytes);

        out.writeObject(obj);
        out.flush();
        out.close();

        return bytes.toByteArray();
    }

    private static Object unmarshal(byte[] buf) throws Exception {
        return new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();
    }

    private static byte[] marshal(Object obj) throws IOException {
        ByteArrayOutputStream   bytes   = new ByteArrayOutputStream();
        ObjectOutput            out = new ObjectOutputStream(bytes);

        out.writeObject(obj);
        out.flush();
        out.close();

        return bytes.toByteArray();
    }

    public void testStreaming() throws Exception {
        Package pkg = new Package("test");

        byte[]  buf = marshal(pkg);
        assertEquals(unmarshal(buf), pkg);

        buf = serialize(pkg);
        assertEquals(deserialize(buf), pkg);
    }
}
