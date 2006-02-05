package org.drools.util.proxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ProxyGeneratorTest extends TestCase {

    public void testNonShadow() throws IOException {
        Person original = new Person();
        original.setAge(31);
        original.setName("michael");
        
        Person p = (Person) ProxyGenerator.generateProxy(original, false, false);
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
        
        Person p = (Person) ProxyGenerator.generateProxy(original, true, false);
        standardTests( original,
                   p );
        
        //check that stuff is passed through, but shadow keeps old copy
        p.changeName("jo");
        assertEquals("michael", p.getName()); //ooh clever...
        assertEquals("jo", original.getName());        
        
    }
    
    public void testChangeListener() throws IOException {
        Person original = new Person();
        original.setAge(31);
        original.setName("michael");
        
        Person p = (Person) ProxyGenerator.generateProxy(original, false, true);
        standardTests( original,
                   p );
               
        
        //check that stuff is passed through (bypasses the proxy).
        p.changeName("jo");
        assertEquals("jo", p.getName());
        assertEquals("jo", original.getName());
        
        assertTrue(p instanceof ChangeListener);
        ChangeListener cl = (ChangeListener) p;
        
        TestListener listener = new TestListener();
        cl.addPropertyChangeListener(listener);
        
        p.setAge(42);
        assertEquals(42, p.getAge()); //just to make sure it actually worked
        assertEquals(42, original.getAge());
        assertEquals(1, listener.events.size());
        PropertyChangeEvent event = (PropertyChangeEvent) listener.events.get(0);
        assertEquals("age", event.getPropertyName());
        assertEquals(new Integer(31), event.getOldValue());
        assertEquals(new Integer(42), event.getNewValue());
        
        
        p.setName("chloe");
        assertEquals(2, listener.events.size());
        event = (PropertyChangeEvent) listener.events.get(1);
        assertEquals("name", event.getPropertyName());
        assertEquals("jo", event.getOldValue());
        assertEquals("chloe", event.getNewValue());
        
        cl.removePropertyChangeListener(listener);
        p.setName("done");
        assertEquals(2, listener.events.size());
        
    }
    
    public void testOtherObject() throws Exception {
        Person p = new Person();
        Object personProxy = ProxyGenerator.generateProxy(p, false, false);
        
        Child c = new Child(new BigInteger("321321321"), "Chloe Emma Neale");
        Child proxyChild = (Child) ProxyGenerator.generateProxy(c, true, false);
        assertNotSame(proxyChild, c);
        assertEquals(proxyChild.getName(), c.getName());
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
    
    public void testPerf() throws Exception {
        
        Person o = new Person();
        o.setName("michael");
        o.setAge(31);
        
        Person p = (Person) ProxyGenerator.generateProxy(o, true, false);
        
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String s = p.getName() + i;
        }
        System.out.println("Time taken proxy: " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String s = o.getName() + i;
        }
        System.out.println("Time taken native: " + (System.currentTimeMillis() - start));        
        
        
    }
    
    static class TestListener implements PropertyChangeListener {

        List events = new ArrayList();
        
        public void propertyChange(PropertyChangeEvent event) {
            events.add(event);
        }
        
    }
    
}
