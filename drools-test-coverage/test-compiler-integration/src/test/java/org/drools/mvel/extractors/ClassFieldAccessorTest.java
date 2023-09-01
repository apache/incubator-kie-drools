/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.extractors;

import org.drools.mvel.accessors.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.core.base.ConcreteChild;
import org.drools.core.util.asm.BeanInherit;
import org.drools.core.util.asm.InterfaceChild;
import org.drools.core.util.asm.TestAbstract;
import org.drools.core.util.asm.TestAbstractImpl;
import org.drools.core.util.asm.TestBean;
import org.drools.core.util.asm.TestInterface;
import org.drools.core.util.asm.TestInterfaceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;

public class ClassFieldAccessorTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testBasic() throws Exception {
        final Object[] objArray = new Object[1];

        final TestBean obj = new TestBean();
        obj.setBlah( false );
        obj.setSomething( "no" );
        obj.setObjArray( objArray );

        final ClassFieldReader ext = store.getReader( TestBean.class,
                                                      "blah" );
        assertThat(((Boolean) ext.getValue(null,
                obj)).booleanValue()).isEqualTo(false);

        final ClassFieldReader ext2 = store.getReader( TestBean.class,
                                                       "fooBar" );
        assertThat(ext2.getValue(null,
                obj)).isEqualTo("fooBar");

        final ClassFieldReader ext3 = store.getReader( TestBean.class,
                                                       "objArray" );
        assertThat(ext3.getValue(null,
                obj)).isEqualTo(objArray);

    }

    @Test
    public void testInterface() throws Exception {

        final TestInterface obj = new TestInterfaceImpl();
        final ClassFieldReader ext = store.getReader( TestInterface.class,
                                                      "something" );

        assertThat((String) ext.getValue(null,
                obj)).isEqualTo("foo");

    }

    @Test
    public void testAbstract() throws Exception {

        final ClassFieldReader ext = store.getReader( TestAbstract.class,
                                                      "something" );
        final TestAbstract obj = new TestAbstractImpl();
        assertThat((String) ext.getValue(null,
                obj)).isEqualTo("foo");

    }

    @Test
    public void testInherited() throws Exception {
        final ClassFieldReader ext = store.getReader( BeanInherit.class,
                                                      "text" );
        final BeanInherit obj = new BeanInherit();
        assertThat((String) ext.getValue(null,
                obj)).isEqualTo("hola");

    }

    @Test
    public void testMultipleInterfaces() throws Exception {
        final ConcreteChild obj = new ConcreteChild();
        final ClassFieldReader ext = store.getReader( InterfaceChild.class,
                                                      "foo" );
        assertThat(((Number) ext.getValue(null,
                obj)).intValue()).isEqualTo(42);
    }

    @Test
    public void testLong() throws Exception {
        final ClassFieldReader ext = store.getReader( TestBean.class,
                                                      "longField" );
        final TestBean bean = new TestBean();
        assertThat(((Number) ext.getValue(null,
                bean)).longValue()).isEqualTo(424242);
    }

    @Test
    public void testNonExistentField() throws Exception {
        final Object[] objArray = new Object[1];

        final TestBean obj = new TestBean();
        obj.setBlah( false );
        obj.setSomething( "no" );
        obj.setObjArray( objArray );

        ClassFieldReader ext = store.getReader( TestBean.class,
                                                "xyz" );
        assertThat(ext).isNull();
    }

    @Test
    public void testBuildFieldAccessor() {
        try {
            ClassFieldAccessor intAccessor = store.getAccessor( TestClass.class,
                                                                "intAttr" );
            ClassFieldAccessor strAccessor = store.getAccessor( TestClass.class,
                                                                "strAttr" );

            String testString1 = "TestAttr1";
            String testString2 = "TestAttr2";
            TestClass instance = new TestClass();
            instance.setIntAttr( 10 );
            instance.setStrAttr( testString1 );

            assertThat(((Integer) intAccessor.getValue(instance)).intValue()).as("Error reading int attr").isEqualTo(10);
            assertThat(strAccessor.getValue(instance)).as("Error reading String attr").isEqualTo(testString1);

            intAccessor.setValue( instance,
                                  new Integer( 50 ) );
            strAccessor.setValue( instance,
                                  testString2 );

            assertThat(instance.getIntAttr()).as("Error setting int attr").isEqualTo(50);
            assertThat(instance.getStrAttr()).as("Error setting String attr").isEqualTo(testString2);

            assertThat(strAccessor.getValue(instance)).as("Error reading String attr").isEqualTo(testString2);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception is supposed to be generated when creating field accessor: " + e );
        }
    }

    @Test
    public void testNullOnPrimitives() {
        try {
            ClassFieldAccessor intAccessor = store.getAccessor( TestClass.class,
                                                                "intAttr" );
            ClassFieldAccessor strAccessor = store.getAccessor( TestClass.class,
                                                                "strAttr" );
            ClassFieldAccessor byteAccessor = store.getAccessor( TestClass.class,
                                                                 "byteAttr" );
            ClassFieldAccessor booleanAccessor = store.getAccessor( TestClass.class,
                                                                    "booleanAttr" );
            ClassFieldAccessor charAccessor = store.getAccessor( TestClass.class,
                                                                 "charAttr" );
            ClassFieldAccessor doubleAccessor = store.getAccessor( TestClass.class,
                                                                   "doubleAttr" );
            ClassFieldAccessor floatAccessor = store.getAccessor( TestClass.class,
                                                                  "floatAttr" );
            ClassFieldAccessor longAccessor = store.getAccessor( TestClass.class,
                                                                 "longAttr" );
            ClassFieldAccessor shortAccessor = store.getAccessor( TestClass.class,
                                                                  "shortAttr" );

            String testString1 = "TestAttr1";
            TestClass instance = new TestClass();
            instance.setIntAttr( 25 );
            instance.setStrAttr( testString1 );
            instance.setByteAttr( (byte) 10 );
            instance.setBooleanAttr( true );
            instance.setCharAttr( 'x' );
            instance.setDoubleAttr( 10.5d );
            instance.setFloatAttr( 40.3f );
            instance.setLongAttr( 43l );
            instance.setShortAttr( (short) 20 );

            intAccessor.setValue( instance,
                                  null );
            strAccessor.setValue( instance,
                                  null );
            byteAccessor.setValue( instance,
                                   null );
            booleanAccessor.setValue( instance,
                                      null );
            charAccessor.setValue( instance,
                                   null );
            doubleAccessor.setValue( instance,
                                     null );
            floatAccessor.setValue( instance,
                                    null );
            longAccessor.setValue( instance,
                                   null );
            shortAccessor.setValue( instance,
                                    null );

            assertThat(instance.getIntAttr()).as("Error setting attr").isEqualTo(0);
            assertThat(instance.getStrAttr()).as("Error setting attr").isNull();
            assertThat(instance.getByteAttr()).as("Error setting attr").isEqualTo((byte)0);
            assertThat(instance.isBooleanAttr()).as("Error setting attr").isEqualTo(false);
            assertThat(instance.getCharAttr()).as("Error setting attr").isEqualTo('\0');
            assertThat(instance.getDoubleAttr()).as("Error setting attr").isCloseTo(0.0d, within(0.1d));
            assertThat(instance.getFloatAttr()).as("Error setting attr").isCloseTo(0.0f, within(0.1f));
            assertThat(instance.getLongAttr()).as("Error setting attr").isEqualTo(0l);
            assertThat(instance.getShortAttr()).as("Error setting attr").isEqualTo((short) 0);

            assertThat(((Integer) intAccessor.getValue(instance)).intValue()).as("Error reading int attr").isEqualTo(0);
            assertThat(strAccessor.getValue(instance)).as("Error reading String attr").isNull();
            assertThat(((Byte) byteAccessor.getValue(instance)).byteValue()).as("Error reading attr").isEqualTo((byte)0);
            assertThat(((Boolean) booleanAccessor.getValue(instance)).booleanValue()).as("Error reading attr").isEqualTo(false);
            assertThat(((Character) charAccessor.getValue(instance)).charValue()).as("Error reading attr").isEqualTo('\0');
            assertThat(((Double) doubleAccessor.getValue(instance)).doubleValue()).as("Error reading attr").isCloseTo(0.0d, within(0.1d));
            assertThat(((Float) floatAccessor.getValue(instance)).floatValue()).as("Error reading attr").isCloseTo(0.0f, within(0.1f));
            assertThat(((Long) longAccessor.getValue(instance)).longValue()).as("Error reading attr").isEqualTo(0l);
            assertThat(((Short) shortAccessor.getValue(instance)).shortValue()).as("Error reading attr").isEqualTo((short) 0);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception is supposed to be generated when creating field accessor: " + e );
        }
    }

    public static class TestClass {
        private int     intAttr;
        private String  strAttr;
        private byte    byteAttr;
        private boolean booleanAttr;
        private char    charAttr;
        private double  doubleAttr;
        private float   floatAttr;
        private long    longAttr;
        private short   shortAttr;

        /**
         * @return Returns the intAttr.
         */
        public int getIntAttr() {
            return intAttr;
        }

        /**
         * @param intAttr The intAttr to set.
         */
        public void setIntAttr(int intAttr) {
            this.intAttr = intAttr;
        }

        /**
         * @return Returns the strAttr.
         */
        public String getStrAttr() {
            return strAttr;
        }

        /**
         * @param strAttr The strAttr to set.
         */
        public void setStrAttr(String strAttr) {
            this.strAttr = strAttr;
        }

        /**
         * @return Returns the booleanAttr.
         */
        public boolean isBooleanAttr() {
            return booleanAttr;
        }

        /**
         * @param booleanAttr The booleanAttr to set.
         */
        public void setBooleanAttr(boolean booleanAttr) {
            this.booleanAttr = booleanAttr;
        }

        /**
         * @return Returns the byteAttr.
         */
        public byte getByteAttr() {
            return byteAttr;
        }

        /**
         * @param byteAttr The byteAttr to set.
         */
        public void setByteAttr(byte byteAttr) {
            this.byteAttr = byteAttr;
        }

        /**
         * @return Returns the charAttr.
         */
        public char getCharAttr() {
            return charAttr;
        }

        /**
         * @param charAttr The charAttr to set.
         */
        public void setCharAttr(char charAttr) {
            this.charAttr = charAttr;
        }

        /**
         * @return Returns the doubleAttr.
         */
        public double getDoubleAttr() {
            return doubleAttr;
        }

        /**
         * @param doubleAttr The doubleAttr to set.
         */
        public void setDoubleAttr(double doubleAttr) {
            this.doubleAttr = doubleAttr;
        }

        /**
         * @return Returns the floatAttr.
         */
        public float getFloatAttr() {
            return floatAttr;
        }

        /**
         * @param floatAttr The floatAttr to set.
         */
        public void setFloatAttr(float floatAttr) {
            this.floatAttr = floatAttr;
        }

        /**
         * @return Returns the longAttr.
         */
        public long getLongAttr() {
            return longAttr;
        }

        /**
         * @param longAttr The longAttr to set.
         */
        public void setLongAttr(long longAttr) {
            this.longAttr = longAttr;
        }

        /**
         * @return Returns the shortAttr.
         */
        public short getShortAttr() {
            return shortAttr;
        }

        /**
         * @param shortAttr The shortAttr to set.
         */
        public void setShortAttr(short shortAttr) {
            this.shortAttr = shortAttr;
        }
    }

}
