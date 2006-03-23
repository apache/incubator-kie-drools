package org.drools.base;

import org.drools.spi.FieldExtractor;
import org.drools.util.asm.TestAbstract;
import org.drools.util.asm.TestAbstractImpl;
import org.drools.util.asm.TestInterface;
import org.drools.util.asm.TestInterfaceImpl;

import junit.framework.TestCase;

public class ClassFieldExtractorFactoryTest extends TestCase {

    public void testIt() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "name" );
        assertEquals(0, ex.getIndex());
        assertEquals("michael", ex.getValue( new TestBean() ));
        ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "age" );
        assertEquals(1, ex.getIndex());
        assertEquals(new Integer(42), ex.getValue( new TestBean() ));
        
    }
    
    public void testInterface() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestInterface.class, "something" );
        assertEquals(0, ex.getIndex());
        assertEquals("foo", ex.getValue( new TestInterfaceImpl() ));
    }
    
    public void testAbstract() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestAbstract.class, "something" );
        assertEquals(0, ex.getIndex());
        assertEquals("foo", ex.getValue( new TestAbstractImpl() ));
    }   
    
    

    
}

