package org.drools.base;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FieldImplTest extends TestCase {
    FieldImpl field1;
    FieldImpl field2;
    FieldImpl field3;
    FieldImpl field4;
    FieldImpl field5;

    protected void setUp() throws Exception {
        super.setUp();
        this.field1 = new FieldImpl( null );
        this.field2 = new FieldImpl( null );
        this.field3 = new FieldImpl( "A" );
        this.field4 = new FieldImpl( "A" );
        this.field5 = new FieldImpl( "B" );

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        Assert.assertEquals( field1,
                             field1 );
        Assert.assertEquals( field1,
                             field2 );
        Assert.assertEquals( field3,
                             field3 );
        Assert.assertEquals( field3,
                             field4 );
        Assert.assertFalse( field1.equals( field3 ) );
        Assert.assertFalse( field3.equals( field1 ) );
        Assert.assertFalse( field3.equals( field5 ) );
    }

    /*
     * Test method for 'org.drools.base.FieldImpl.hashCode()'
     */
    public void testHashCode() {
        Assert.assertEquals( field1.hashCode(),
                             field1.hashCode() );
        Assert.assertEquals( field1.hashCode(),
                             field2.hashCode() );
        Assert.assertEquals( field3.hashCode(),
                             field3.hashCode() );
        Assert.assertEquals( field3.hashCode(),
                             field4.hashCode() );
        Assert.assertFalse( field1.hashCode() == field3.hashCode() );
        Assert.assertFalse( field3.hashCode() == field1.hashCode() );
        Assert.assertFalse( field3.hashCode() == field5.hashCode() );
    }

    /*
     * Test method for 'org.drools.base.FieldImpl.equals(Object)'
     */
    public void testEqualsObject() {

    }

}
