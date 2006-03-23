package org.drools.util.asm;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

public class ClassFieldInspectorTest extends TestCase {

    public void testIt() throws Exception {
        ClassFieldInspector ext = new ClassFieldInspector( Person.class );
        assertEquals(3, ext.getPropertyGetters().size());
        assertEquals("getAge", ((Method) ext.getPropertyGetters().get(0)).getName());
        assertEquals("isHappy", ((Method) ext.getPropertyGetters().get(1)).getName());
        assertEquals("getName", ((Method) ext.getPropertyGetters().get(2)).getName());
        
        Map names = ext.getFieldNames();
        assertNotNull(names);
        assertEquals(3, names.size());
        assertEquals(0, ((Integer)names.get("age")).intValue());
        assertEquals(1, ((Integer)names.get("happy")).intValue());
        assertEquals(2, ((Integer)names.get("name")).intValue());
        
    }
    

    public void testInterface() throws Exception {
        ClassFieldInspector ext = new ClassFieldInspector( TestInterface.class );
        assertEquals(2, ext.getPropertyGetters().size());
        assertEquals("getSomething", ((Method) ext.getPropertyGetters().get(0)).getName());
        assertEquals("getAnother", ((Method) ext.getPropertyGetters().get(1)).getName());
        
      
        
        Map names = ext.getFieldNames();
        assertNotNull(names);
        assertEquals(2, names.size());
        assertEquals(0, ((Integer)names.get("something")).intValue());
        assertEquals(1, ((Integer)names.get("another")).intValue());
        
    }
    
    public void testAbstract() throws Exception {
        ClassFieldInspector ext = new ClassFieldInspector( TestAbstract.class );
        assertEquals(2, ext.getPropertyGetters().size());
        assertEquals("getSomething", ((Method) ext.getPropertyGetters().get(0)).getName());
        assertEquals("getAnother", ((Method) ext.getPropertyGetters().get(1)).getName());
        
        Map names = ext.getFieldNames();
        assertNotNull(names);
        assertEquals(2, names.size());
        assertEquals(0, ((Integer)names.get("something")).intValue());
        assertEquals(1, ((Integer)names.get("another")).intValue());
        
    }    
    
    static class Person {
        private boolean happy;
        private String name;
        private int age;
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public boolean isHappy() {
            return happy;
        }
        public void setHappy(boolean happy) {
            this.happy = happy;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        
        //ignore this as it returns void type
        public void getNotAGetter() {
            return;
        }
        
        //ignore this as private
        private boolean isBogus() {
            return false;
        }
        
        //this will not show up as it is a getter that takes an argument
        public String getAlsoBad(String s) {
            return "ignored";
        }
        
        
        
    }
    
}
