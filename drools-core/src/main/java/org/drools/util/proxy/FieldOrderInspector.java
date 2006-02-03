package org.drools.util.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.CodeVisitor;


/**
 * Visit a POJO user class, and extract the property getter methods that are public, in the 
 * order in which they are declared actually in the class itself (not using introspection).
 * 
 * @author Michael Neale
 */
public class FieldOrderInspector {

    private List methods;
    
    /**
     * @param clazz The class that the fields to be shadowed are extracted for.
     * @throws IOException
     */
    public FieldOrderInspector(Class clazz) throws IOException {
        String name = getResourcePath( clazz );
        InputStream stream = this.getClass().getResourceAsStream(name);
        ClassReader reader = new ClassReader(stream);
        ClassFieldVisitor visitor = new ClassFieldVisitor(clazz);
        reader.accept(visitor, false);
        this.methods = visitor.getPropertyGetters();
    }

    /**
     * Convert it to a form so we can load the bytes from the classpath.
     */
    private String getResourcePath(Class clazz) {
        return "/" + clazz.getName().replaceAll("\\.", "/") + ".class";
    }
    
    /** 
     * Return a list in order of which the getters (and "is") methods were found.
     * This should only be done once when compiling a rulebase ideally.
     */
    public List getPropertyGetters() {
        return methods;
    }
    
    
    /**
     * Using the ASM classfield extractor to pluck it out in the order they appear in the class file.
     * @author Michael Neale
     */
    static class ClassFieldVisitor implements ClassVisitor {

        private static final int PUBLIC_ACCESS = 1;
        private List methodList = new ArrayList();
        private Class clazz;
        
        
        ClassFieldVisitor(Class cls) {
            this.clazz = cls;
        }
        
        
        public List getPropertyGetters() {
            return methodList;
        }
        
        public CodeVisitor visitMethod(int access,
                                       String name,
                                       String desc,
                                       String[] signature,
                                       Attribute attr) {
            //only want public methods that are get or is
            if (access == PUBLIC_ACCESS) {
                if (name.startsWith("get") || name.startsWith("is")) {
                    try {
                        Method method = clazz.getMethod(name, null);
                        methodList.add(method);
                    } catch (NoSuchMethodException e) {
                        //TODO: must be a better way. We only want fields with no args.
                    }
                }
            }
            return null;
        }
        
        
        public void visit(int arg0,
                          int arg1,
                          String arg2,
                          String arg3,
                          String[] arg4,
                          String arg5) {}

        public void visitInnerClass(String arg0,
                                    String arg1,
                                    String arg2,
                                    int arg3) {}

        public void visitField(int access,
                               String arg1,
                               String arg2,
                               Object arg3,
                               Attribute arg4) {}


        public void visitAttribute(Attribute arg0) {}

        public void visitEnd() {}
        
        
    }
}
