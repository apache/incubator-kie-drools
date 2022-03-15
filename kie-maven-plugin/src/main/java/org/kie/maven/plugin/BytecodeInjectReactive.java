/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.maven.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.phreak.ReactiveCollection;
import org.drools.core.phreak.ReactiveList;
import org.drools.core.phreak.ReactiveObject;
import org.drools.core.phreak.ReactiveObjectUtil;
import org.drools.core.phreak.ReactiveSet;
import org.drools.core.spi.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.CtField.Initializer;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.MethodSignature;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.bytecode.stackmap.MapMaker;

/*
 * kudos to hibernate-enhance-maven-plugin.
 */
public class BytecodeInjectReactive {
    public static final String DROOLS_PREFIX = "$$_drools_";
    public static final String FIELD_WRITER_PREFIX = DROOLS_PREFIX + "write_";
    public static final String DROOLS_LIST_OF_TUPLES = DROOLS_PREFIX + "lts";

    public static final Logger LOG = LoggerFactory.getLogger(BytecodeInjectReactive.class);

    private Map<String, CtMethod> writeMethods;
    private ClassPool cp;
    
    public BytecodeInjectReactive(ClassPool cp) {
        this.cp = cp;
        init();
    }

    public static BytecodeInjectReactive newInstance(ClassPool cp) {
        return new BytecodeInjectReactive(cp);
    }
    
    /**
     * Utility method for returning the (inferred) classpath of classloading from the given Class.
     * @param clazz the enclosing Class.
     * @return the (inferred) classpath of clazz
     */
    public static String classpathFromClass(Class<?> clazz) {
        String aname = clazz.getPackage().getName().replaceAll("\\.", "/") + "/" +  clazz.getSimpleName()+".class";
        String apath = ClassLoader.getSystemClassLoader().getResource( aname).getPath();
        String path = null;
        if (apath.contains("!")) {
            path = apath.substring(0, apath.indexOf("!")).replace("file:", "");
        } else {
            path = apath.substring(0, apath.indexOf(aname));
        }
        return path;
    }
    
    private void init() {
        this.writeMethods = new HashMap<String, CtMethod>();
    }
    
    public byte[] injectReactive(String classname) throws Exception {
        init();
        
        CtClass droolsPojo = cp.get(classname);
        if (collectReactiveFields(droolsPojo).size() == 0) {
            LOG.info("Skipped bytecode injection in class " + droolsPojo.getName()+ " because no fields candidated for reactivity.");
            return droolsPojo.toBytecode();
        }
        
        droolsPojo.addInterface( cp.get(ReactiveObject.class.getName()) );
        
        CtField ltsCtField = new CtField( cp.get(Collection.class.getName()), DROOLS_LIST_OF_TUPLES, droolsPojo );
        ltsCtField.setModifiers(Modifier.PRIVATE);
        ClassType listOfTuple = new SignatureAttribute.ClassType(Collection.class.getName(),
                new TypeArgument[]{new TypeArgument( new SignatureAttribute.ClassType(Tuple.class.getName()) )});
        ltsCtField.setGenericSignature(
                listOfTuple.encode()
                );
        // Do not use the Initializer.byNew... as those method always pass at least 1 parameter which is "this".
        droolsPojo.addField(ltsCtField, Initializer.byExpr("new java.util.HashSet();"));
        
        final CtMethod getLeftTuplesCtMethod = CtNewMethod.make(
                "public java.util.Collection getLeftTuples() {\n" + 
                "    return this.$$_drools_lts != null ? this.$$_drools_lts : java.util.Collections.emptyList();\n"+
                "}", droolsPojo );
        MethodSignature getLeftTuplesSignature = new MethodSignature(null, null, listOfTuple, null);
        getLeftTuplesCtMethod.setGenericSignature(getLeftTuplesSignature.encode());
        droolsPojo.addMethod(getLeftTuplesCtMethod);
        
        final CtMethod addLeftTupleCtMethod = CtNewMethod.make(
                "public void addLeftTuple("+Tuple.class.getName()+" leftTuple) {\n" + 
                "    if ($$_drools_lts == null) {\n" + 
                "        $$_drools_lts = new java.util.HashSet();\n" + 
                "    }\n" + 
                "    $$_drools_lts.add(leftTuple);\n" + 
                "}", droolsPojo );
        droolsPojo.addMethod(addLeftTupleCtMethod);
        
        final CtMethod removeLeftTupleCtMethod = CtNewMethod.make(
                "public void removeLeftTuple("+Tuple.class.getName()+" leftTuple) {\n" + 
                "    $$_drools_lts.remove(leftTuple);\n" + 
                "}", droolsPojo );
        droolsPojo.addMethod(removeLeftTupleCtMethod);
        
        Map<String, CtField> fieldsMap = collectReactiveFields(droolsPojo);
        for (CtField f : fieldsMap.values()) {
            LOG.debug("Preparing field writer method for field: {}.", f);
            writeMethods.put(f.getName(), makeWriter(droolsPojo, f));
        }
        
        enhanceAttributesAccess(fieldsMap, droolsPojo);
        
        // first call CtClass.toClass() before the original class is loaded, it will persist the bytecode instrumentation changes in the classloader.
        return droolsPojo.toBytecode();
    }
    
    protected void enhanceAttributesAccess(Map<String, CtField> fieldsMap, CtClass managedCtClass) throws Exception {
        final ConstPool constPool = managedCtClass.getClassFile().getConstPool();
        final ClassPool classPool = managedCtClass.getClassPool();

        for ( Object oMethod : managedCtClass.getClassFile().getMethods() ) {
            final MethodInfo methodInfo = (MethodInfo) oMethod;
            final String methodName = methodInfo.getName();

            // skip methods added by enhancement, and abstract methods (methods without any code)
            if ( methodName.startsWith( DROOLS_PREFIX ) || methodInfo.getCodeAttribute() == null ) {
                continue;
            }

            try {
                final CodeIterator itr = methodInfo.getCodeAttribute().iterator();
                while ( itr.hasNext() ) {
                    final int index = itr.next();
                    final int op = itr.byteAt( index );
                    if ( op != Opcode.PUTFIELD && op != Opcode.GETFIELD ) {
                        continue;
                    }

                    final String fieldName = constPool.getFieldrefName( itr.u16bitAt( index + 1 ) );
                    CtField ctField = fieldsMap.get(fieldName);
                    if (ctField == null ) {
                        continue;
                    }
                    
                    // if we are in constructors, only need to intercept assignment statement for Reactive Collection/List/... (regardless they may be final)
                    if ( methodInfo.isConstructor() && !( isCtFieldACollection(ctField) ) ) {
                        continue;
                    }

                    if (op == Opcode.PUTFIELD) {
                        // addMethod is a safe add, if constant already present it return the existing value without adding.
                        final int methodIndex = addMethod( constPool, writeMethods.get(fieldName) );
                        itr.writeByte( Opcode.INVOKEVIRTUAL, index );
                        itr.write16bit( methodIndex, index + 1 );
                    }
                }
                methodInfo.getCodeAttribute().setAttribute( MapMaker.make( classPool, methodInfo ) );
            }
            catch (BadBytecode bb) {
                final String msg = String.format(
                        "Unable to perform field access transformation in method [%s]",
                        methodName
                );
                throw new Exception( msg, bb );
            }
        }
    }
    
    private static CtMethod write(CtClass target, String format, Object ... args) throws CannotCompileException {
        final String body = String.format( format, args );
        LOG.debug( "writing method into [{}]:\n{}\n", target.getName(), body );
        final CtMethod method = CtNewMethod.make( body, target );
        target.addMethod( method );
        return method;
    }
    
    /**
     * Add Method to ConstPool. If method was not in the ConstPool will add and return index, otherwise will return index of already existing entry of constpool
     */
    private static int addMethod(ConstPool cPool, CtMethod method) {
        // addMethodrefInfo is a safe add, if constant already present it return the existing value without adding.
        return cPool.addMethodrefInfo( cPool.getThisClassInfo(), method.getName(), method.getSignature() );
    }
    
    private CtMethod makeWriter(CtClass managedCtClass, CtField field) throws Exception {
        final String fieldName = field.getName();
        final String writerName = FIELD_WRITER_PREFIX + fieldName;
        
        return write(
                managedCtClass,
                "public void %s(%s %s) {%n%s%n}",
                writerName,
                field.getType().getName(),
                fieldName,
                buildWriteInterceptionBodyFragment( field )
        );
        
    }

    private String buildWriteInterceptionBodyFragment(CtField field) throws NotFoundException {
        // remember: In the source text given to setBody(), the identifiers starting with $ have special meaning
        // $0, $1, $2, ...     this and actual parameters 
        
        LOG.debug("buildWriteInterceptionBodyFragment: {} {}", field.getType().getClass(), field.getType());
        
        if ( isCtFieldACollection(field) ) {
            if ( field.getType().equals(cp.get(Set.class.getName())) ) {
                // it implements Set, so wrap accordingly with ReactiveSet:
                return String.format(
                        "  this.%1$s = new "+ReactiveSet.class.getName()+"($1); ",
                        field.getName()
                        );
            }
            
            if ( field.getType().equals(cp.get(List.class.getName())) ) {
                // it implements List, so wrap accordingly with ReactiveList:
                return String.format(
                        "  this.%1$s = new "+ReactiveList.class.getName()+"($1); ",
                        field.getName()
                        );
            }
            
            return String.format(
                    "  this.%1$s = new "+ReactiveCollection.class.getName()+"($1); ",
                    field.getName()
                    );
        }
        
        // 2nd line will result in: ReactiveObjectUtil.notifyModification((ReactiveObject) this);
        // and that is fine because ASM: INVOKESTATIC org/drools/core/phreak/ReactiveObjectUtil.notifyModification (Lorg/drools/core/phreak/ReactiveObject;)V
        return String.format(
                "  this.%1$s = $1;%n" +
                "  "+ReactiveObjectUtil.class.getName()+".notifyModification($0); ",
                field.getName()
                );
    }

    private Map<String, CtField> collectReactiveFields(CtClass managedCtClass) {
        final Map<String, CtField> persistentFieldMap = new HashMap<String, CtField>();
        for ( CtField ctField : managedCtClass.getDeclaredFields() ) {
            // skip static fields, skip final fields, and skip fields added by enhancement
            if ( Modifier.isStatic( ctField.getModifiers() ) || ctField.getName().startsWith( DROOLS_PREFIX ) ) {
                continue;
            }
            // skip outer reference in inner classes
            if ( "this$0".equals( ctField.getName() ) ) {
                continue;
            }
            // optimization: skip final field, unless it is a Reactive Collection/List/... in which case we need to consider anyway:
            if ( Modifier.isFinal( ctField.getModifiers()) ) {
                if ( !isCtFieldACollection(ctField) ) {
                    continue;
                }
            }
            persistentFieldMap.put( ctField.getName(), ctField );
        }
        // CtClass.getFields() does not return private fields, while CtClass.getDeclaredFields() does not return inherit
        for ( CtField ctField : managedCtClass.getFields() ) {
            if ( ctField.getDeclaringClass().equals( managedCtClass ) ) {
                // Already processed above
                continue;
            }
            if ( Modifier.isStatic( ctField.getModifiers() ) || ctField.getName().startsWith( DROOLS_PREFIX ) ) {
                continue;
            }
            persistentFieldMap.put( ctField.getName(), ctField );
        }
        return persistentFieldMap;
    }

    /**
     * Verify that CtField is exactly the java.util.Collection, java.util.List or java.util.Set, otherwise cannot instrument the class' field
     */
    private boolean isCtFieldACollection(CtField ctField) {
        try {
            return ctField.getType().equals(cp.get(Collection.class.getName()))
                    || ctField.getType().equals(cp.get(List.class.getName()))
                    || ctField.getType().equals(cp.get(Set.class.getName())) ;
        } catch (NotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
