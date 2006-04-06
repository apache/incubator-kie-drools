package org.drools.base;

import junit.framework.TestCase;

import org.drools.util.asm.BeanInherit;
import org.drools.util.asm.InterfaceChild;
import org.drools.util.asm.TestAbstract;
import org.drools.util.asm.TestAbstractImpl;
import org.drools.util.asm.TestBean;
import org.drools.util.asm.TestInterface;
import org.drools.util.asm.TestInterfaceImpl;

public class ClassFieldExtractorTest extends TestCase {

    public void testBasic() throws Exception {
        TestBean obj = new TestBean();
        obj.setBlah(false);
        obj.setSomething("no");
        
        ClassFieldExtractor ext = new ClassFieldExtractor(TestBean.class, "blah");
        assertEquals(false, ((Boolean)ext.getValue( obj )).booleanValue());
        
    }
    
    public void testInterface() throws Exception {
        
        TestInterface obj = new TestInterfaceImpl();
        ClassFieldExtractor ext = new ClassFieldExtractor(TestInterface.class, "something");
        
        assertEquals("foo", (String)ext.getValue( obj ));
        
    }    
    
    public void testAbstract() throws Exception {
        
        ClassFieldExtractor ext = new ClassFieldExtractor(TestAbstract.class, "something");
        TestAbstract obj = new TestAbstractImpl();
        assertEquals("foo", (String)ext.getValue( obj ));
        
    }     
    
    public void testInherited() throws Exception {
        ClassFieldExtractor ext = new ClassFieldExtractor(BeanInherit.class, "text" );
        BeanInherit obj = new BeanInherit();
        assertEquals("hola", (String)ext.getValue( obj ));
        
    }    
    
    public void testMultipleInterfaces() throws Exception {
        ConcreteChild obj = new ConcreteChild();
        ClassFieldExtractor ext = new ClassFieldExtractor(InterfaceChild.class, "foo");
        assertEquals(42, ((Integer)ext.getValue( obj )).intValue());
    }
    
}
