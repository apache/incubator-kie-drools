package org.drools.util.asm;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.asm.ClassWriter;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;

/**
 * Will provide implementations of FieldAccessor as needed.
 * 
 * There is no proxying involved.
 * 
 * Uses ASM to generate a implementation of a FieldAccessor. 
 * Uses tableswitch so it is as fast as humanly possible.
 * @author Michael Neale
 */
public class FieldAccessorGenerator {

    //used to make sure generated classes are unique...
    private static final String GEN_PACKAGE_PREFIX = "org.drools.fieldaccess.";
    private final Map cache = new HashMap();
    
    /**
     * Looks up an instance of a field accessor for the given class.
     * If none is found it will generate one, and then cache it.
     */
    public FieldAccessor getInstanceFor(Class cls) throws Exception {
        Object obj = cache.get(cls);
        if (obj == null) {
            obj = newInstanceFor(cls);
            cache.put(cls, obj);
        }
        return (FieldAccessor) obj;
    }
    
    /**
     * Generate a new implementation for of a FieldAccessor for the given class.
     * No caching. Uses ASM.
     */
    public FieldAccessor newInstanceFor(Class cls) throws Exception {
        
        ClassFieldInspector inspector = new ClassFieldInspector(cls);
        Method[] getters = (Method[]) inspector.getPropertyGetters().toArray(new Method[] {});
        
        String generatedClassName = GEN_PACKAGE_PREFIX + cls.getName();
        
        byte[] generatedClass = AccessorClassFactory.generateClass(getters, cls, generatedClassName);
        ByteArrayClassLoader cl = new ByteArrayClassLoader( Thread.currentThread().getContextClassLoader() );
        cl.addByteArray( generatedClassName,  generatedClass);
        return (FieldAccessor) cl.loadClass( generatedClassName ).newInstance();
    }



    /**
     * OK, deep breaths, this is where it all happens...
     * If you don't know ASM, and a bit about bytecode, then move along, theres nothing to see here.
     * 
     * @author Michael Neale
     */
    static class AccessorClassFactory
        implements
        Opcodes {

        private static final String GET_FIELD_BY_INDEX_METHOD_NAME = "getFieldByIndex";

        private static String getShortName(Class cls) {
            String name = cls.getName();
            String packageName = cls.getPackage().getName();
            return name.substring(packageName.length() + 1, name.length());
        }        
        
        public static byte[] generateClass(Method getters[], Class targetClass, String generatedClassName) throws Exception {

            ClassWriter cw = new ClassWriter( true );

            generatedClassName = generatedClassName.replaceAll("\\.", "/");
            
            cw.visit( V1_2,
                      ACC_PUBLIC + ACC_SUPER,
                      generatedClassName,
                      null,
                      "java/lang/Object",
                      new String[]{Type.getInternalName(FieldAccessor.class)});

            cw.visitSource( getShortName(targetClass) + ".java",
                            null );

            doConstructor( cw );
            
            doMethods( cw, Type.getInternalName(targetClass), getters );

            cw.visitEnd();

            return cw.toByteArray();
        }

        private static void doMethods(ClassWriter cw, String targetType, Method[] getters) {
            
             
            
            
            MethodVisitor mv;
            mv = cw.visitMethod( ACC_PUBLIC,
                                 GET_FIELD_BY_INDEX_METHOD_NAME,
                                 "(Ljava/lang/Object;I)Ljava/lang/Object;",
                                 null,
                                 null );
            mv.visitCode();
            Label entry = new Label();
            mv.visitLabel( entry );
            mv.visitVarInsn( ALOAD,
                             1 );
            mv.visitTypeInsn( CHECKCAST,
                              targetType );
            int target = 3;
            mv.visitVarInsn( ASTORE,
                             target ); //this is the actual casted object
            Label start = new Label();
            mv.visitLabel( start );

            mv.visitVarInsn( ILOAD,
                             2 ); //the index, I think.
            //END BOILERPLATE
            
            Label[] switchItems = new Label[getters.length];
            for (int i= 0; i < getters.length; i++) {
                switchItems[i] = new Label();
            }
            
            //setup switch statment (with default)
            Label defaultSwitch = new Label();
            mv.visitTableSwitchInsn( 0,
                                     switchItems.length - 1,
                                     defaultSwitch,
                                     switchItems );
            
            
            //START switch items
            for (int i= 0; i < getters.length; i++) {
                Method method = getters[i];
                if (method.getReturnType().isPrimitive()) {
                    doSwitchItemBoxed( mv, switchItems[i],
                                       target, targetType, method.getName(), method.getReturnType());                    
                } else {
                    doSwitchItemObject(mv, switchItems[i], target, targetType, method.getName(), method.getReturnType());
                }
            }            
            
            //the default item...
            mv.visitLabel( defaultSwitch );
            mv.visitInsn( ACONST_NULL );
            mv.visitInsn( ARETURN );
            
            Label endLabel = new Label();
            mv.visitLabel( endLabel );

            mv.visitMaxs( 0,0 ); //dummy values, its calculated anyway
            mv.visitEnd();
        }

        /** a switch item that requires autoboxing */
        private static void doSwitchItemBoxed(MethodVisitor mv, Label switchItem,
                                              int target, String targetType, String targetMethod,
                                              Class scalarType) {
            Class boxType = null;
            boxType = getBoxType( scalarType );
            String scalarDescriptor = Type.getDescriptor(scalarType);
            String internalBoxName = Type.getInternalName(boxType);
            mv.visitLabel( switchItem );
            mv.visitTypeInsn( NEW,
                              internalBoxName );
            mv.visitInsn( DUP );
            mv.visitVarInsn( ALOAD,
                             target );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                targetType,
                                targetMethod,
                                "()" + scalarDescriptor );
            mv.visitMethodInsn( INVOKESPECIAL,
                                internalBoxName,
                                "<init>",
                                "(" + scalarDescriptor + ")V" );
            mv.visitInsn( ARETURN );
        }

        /** Work out the appropriate box type for a scalar/primitive class */
        private static Class getBoxType(Class scalarType) {
            
            if (scalarType == int.class) {
                return Integer.class;
            } else if (scalarType == boolean.class) {
                return Boolean.class;
            } else if (scalarType == char.class) {
                return Character.class;
            } else if (scalarType == byte.class) {
                return Byte.class;
            } else if (scalarType == short.class) {
                return Short.class;
            } else if (scalarType == long.class) {
                return Long.class;
            } else if (scalarType == float.class) {
                return Float.class;
            } else if (scalarType == double.class) {
                return Double.class;
            } else if (scalarType == void.class) {
                return Void.class;
            } else {
                throw new IllegalArgumentException("Unknown scalar type: " + scalarType.getName());
            }
            
        }

        /** A regular switch item, which doesn't require boxing */
        private static void doSwitchItemObject(MethodVisitor mv, Label label,
                                         int target, String targetType, String targetMethod, Class returnClass) {
            
            String returnType = "()" + Type.getDescriptor(returnClass);
            mv.visitLabel( label );            
            mv.visitVarInsn( ALOAD,
                             target );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                targetType,
                                targetMethod,
                                returnType );
            mv.visitInsn( ARETURN );
        }

        private static void doConstructor(ClassWriter cw) {
            MethodVisitor mv;
            mv = cw.visitMethod( ACC_PUBLIC,
                                 "<init>",
                                 "()V",
                                 null,
                                 null );
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitLineNumber( 5,
                                l0 );
            mv.visitVarInsn( ALOAD,
                             0 );
            mv.visitMethodInsn( INVOKESPECIAL,
                                "java/lang/Object",
                                "<init>",
                                "()V" );
            mv.visitInsn( RETURN );
            Label l1 = new Label();
            mv.visitLabel( l1 );
//            mv.visitLocalVariable( "this",
//                                   "Lcom/something/MyObjectFieldAccessor;",
//                                   null,
//                                   l0,
//                                   l1,
//                                   0 );
            mv.visitMaxs( 1,
                          1 );
            mv.visitEnd();
        }
    }
    
    /**
     * Simple classloader for the ASM generated accessors.
     * @author Michael Neale
     */
    static class ByteArrayClassLoader extends ClassLoader {

        public ByteArrayClassLoader(ClassLoader parent) {
          super(parent);
        }
        public void addByteArray(String name, byte[] bytes)
        {
            defineClass(name, bytes, 0, bytes.length);
        }
        
    }

}
