package org.drools.base;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;

public class ShadowProxyFactoryTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetProxy() {
        try {
            // creating original object
            String originalType = "stilton";
            int originalPrice = 15;
            Cheese cheese = new Cheese(originalType, originalPrice);
            
            // creating proxy
            Class proxy = ShadowProxyFactory.getProxy( Cheese.class );
            Cheese cheeseProxy = (Cheese) proxy.getConstructor( new Class[] { Cheese.class } ).newInstance( new Object[] { cheese } );

            // proxy is proxying the values
            Assert.assertEquals( originalType, cheeseProxy.getType() );
            Assert.assertEquals( originalPrice, cheeseProxy.getPrice() );
            
            // changing original values
            String actualType = "rotten stilton";
            int actualPrice = 1;
            cheese.setType( actualType );
            cheese.setPrice( actualPrice );
            
            // proxy does not see changes
            Assert.assertEquals( actualType, cheese.getType() );
            Assert.assertFalse( actualType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( originalType, cheeseProxy.getType() );
            Assert.assertEquals( actualPrice, cheese.getPrice() );
            Assert.assertFalse( actualPrice == cheeseProxy.getPrice() );
            Assert.assertEquals( originalPrice, cheeseProxy.getPrice() );
            
            // reseting proxy
            ((ShadowProxy) cheeseProxy).resetProxy();
            
            // now proxy see changes
            Assert.assertEquals( actualType, cheese.getType() );
            Assert.assertEquals( actualType, cheeseProxy.getType() );
            Assert.assertFalse( originalType.equals( cheeseProxy.getType() ) );
            Assert.assertEquals( actualPrice, cheese.getPrice() );
            Assert.assertEquals( actualPrice, cheeseProxy.getPrice() );
            Assert.assertFalse( originalPrice == cheeseProxy.getPrice() );
            
        } catch ( Exception e ) {
            fail("Error: "+e.getMessage());
        }
    }
}
