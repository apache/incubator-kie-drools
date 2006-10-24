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

    public void testEagerProxyForClass() {
        try {
            // creating original object
            final String originalType = "stilton";
            final int originalPrice = 15;
            final Cheese cheese = new Cheese( originalType,
                                        originalPrice );

            // creating proxy
            final Class proxy = ShadowProxyFactory.getEagerProxy( Cheese.class );
            final Cheese cheeseProxy = (Cheese) proxy.getConstructor( new Class[]{Cheese.class} ).newInstance( new Object[]{cheese} );

            // proxy is proxying the values
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );

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

    public void testEagerProxyForInterface() {
        try {
            // creating original object
            final String originalType = "stilton";
            final int originalPrice = 15;
            final Cheese cheese = new Cheese( originalType,
                                        originalPrice );

            // creating proxy
            final Class proxy = ShadowProxyFactory.getEagerProxy( CheeseInterface.class );
            final CheeseInterface cheeseProxy = (CheeseInterface) proxy.getConstructor( new Class[]{CheeseInterface.class} ).newInstance( new Object[]{cheese} );

            // proxy is proxying the values
            Assert.assertEquals( originalType,
                                 cheeseProxy.getType() );
            Assert.assertEquals( originalPrice,
                                 cheeseProxy.getPrice() );

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

    public void testEagerProxyForAPIClass() {
        try {
            // creating original object
            final List list = new ArrayList();

            // creating proxy
            final Class proxy = ShadowProxyFactory.getEagerProxy( ArrayList.class );
            final List listProxy = (List) proxy.getConstructor( new Class[]{ArrayList.class} ).newInstance( new Object[]{list} );

            // proxy is proxying the values
            Assert.assertEquals( list,
                                 listProxy );

        } catch ( final Exception e ) {
            fail( "Error: " + e.getMessage() );
        }
    }

}
