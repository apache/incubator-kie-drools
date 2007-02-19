package org.drools.base;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.CheeseInterface;

public class ShadowProxyFactoryTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProxyForClass() {
        try {
            // creating original object
            final String originalType = "stilton";
            final int originalPrice = 15;
            final Cheese cheese = new Cheese( originalType,
                                        originalPrice );

            // creating proxy
            final Class proxy = ShadowProxyFactory.getProxy( Cheese.class );
            final Cheese cheeseProxy = (Cheese) proxy.getConstructor( new Class[]{Cheese.class} ).newInstance( new Object[]{cheese} );

            // proxy is proxying the values
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );
            Assert.assertSame( cheese, ((ShadowProxy)cheeseProxy).getShadowedObject() );
            
            // proxy must recongnize the original object on equals()/hashcode() calls
            //Assert.assertEquals( cheeseProxy.hashCode(), cheese.hashCode() );
            Assert.assertEquals( cheeseProxy, cheese );

            // changing original values
            final String actualType = "rotten stilton";
            final int actualPrice = 1;
            cheese.setType( actualType );
            cheese.setPrice( actualPrice );

            // proxy does not see changes
            Assert.assertEquals( actualType,
                                 cheese.getType() );
            Assert.assertFalse( actualType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( actualPrice,
                                 cheese.getPrice() );
            Assert.assertFalse( actualPrice == cheeseProxy.getPrice() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );

            // reseting proxy
            ((ShadowProxy) cheeseProxy).updateProxy();

            // now proxy see changes
            Assert.assertEquals( actualType,
                                 cheese.getType() );
            Assert.assertEquals( actualType,
                                 cheeseProxy.getType() );
            Assert.assertFalse( originalType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( actualPrice,
                                 cheese.getPrice() );
            Assert.assertEquals( actualPrice,
                                 cheeseProxy.getPrice() );
            Assert.assertFalse( originalPrice == cheeseProxy.getPrice() );

        } catch ( final Exception e ) {
            fail( "Error: " + e.getMessage() );
        }
    }

    public void testProxyForInterface() {
        try {
            // creating original object
            final String originalType = "stilton";
            final int originalPrice = 15;
            final Cheese cheese = new Cheese( originalType,
                                        originalPrice );

            // creating proxy
            final Class proxy = ShadowProxyFactory.getProxy( CheeseInterface.class );
            final CheeseInterface cheeseProxy = (CheeseInterface) proxy.getConstructor( new Class[]{CheeseInterface.class} ).newInstance( new Object[]{cheese} );

            // proxy is proxying the values
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );
            Assert.assertSame( cheese, ((ShadowProxy)cheeseProxy).getShadowedObject() );

            // changing original values
            final String actualType = "rotten stilton";
            final int actualPrice = 1;
            cheese.setType( actualType );
            cheese.setPrice( actualPrice );

            // proxy does not see changes
            Assert.assertEquals( actualType,
                                 cheese.getType() );
            Assert.assertFalse( actualType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( actualPrice,
                                 cheese.getPrice() );
            Assert.assertFalse( actualPrice == cheeseProxy.getPrice() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );

            // reseting proxy
            ((ShadowProxy) cheeseProxy).updateProxy();

            // now proxy see changes
            Assert.assertEquals( actualType,
                                 cheese.getType() );
            Assert.assertEquals( actualType,
                                 cheeseProxy.getType() );
            Assert.assertFalse( originalType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( actualPrice,
                                 cheese.getPrice() );
            Assert.assertEquals( actualPrice,
                                 cheeseProxy.getPrice() );
            Assert.assertFalse( originalPrice == cheeseProxy.getPrice() );

        } catch ( final Exception e ) {
            fail( "Error: " + e.getMessage() );
        }
    }

    public void testProxyForAPIClass() {
        try {
            // creating original object
            final List list = new ArrayList();

            // creating proxy
            final Class proxy = ShadowProxyFactory.getProxy( ArrayList.class );
            final List listProxy = (List) proxy.getConstructor( new Class[]{ArrayList.class} ).newInstance( new Object[]{list} );

            // proxy is proxying the values
            Assert.assertEquals( list,
                                 listProxy );
            Assert.assertSame( list, ((ShadowProxy)listProxy).getShadowedObject() );

        } catch ( final Exception e ) {
            fail( "Error: " + e.getMessage() );
        }
    }

    public void testEqualsHashCodeForClass() {
        try {
            // creating original object
            final String originalType = "stilton";
            final int originalPrice = 15;
            final Cheese cheese = new Cheese( originalType,
                                        originalPrice );

            // creating proxy
            final Class proxy = ShadowProxyFactory.getProxy( Cheese.class );
            final Cheese cheeseProxy1 = (Cheese) proxy.getConstructor( new Class[]{Cheese.class} ).newInstance( new Object[]{cheese} );
            final Cheese cheeseProxy2 = (Cheese) proxy.getConstructor( new Class[]{Cheese.class} ).newInstance( new Object[]{cheese} );

            int cheesehash = cheeseHashCode( cheese );
            Assert.assertEquals( cheeseProxy1, cheeseProxy2 );
            Assert.assertEquals( cheeseProxy2, cheeseProxy1 );
            Assert.assertEquals( cheesehash, cheeseProxy1.hashCode() );
            
            // changing original values
            final String actualType = "rotten stilton";
            final int actualPrice = 1;
            cheese.setType( actualType );
            cheese.setPrice( actualPrice );

            Assert.assertEquals( cheesehash, cheeseProxy1.hashCode() );
            
            // updating proxy1
            ((ShadowProxy) cheeseProxy1).updateProxy();
            
            Assert.assertEquals( cheeseHashCode( cheese ), cheeseProxy1.hashCode() );

            // now they are different
            Assert.assertFalse( cheeseProxy1.equals( cheeseProxy2 ) );
            Assert.assertFalse( cheeseProxy2.equals( cheeseProxy1 ) );
            
            // updating proxy2
            ((ShadowProxy) cheeseProxy2).updateProxy();

            // now they are equal again
            Assert.assertEquals( cheeseProxy1, cheeseProxy2 );
            Assert.assertEquals( cheeseProxy2, cheeseProxy1 );
            
        } catch ( final Exception e ) {
            fail( "Error: " + e.getMessage() );
        }
    }

// TODO: find a new way to test hashcode
//    public void testEqualsHashCodeForClass2() {
//        try {
//            // creating original object
//            final TestBean bean = new TestBean();
//
//            // creating proxy
//            final Class proxy = ShadowProxyFactory.getProxy( TestBean.class );
//            final TestBean beanProxy1 = (TestBean) proxy.getConstructor( new Class[]{TestBean.class} ).newInstance( new Object[]{bean} );
//            final TestBean beanProxy2 = (TestBean) proxy.getConstructor( new Class[]{TestBean.class} ).newInstance( new Object[]{bean} );
//
//            Assert.assertEquals( beanProxy1, beanProxy2 );
//            Assert.assertEquals( beanProxy2, beanProxy1 );
//            Assert.assertEquals( -130900686 , beanProxy1.hashCode() );
//            
//        } catch ( final Exception e ) {
//            fail( "Error: " + e.getMessage() );
//        }
//    }

    private int cheeseHashCode(Cheese cheese) {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((cheese.getType() == null) ? 0 : cheese.getType().hashCode());
        result = PRIME * result + cheese.getPrice();
        return result;
    }

}
