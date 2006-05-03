package org.drools.util.asm;

import java.io.InputStream;

import org.drools.asm.ClassReader;

import junit.framework.TestCase;

public class MethodComparerTest extends TestCase {

    public void testMethodCompare() throws Exception {
        MethodComparator comp = new MethodComparator();
        boolean result = comp.equivalent( "evaluate", 
                                          new ClassReader(getClassData(MethodCompareA.class)), 
                                          "evaluate", 
                                          new ClassReader(getClassData(MethodCompareB.class)) );
        assertEquals(true, result);
        
        result = comp.equivalent( "evaluate", 
                                          new ClassReader(getClassData(MethodCompareA.class)), 
                                          "evaluate2", 
                                          new ClassReader(getClassData(MethodCompareA.class)) );
        assertEquals(false, result);
        
        result = comp.equivalent( "evaluate", 
                                  new ClassReader(getClassData(MethodCompareB.class)), 
                                  "evaluate2", 
                                  new ClassReader(getClassData(MethodCompareA.class)) );
        assertEquals(false, result);
        
        result = comp.equivalent( "evaluate", 
                                          new ClassReader(getClassData(MethodCompareB.class)), 
                                          "evaluate", 
                                          new ClassReader(getClassData(MethodCompareA.class)) );
        assertEquals(true, result);
        
        result = comp.equivalent( "evaluate", 
                                          new ClassReader(getClassData(MethodCompareA.class)), 
                                          "evaluate", 
                                          new ClassReader(getClassData(MethodCompareA.class)) );
        assertEquals(true, result);
        

        result = comp.equivalent( "evaluate", 
                                  new ClassReader(getClassData(MethodCompareA.class)), 
                                  "askew", 
                                  new ClassReader(getClassData(MethodCompareA.class)) );
        assertEquals(false, result);
        
        
        
        
    }
    
    
    
    private InputStream getClassData(Class clazz) {
        String name = getResourcePath( clazz );
        return clazz.getResourceAsStream( name );
    }
    
    private String getResourcePath(Class clazz) {
        return "/" + clazz.getName().replaceAll( "\\.",
                                                 "/" ) + ".class";
    }
    
    
    
}
