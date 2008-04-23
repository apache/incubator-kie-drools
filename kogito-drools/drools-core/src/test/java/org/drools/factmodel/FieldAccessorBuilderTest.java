package org.drools.factmodel;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FieldAccessorBuilderTest extends TestCase {
    FieldAccessorBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        this.builder = new FieldAccessorBuilder();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'br.com.auster.common.asm.FieldAccessorBuilder.buildFieldAccessor(ClassDefinition, FieldDefinition)'
     */
    public void testBuildFieldAccessor() {
        try {
            FieldAccessor intAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                           "intAttr" ).newInstance();
            FieldAccessor strAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                           "strAttr" ).newInstance();

            String testString1 = "TestAttr1";
            String testString2 = "TestAttr2";
            TestClass instance = new TestClass();
            instance.setIntAttr( 10 );
            instance.setStrAttr( testString1 );

            Assert.assertEquals( "Error reading int attr",
                                 10,
                                 ((Integer) intAccessor.getValue( instance )).intValue() );
            Assert.assertEquals( "Error reading String attr",
                                 testString1,
                                 strAccessor.getValue( instance ) );

            intAccessor.setValue( instance,
                                  new Integer( 50 ) );
            strAccessor.setValue( instance,
                                  testString2 );

            Assert.assertEquals( "Error setting int attr",
                                 50,
                                 instance.getIntAttr() );
            Assert.assertEquals( "Error setting String attr",
                                 testString2,
                                 instance.getStrAttr() );

            Assert.assertEquals( "Error reading int attr",
                                 50,
                                 ((Integer) intAccessor.getValue( instance )).intValue() );
            Assert.assertEquals( "Error reading String attr",
                                 testString2,
                                 strAccessor.getValue( instance ) );

        } catch ( Exception e ) {
            e.printStackTrace();
            Assert.fail( "No exception is supposed to be generated when creating field accessor: " + e );
        }
    }

    public void testNullOnPrimitives() {
        try {
            FieldAccessor intAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                    "intAttr" ).newInstance();
            FieldAccessor strAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                    "strAttr" ).newInstance();
            FieldAccessor byteAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                     "byteAttr" ).newInstance();
            FieldAccessor booleanAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                        "booleanAttr" ).newInstance();
            FieldAccessor charAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                     "charAttr" ).newInstance();
            FieldAccessor doubleAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                       "doubleAttr" ).newInstance();
            FieldAccessor floatAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                      "floatAttr" ).newInstance();
            FieldAccessor longAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                     "longAttr" ).newInstance();
            FieldAccessor shortAccessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( TestClass.class,
                                                                                      "shortAttr" ).newInstance();

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

            Assert.assertEquals( "Error setting attr",
                                 0,
                                 instance.getIntAttr() );
            Assert.assertNull( "Error setting attr",
                               instance.getStrAttr() );
            Assert.assertEquals( "Error setting attr",
                                 0,
                                 instance.getByteAttr() );
            Assert.assertEquals( "Error setting attr",
                                 false,
                                 instance.isBooleanAttr() );
            Assert.assertEquals( "Error setting attr",
                                 '\0',
                                 instance.getCharAttr() );
            Assert.assertEquals( "Error setting attr",
                                 0.0d,
                                 instance.getDoubleAttr(),
                                 0.1d );
            Assert.assertEquals( "Error setting attr",
                                 0.0f,
                                 instance.getFloatAttr(),
                                 0.1f );
            Assert.assertEquals( "Error setting attr",
                                 0l,
                                 instance.getLongAttr() );
            Assert.assertEquals( "Error setting attr",
                                 (short) 0,
                                 instance.getShortAttr() );

            Assert.assertEquals( "Error reading int attr",
                                 0,
                                 ((Integer) intAccessor.getValue( instance )).intValue() );
            Assert.assertNull( "Error reading String attr",
                               strAccessor.getValue( instance ) );
            Assert.assertEquals( "Error reading attr",
                                 0,
                                 ((Byte) byteAccessor.getValue( instance )).byteValue() );
            Assert.assertEquals( "Error reading attr",
                                 false,
                                 ((Boolean) booleanAccessor.getValue( instance )).booleanValue() );
            Assert.assertEquals( "Error reading attr",
                                 '\0',
                                 ((Character) charAccessor.getValue( instance )).charValue() );
            Assert.assertEquals( "Error reading attr",
                                 0.0d,
                                 ((Double) doubleAccessor.getValue( instance )).doubleValue(),
                                 0.1d );
            Assert.assertEquals( "Error reading attr",
                                 0.0f,
                                 ((Float) floatAccessor.getValue( instance )).floatValue(),
                                 0.1f );
            Assert.assertEquals( "Error reading attr",
                                 0l,
                                 ((Long) longAccessor.getValue( instance )).longValue() );
            Assert.assertEquals( "Error reading attr",
                                 (short) 0,
                                 ((Short) shortAccessor.getValue( instance )).shortValue() );

        } catch ( Exception e ) {
            e.printStackTrace();
            Assert.fail( "No exception is supposed to be generated when creating field accessor: " + e );
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
