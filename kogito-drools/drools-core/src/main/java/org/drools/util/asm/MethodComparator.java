package org.drools.util.asm;

import java.util.List;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Attribute;
import org.drools.asm.ClassReader;
import org.drools.asm.ClassVisitor;
import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.util.TraceMethodVisitor;


/**
 * The purpose of this utility it to check if 2 method implementations are equivalent, by comparing the bytecode.
 * This essentual for node sharing where java semantics are involved.
 * @author Michael Neale
 */
public class MethodComparator {


    /**
     * This actually does the comparing.
     * Class1 and Class2 are class reader instances to the respective classes. method1 and method2 are looked up on the 
     * respective classes and their contents compared.
     */
    public boolean equivalent(String method1, ClassReader class1, String method2, ClassReader class2) {
        
        Tracer visit1 = new Tracer(method1);
        class1.accept( visit1, true );
        TraceMethodVisitor trace1 = visit1.getTrace();
        List list1 = (trace1.getText());
      
        Tracer visit2 = new Tracer(method2);
        class2.accept( visit2, true );
        TraceMethodVisitor trace2 = visit2.getTrace();
        List list2 = trace2.getText();
        
        if (list1.size() != list2.size()) return false;
        
        for (int i = 0; i < list1.size(); i++) {
            if (! 
                    (list1.get( i ).equals( list2.get( i ) ))
                    ) {
                return false;
                
            }
        }
        return true;
        
    }
    
    static class Tracer implements ClassVisitor {

        private TraceMethodVisitor trace;
        private String methodName;
        
        public Tracer(String methodName) {
            this.methodName = methodName;
        }
        
        public void visit(int version,
                          int access,
                          String name,
                          String signature,
                          String superName,
                          String[] interfaces) {
        }

        public AnnotationVisitor visitAnnotation(String desc,
                                                 boolean visible) {
            return new DummyAnnotationVisitor();
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitEnd() {
        }

        public FieldVisitor visitField(int access,
                                       String name,
                                       String desc,
                                       String signature,
                                       Object value) {
            return null;
        }

        public void visitInnerClass(String name,
                                    String outerName,
                                    String innerName,
                                    int access) {
        }

        public MethodVisitor visitMethod(int access,
                                         String name,
                                         String desc,
                                         String signature,
                                         String[] exceptions) {

            if (this.methodName.equals( name )) {
                trace = new TraceMethodVisitor();
                return trace;
            }
            return null;
        }

        public void visitOuterClass(String owner,
                                    String name,
                                    String desc) {
        }

        public void visitSource(String source,
                                String debug) {
        }
        
        public TraceMethodVisitor getTrace() {
            return this.trace;
        }

        
    }
    
    static class DummyAnnotationVisitor implements AnnotationVisitor {

        public void visit(String name,
                          Object value) {
        }

        public AnnotationVisitor visitAnnotation(String name,
                                                 String desc) {
            return new DummyAnnotationVisitor();
        }

        public AnnotationVisitor visitArray(String name) {
            return new DummyAnnotationVisitor();
        }

        public void visitEnd() {}

        public void visitEnum(String name,
                              String desc,
                              String value) {}
        
    }
    
}
