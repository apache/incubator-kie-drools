package org.drools.util.asm;

import junit.framework.TestCase;


public class FieldAccessorGeneratorTest extends TestCase {

    public void testBasic() throws Exception {
        FieldAccessorGenerator gen = new FieldAccessorGenerator();
        FieldAccessor ac = gen.newInstanceFor(TestObject.class);
        assertNotNull(ac);
        
        TestObject obj = new TestObject();
        obj.setHappy(true);
        obj.setPersonName("michael");
        obj.setPersonAge(31);
        
        assertEquals(true, ((Boolean)ac.getFieldByIndex(obj, 0)).booleanValue());
        assertEquals(31, ((Integer)ac.getFieldByIndex(obj, 1)).intValue());
        assertEquals("michael", ac.getFieldByIndex(obj, 2));
        
    }
    
    public void testAnother() throws Exception {
        FieldAccessorGenerator gen = new FieldAccessorGenerator();
        FieldAccessor ac = gen.getInstanceFor(TestBean.class);
        TestBean obj = new TestBean();
        obj.setBlah(false);
        obj.setSomething("no");
        assertEquals(false, ((Boolean)ac.getFieldByIndex(obj, 0)).booleanValue());
        
        
        //check its being cached
        FieldAccessor ac2 = gen.getInstanceFor(TestBean.class);
        assertEquals(ac, ac2);
    }
    
}
