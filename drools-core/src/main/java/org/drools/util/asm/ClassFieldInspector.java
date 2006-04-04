package org.drools.util.asm;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Attribute;
import org.drools.asm.ClassReader;
import org.drools.asm.ClassVisitor;

import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;


/**
 * Visit a POJO user class, and extract the property getter methods that are public, in the 
 * order in which they are declared actually in the class itself (not using introspection).
 * 
 * This may be enhanced in the future to allow annotations or perhaps external meta data
 * configure the order of the indexes, as this may provide fine tuning options in special cases.
 * 
 * @author Michael Neale
 */
public class ClassFieldInspector {

    private List methods = new ArrayList(); 
    private Map fieldNames = new HashMap();
    /**
     * @param clazz The class that the fields to be shadowed are extracted for.
     * @throws IOException
     */
    public ClassFieldInspector(Class clazz) throws IOException {
        processClass( clazz );
    }

    /** Walk up the inheritance hierarchy recursively, reading in fields */
    private void processClass(Class clazz) throws IOException {
        String name = getResourcePath( clazz );
        InputStream stream = clazz.getResourceAsStream(name);
        ClassReader reader = new ClassReader(stream);
        ClassFieldVisitor visitor = new ClassFieldVisitor(clazz, methods.size());
        reader.accept(visitor, false);
        this.methods.addAll( visitor.getPropertyGetters() );
        this.fieldNames.putAll( visitor.getFieldNameMap() );
        if (clazz.getSuperclass() != null) {
            processClass(clazz.getSuperclass());
        }
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
     * Return a mapping of the field "names" (ie bean property name convention)
     * to the numerical index by which they can be accessed.
     */
    public Map getFieldNames() {
        return fieldNames;
    }
    
    /**
     * Using the ASM classfield extractor to pluck it out in the order they appear in the class file.
     * @author Michael Neale
     */
    static class ClassFieldVisitor implements ClassVisitor {

        private List methodList = new ArrayList();
        private Class clazz;
        private Map fieldNameMap = new HashMap();
        private int startingIndex = 0;
        
        ClassFieldVisitor(Class cls, int startingIndex) {
            this.clazz = cls;
            this.startingIndex = startingIndex;
        }
        
        
        public List getPropertyGetters() {
            return methodList;
        }
        
        public Map getFieldNameMap() {
            return fieldNameMap;
        }
        
        
        public MethodVisitor visitMethod(int access,
                                         String name,
                                         String desc,
                                         String signature,
                                         String[] exceptions) {
            //only want public methods that start with 'get' or 'is'
            //and have no args, and return a value
            if ((access & Opcodes.ACC_PUBLIC) > 0) {
                if (desc.startsWith( "()" ) && ( name.startsWith("get") || name.startsWith("is") ) ) {
                    try {
                        Method method = clazz.getMethod(name, (Class[]) null);
                        if (method.getReturnType() != void.class) {
                            int fieldIndex = this.methodList.size()+this.startingIndex;                                                           
                            addToMapping(method, fieldIndex);
                        }
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException("Error in getting field access method.");
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


        public void visit(int arg0,
                          int arg1,
                          String arg2,
                          String arg3,
                          String arg4,
                          String[] arg5) {
            
            
        }


        public void visitSource(String arg0,
                                String arg1) {
            
            
        }


        public void visitOuterClass(String arg0,
                                    String arg1,
                                    String arg2) {
            
            
        }


        public AnnotationVisitor visitAnnotation(String arg0,
                                                 boolean arg1) {
            
            return new ClassFieldAnnotationVisitor();
        }


        public FieldVisitor visitField(int arg0,
                                       String arg1,
                                       String arg2,
                                       String arg3,
                                       Object arg4) {
            
            return null;
        }





        private void addToMapping(Method method, int index) {
            String name = method.getName();
            int offset;
            if (name.startsWith("is")) {
                offset = 2;
            } else {
                offset = 3;
            }
            String fieldName = calcFieldName( name, offset );
            if (fieldNameMap.containsKey( fieldName )) {
                //only want it once, the first one thats found
                return; 
            } else {
                this.fieldNameMap.put(fieldName, new Integer(index));
                methodList.add(method);
            }
        }


        private String calcFieldName(String name, int offset) {
            name = name.substring(offset);
            char first = Character.toLowerCase(name.charAt(0));
            name = first + name.substring(1);
            return name;
        }
        
        
    }
    
    /** 
     * This is required for POJOs that have annotations. 
     * It may also come in handy if we want to allow custom annotations for marking field numbers etc.
     */
    static class ClassFieldAnnotationVisitor implements AnnotationVisitor {

        public void visit(String arg0,
                          Object arg1) {
        }

        public void visitEnum(String arg0,
                              String arg1,
                              String arg2) {
        }

        public AnnotationVisitor visitAnnotation(String arg0,
                                                 String arg1) {
            return new ClassFieldAnnotationVisitor();
        }

        public AnnotationVisitor visitArray(String arg0) {
            return new ClassFieldAnnotationVisitor();
        }

        public void visitEnd() {
            
            
        }
        
    }
}
