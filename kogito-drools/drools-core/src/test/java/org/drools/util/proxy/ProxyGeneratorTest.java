package org.drools.util.proxy;

import java.io.IOException;

import junit.framework.TestCase;

public class ProxyGeneratorTest extends TestCase {

    public void testNonShadow() throws IOException {
        Person original = new Person();
        original.setAge(31);
        original.setName("michael");
        
        Person p = (Person) ProxyGenerator.generateProxy(original, false);
        standardTests( original,
                   p );
               
        
        //check that stuff is passed through (bypasses the proxy).
        p.changeName("jo");
        assertEquals("jo", p.getName());
        assertEquals("jo", original.getName());        
    }
    
    public void testShadow() throws IOException {
        Person original = new Person();
        original.setAge(31);
        original.setName("michael");
        
        Person p = (Person) ProxyGenerator.generateProxy(original, true);
        standardTests( original,
                   p );
        
        //check that stuff is passed through, but shadow keeps old copy
        p.changeName("jo");
        assertEquals("michael", p.getName()); //ooh clever...
        assertEquals("jo", original.getName());        
        
    }

    private void standardTests(Person original,
                           Person p) {
        assertTrue(p instanceof FieldIndexAccessor);
        assertNotSame(p, original);
        
        //check we can use the proxy
        assertEquals(original.getName(), p.getName());
        assertEquals(original.getAge(), p.getAge());
        assertEquals(original.isHappy(), p.isHappy());
        
        FieldIndexAccessor s = (FieldIndexAccessor) p;
        
        //check that order of declarations count, and we can access then via the indexaccessor interface
        assertNotNull(p.getName(), s.getField(1));
        assertEquals(p.getAge(), ((Integer)s.getField(2)).intValue());
        assertEquals(p.isHappy(), ((Boolean) s.getField(3)).booleanValue());

        
        //now check that you can get back to the target
        assertTrue(p instanceof TargetAccessor);
        TargetAccessor t = (TargetAccessor) p;
        assertEquals(original, t.getTarget());
    }
    
}
