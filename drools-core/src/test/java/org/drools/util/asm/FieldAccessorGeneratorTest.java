package org.drools.util.asm;

import junit.framework.TestCase;


public class FieldAccessorGeneratorTest extends TestCase {

    public void testBasic() throws Exception {
        FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        FieldAccessorMap map = gen.newInstanceFor(TestObject.class);
        FieldAccessor ac = map.getFieldAccessor();
        assertNotNull(ac);
        
        TestObject obj = new TestObject();
        obj.setHappy(true);
        obj.setPersonName("michael");
        obj.setPersonAge(31);
        
        assertEquals(true, ((Boolean)ac.getFieldByIndex(obj, 0)).booleanValue());
        assertEquals(31, ((Integer)ac.getFieldByIndex(obj, 1)).intValue());
        assertEquals("michael", ac.getFieldByIndex(obj, 2));
        
        Integer index = (Integer) map.getFieldNameMap().get("personName");
        assertEquals(2, index.intValue());
        
        index = (Integer) map.getFieldNameMap().get("personAge");
        assertEquals(1, index.intValue());
        
        index = (Integer) map.getFieldNameMap().get("happy");
        assertEquals(0, index.intValue());        
        
        
    }
    
    public void testAnother() throws Exception {
        FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        FieldAccessor ac = gen.getInstanceFor(TestBean.class).getFieldAccessor();
        TestBean obj = new TestBean();
        obj.setBlah(false);
        obj.setSomething("no");
        assertEquals(false, ((Boolean)ac.getFieldByIndex(obj, 0)).booleanValue());
        
        
        //check its being cached
        FieldAccessor ac2 = gen.getInstanceFor(TestBean.class).getFieldAccessor();
        assertEquals(ac, ac2);
    }
    
    public void testInterface() throws Exception {
        FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        FieldAccessorMap map = gen.newInstanceFor(TestInterface.class);
        FieldAccessor ac = map.getFieldAccessor();
        assertNotNull(ac);
        
        TestInterface obj = new TestInterfaceImpl();
        
        assertEquals("foo", (String)ac.getFieldByIndex(obj, 0));
        assertEquals(42, ((Integer)ac.getFieldByIndex(obj, 1)).intValue());
        
        Integer index = (Integer) map.getFieldNameMap().get("something");
        assertEquals(0, index.intValue());
        
        index = (Integer) map.getFieldNameMap().get("another");
        assertEquals(1, index.intValue());
        
        
    }    
    
    public void testAbstract() throws Exception {
        FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        FieldAccessorMap map = gen.newInstanceFor(TestAbstract.class);
        FieldAccessor ac = map.getFieldAccessor();
        assertNotNull(ac);
        
        TestAbstract obj = new TestAbstractImpl();
        
        assertEquals(42, ((Integer)ac.getFieldByIndex(obj, 1)).intValue());
        assertEquals("foo", (String)ac.getFieldByIndex(obj, 0));
        
        
        Integer index = (Integer) map.getFieldNameMap().get("something");
        assertEquals(0, index.intValue());
        
        index = (Integer) map.getFieldNameMap().get("another");
        assertEquals(1, index.intValue());
        
        
    }     
    
    public void testInherited() throws Exception {
        FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        FieldAccessorMap map = gen.newInstanceFor(BeanInherit.class);
        FieldAccessor ac = map.getFieldAccessor();
        assertNotNull(ac);
        
        BeanInherit obj = new BeanInherit();
        
        
        assertEquals(42, ((Integer)ac.getFieldByIndex(obj, map.getIndex( "number" ))).intValue());
        assertEquals("hola", (String)ac.getFieldByIndex(obj, map.getIndex( "text" )));
        
    }
    
}
