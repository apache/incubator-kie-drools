/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.marshalling.util;

import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.common.AbstractRuleBase;
import org.drools.common.AbstractWorkingMemory;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.ObjectHashSet;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.time.impl.JDKTimerService;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompareViaReflectionUtil {

    private static Logger logger = LoggerFactory.getLogger(CompareViaReflectionUtil.class);

    public static IdentityHashMap<Object, Object> seenObjects = null;
    private static Class<?> OBJECT_ARRAY_CLASS = (new Object[0]).getClass();

    private static int TO_ARRAY = 0;
    private static int ENTRY_SET = 1;
    private static HashSet<Package> javaPackages = new HashSet<Package>();
    static { 
        // primitives ("package null")
        javaPackages.add(long.class.getPackage());
        // java.math
        javaPackages.add(BigDecimal.class.getPackage());
        // java.util
        javaPackages.add(AbstractCollection.class.getPackage());
        // java.util.concurrent
        javaPackages.add(BlockingQueue.class.getPackage());
        // java.util.concurrent.atomic
        javaPackages.add(AtomicInteger.class.getPackage());
        // java.util.concurrent.atomic
//        javaPackages.add(ReentrantLock.class.getPackage());
        javaPackages.add(Long.class.getPackage());
    }

    private static HashMap<Class<?>, Integer> arrClassMap = new HashMap<Class<?>, Integer>();
    {
        arrClassMap.put((new byte[0]).getClass(), BYTE);
        arrClassMap.put((new short[0]).getClass(), SHORT);
        arrClassMap.put((new int[0]).getClass(), INT);
        arrClassMap.put((new long[0]).getClass(), LONG);
        arrClassMap.put((new float[0]).getClass(), FLOAT);
        arrClassMap.put((new double[0]).getClass(), DOUBLE);
        arrClassMap.put((new boolean[0]).getClass(), BOOLEAN);
        arrClassMap.put((new char[0]).getClass(), CHAR);
        arrClassMap.put((new Object[0]).getClass(), OBJECT);
    }


    private static HashSet<Field> doNotCompareFieldsMap = new HashSet<Field>();
    static {
        try {
            doNotCompareFieldsMap.add(AbstractWorkingMemory.class.getDeclaredField("id"));
            doNotCompareFieldsMap.add(KnowledgeBaseImpl.class.getDeclaredField("mappedKnowledgeBaseListeners"));
            doNotCompareFieldsMap.add(AbstractRuleBase.class.getDeclaredField("id"));
            doNotCompareFieldsMap.add(AbstractRuleBase.class.getDeclaredField("workingMemoryCounter"));
            doNotCompareFieldsMap.add(WorkItemImpl.class.getDeclaredField("id"));
            doNotCompareFieldsMap.add(WorkItemImpl.class.getDeclaredField("processInstanceId"));
            doNotCompareFieldsMap.add(ClassFieldAccessorCache.class.getDeclaredField("classLoader"));
            doNotCompareFieldsMap.add(AbstractWorkingMemory.class.getDeclaredField("globalResolver"));
            doNotCompareFieldsMap.add(JDKTimerService.class.getDeclaredField("scheduler"));
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            // do nothing
        }
    }

    private final static int BYTE    = 0;
    private final static int SHORT   = 1;
    private final static int INT     = 2;
    private final static int LONG    = 3;
    private final static int FLOAT   = 4;
    private final static int DOUBLE  = 5;
    private final static int BOOLEAN = 6;
    private final static int CHAR    = 7;
    private final static int OBJECT  = 8;
    private final static int NULL    = 9;

    @SuppressWarnings("rawtypes")
    private static HashSet<Class> atomicPrimitiveClasses = new HashSet<Class>();
    static { 
        atomicPrimitiveClasses.add(AtomicBoolean.class);
        atomicPrimitiveClasses.add(AtomicInteger.class);
        atomicPrimitiveClasses.add(AtomicLong.class);
        atomicPrimitiveClasses.add(AtomicReference.class);
    }

    @SuppressWarnings("rawtypes")
    private static HashSet<Class> atomicArrayClasses = new HashSet<Class>();
    static { 
        atomicArrayClasses.add(AtomicIntegerArray.class);
        atomicArrayClasses.add(AtomicLongArray.class);
        atomicArrayClasses.add(AtomicReferenceArray.class);
    }

    /**
     * This method compares two objects recursively. 
     * @see #compareInstances(DebugContext, Object, Object)
     * @param objA The object to be compared to objB.
     * @param objB The object to be compared to objA. 
     * @return Whether or not the two objects are equal.
     */
    public static boolean compareInstances(Object objA, Object objB  ) { 
        seenObjects = new IdentityHashMap<Object, Object>();
        return compareInstances(null, objA, objB); 
    }
    
    /**
     * A Java object basically consists (or can consist) of 3 things: <ol>
     * <li>Methods, if present</li>
     * <li>Fields (attributes) which contain other Java objects</li>
     * <li>Fields which contain primitives or primitive based objects (int, Integer, String, etc.)</li>
     * <li>Fields which contain arrays, Java collection objects (byte [], Set, HashMap, LinkedBlockingQueue, etc..)</li>
     * </ol>
     * This means, that when we compare two instantiations of the same class, we can do it as follows:<ul>
     * <li>Methods:
     *     <ul><li>We don't have to compare methods, since those don't have any "state".</li></ul>
     *  </li><li>Objects:
     *     <ul><li>Comparing fields containing other Java objects (that are don't belong to a java.* package) is simply a recursive operation.</li></ul>
     * </li><li>Primitive based:
     *     <ul><li>Comparing primitive or primitive based objects can be done using <pre>objA.equals(objB)</pre></li></ul>
     * </li><li>Arrays:
     *     <ul><li>We iterate through the array and compare elements to each other recursively.</li></ul>
     * </li><li>Collections:
     *      <ul>
     *      <li>Fields containing collection objects end up having one of two methods:<ul>
     *          <li>toArray(), which returns an Object&lt;?&gt; []</li>
     *          <li>entrySet(), which returns a Set&lt;Map.Entry&lt;K, V&gt;&gt; object</li>
     *          </ul></li>
     *      </ul>
     * </ul>
     * And that is exactly what we do in this method! We recursively step through the object tree which 
     * has, as its root node, the class defined by the initial objects given to this method.
     * </p>
     * Lastly, you might see the following output if the TRACE level is set for logging out of this object:
     * <pre>
     *  0   : objA and objB are both null
     *  X   : objA and objB are unequal
     *  =   : objA and objB are equal
     *  ==  : objA and objB are the <i>same</i> instance (of the same object)
     *  (=) : objA and objB are class objects and thus both equal and the same
     *  %   : objA and objB are not being compared
     *  !   : objA has already been compared and will not be compared again 
     *         in order to avoid cycles in the object tree</pre>
     * <b>Note</b>: This method has a few weaknesses:<ul>
     * <li>If the object tree contains objects that <i>extend</i> collection objects (HashMap, List, etc.), this method might not compare them 
     * correctly or efficiently. The logic to do this fairly trivial but just hasn't been added yet.</li>
     * <li>If the object tree contains objects that are "home made" collection objects, this method might
     * also not compare these correctly or efficiently. The logic to do this is non-trivial and to some degree 
     * dependent on how the objects are implemented and what type of access is given to the data in the object.</li>
     * </ul>
     * One last thing to remember here is the following issue:<ul>
     * <li>Due to how some objects are created, it's possible that the object tree of objA and object tree of objB
     * contain the <i>same</i> object instantiation. This will mostly be the result of a static object being used
     * during the the initialization of the objects. But this is nonetheless wierd, because I don't expect this.  
     * </li><li>When this happens, we say that objA and objB are the same</li>
     * </ul>
     * </p>
     * @param context This contains logging information (recursive level, position in object tree of objA/objB)
     * @param objA The first instantiation to be compared.
     * @param objB The first instantiation to be compared.
     * @return Whether or not objA and objB are equal, given the criteria above. 
     */
    private static boolean compareInstances(DebugContext context, Object objA, Object objB  ) { 
        if( context == null ) { 
            context = new DebugContext(0, "", true);
        }

        boolean same = false; 

        if( objA == null && objB == null ) { 
            context.name += "0";
            same = true;
        }
        else if( objA == null || objB == null ) { 
            context.name += "X";
            same = false;
        }
        else if( objA == objB ) {
            context.name += objA.getClass().getSimpleName() + " ";
            String stateSymbol = "==";
            if( objA instanceof Class<?> | objA instanceof Class) {
                stateSymbol = "(=)";
            }
            context.name += stateSymbol;
            same = true;
        }
        else if( objA.getClass().getName().equals(objB.getClass().getName()) ) { 
            Class<?> objClass = objA.getClass();
            context.name += "|" + context.level + "| " + objClass.getSimpleName();

            boolean primitiveBasedObjectOrCollection = false;
            if( javaPackages.contains(objClass.getPackage()) ) { 
                primitiveBasedObjectOrCollection = true;
                same = comparePrimitiveBasedOrCollectionInstances(context, objA, objB);
            }
            else if( objA instanceof AbstractHashTable ) { 
                same = compareDroolsSets(context, objA, objB);
            }
            else if( objA.getClass().isArray() ) { 
                same = compareArrays(context, objA, objB);
            }
            else {
                // Check if it's an enum
                Class<?> superClass = objClass.getSuperclass();
                while( superClass != null ) { 
                    if( superClass.equals(Enum.class) ) { 
                        same = objA.equals(objB);
                        primitiveBasedObjectOrCollection = true;
                        break; 
                    }
                    superClass = superClass.getSuperclass();
                }
            }

            if( ! primitiveBasedObjectOrCollection ) { 
                if( seenObjects.put(objA, objA) == null ) { 
                    same = compareInstancesOfSameClass(context, objA, objB);
                    if( !same ) { 
                        seenObjects.remove(objA);
                    }
                }
                else { 
                    context.name += ": !";
                    same = true;
                }
            }
        }
        else { 
            context.name += ": X";
        }

        if( context.print ) { 
            logger.trace( context.name );
        }
        return same;
    }

    public static boolean compareArrays(DebugContext context, Object objA, Object objB) { 
        
        // Determine array type class
        Object classTypeValue = arrClassMap.get(objA.getClass());
        int classType = -1;
        if( classTypeValue != null) { 
            classType = (Integer) classTypeValue;
        }
        else if( objA.getClass().isArray() ) { 
            classType = OBJECT;
        }
        else if( classTypeValue == null ) { 
           classType = NULL; 
        }

        // Check Sameness
        boolean same = false;
        switch(classType) { 
        case BYTE:
            same = Arrays.equals((byte []) objB,(byte [])objA);
            break;
        case SHORT:
            same = Arrays.equals((short []) objB,(short [])objA);
            break;
        case INT:
            same = Arrays.equals((int []) objB,(int [])objA);
            break;
        case LONG:
            same = Arrays.equals((long []) objB,(long [])objA);
            break;
        case FLOAT:
            same = Arrays.equals((float []) objB,(float [])objA);
            break;
        case DOUBLE:
            same = Arrays.equals((double []) objB,(double [])objA);
            break;
        case BOOLEAN:
            same = Arrays.equals((boolean []) objB,(boolean [])objA);
            break;
        case CHAR:
            same = Arrays.equals((char []) objB,(char [])objA);
            break;
        case OBJECT:
            int lengthA = Array.getLength(objA);
            int lengthB = Array.getLength(objB);
            if( lengthA != lengthB ) { 
                same = false;
            }
            else if( lengthA == 0 ) { 
                same = true;
            }
            else { 
                same = true;
                for( int i = 0; same && i < lengthA; ++i ) { 
                    DebugContext subContext = context.clone();
                    subContext.level = context.level + 1;
                    subContext.name = context.name + ": (" + i + ") ";
                    same = compareInstances(subContext, Array.get(objA, i), Array.get(objB, i));
                }
            }
            break;
        case NULL:
            same = objA == objB;
            break;
        default: 
            fail( "Unable to determine class of array [" +  classType + "]");
        }

        return same;
    }

    /**
     * This method should be called when we've been able to ascertain (all) of the following:<ul>
     * <li>Neither of the objects are null</li>
     * <li>Both of the objects belong to the same class</li>
     * </ul>
     * In this method, we go through <i>all</i> fields of the object and compare them (recursively).
     * <br/>
     * @param context This contains logging information (recursive level, position in object tree of objA/objB)
     * @param objA The first instantiation to be compared.
     * @param objB The first instantiation to be compared.
     * @return Whether or not objA and objB are equal.
     */
    private static boolean compareInstancesOfSameClass(DebugContext context, Object objA, Object objB) { 
        boolean same = false;
        try { 
            Class<?> objClass = objA.getClass();
            do {
                Field [] fields = objClass.getDeclaredFields();
                if( fields.length == 0 ) { 
                    same = true;
                }
                else { 
                    same = true;
                    for( int i = 0; same && i < fields.length; ++i ) { 
                        DebugContext subContext = context.clone();
                        subContext.level = context.level + 1;
                        subContext.name = context.name + ": " + fields[i].getName() + " > ";

                        if( Modifier.isTransient(fields[i].getModifiers()) ||
                            doNotCompareFieldsMap.contains(fields[i]) ) {   
                            if( context.print ) { 
                                logger.trace( context.name + ": " + fields[i].getName() + " %" );
                            }
                            continue; 
                        }

                        fields[i].setAccessible(true);
                        Object subObjA = fields[i].get(objA);
                        Object subObjB = fields[i].get(objB);
                        same = compareInstances(subContext, subObjA, subObjB);
                    }
                }
                objClass = objClass.getSuperclass();
            } while( objClass != null && same);
            context.name += ": " + (same == true ? "=" : "X");
        }
        catch( Exception e ) { 
            same = false;
            e.printStackTrace();
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }
        return same;
    }


    private static boolean compareDroolsSets(DebugContext context, Object objA, Object objB) { 
        boolean same = true;

        int length = 0;
        try { 
            Method sizeMethod = AbstractHashTable.class.getDeclaredMethod("size", (Class []) null);
            Integer sizeA = (Integer) sizeMethod.invoke(objA, (Object []) null);
            Integer sizeB = (Integer) sizeMethod.invoke(objB, (Object []) null);
        
            if( ! sizeA.equals(sizeB) ) { 
                return false;
            }
            length = sizeA.intValue();
        }
        catch( Exception e ) { 
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }
       
        if( length == 0 ) { 
            return true;
        }
       
        Method toArrayMethod = null;
        try { 
            toArrayMethod = AbstractHashTable.class.getDeclaredMethod("toArray", (Class []) null);
        }
        catch( Exception e ) { 
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }
        
        if( toArrayMethod == null ) { 
            fail("Could not retrieve toArray() method for " + objA.getClass().getName());
        }
        
        Object [] arrayA = null;
        Object [] arrayB = null;
        
        try {
            arrayA = (Object []) toArrayMethod.invoke(objA, (Object []) null);
            arrayB = (Object []) toArrayMethod.invoke(objB, (Object []) null);
            
        } catch (Exception e) {
            same = false;
            fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }

        if( arrayA == null && arrayB == null ) { 
            return true;
        }
        else if( arrayA == null || arrayB == null ) { 
            return false; 
        }

        for( int a = 0; a < length; ++a ) { 
            boolean elementIsSame = false;
            for( int b = 0; b < length; ++b ) { 
                Object subObjA = ((ObjectHashSet.ObjectEntry) arrayA[a]).getValue();
                Object subObjB = ((ObjectHashSet.ObjectEntry) arrayA[b]).getValue();
                
                DebugContext entryContext = context.clone();
                String name = context.name + ": ";
                entryContext.level = context.level + 1;
                entryContext.name = name + "<entry> ";
                entryContext.print = false;
                if( compareInstances(entryContext, subObjA, subObjB) ) { 
                    logger.trace(entryContext.name);
                    elementIsSame = true;
                    break;
                }
            }

            if( ! elementIsSame ) { 
                // a matching element was not found in arrayB
                same = false;
                break;
            } 
        }

        return same;
    }

    /**
     * Compare two objects which are all of the following: <ul>
     * <li>A primitive or primitive based object</li>
     * <li>A collection object</li>
     * </ul>
     * @param context This contains logging information (recursive level, position in object tree of objA/objB)
     * @param objA The first instantiation to be compared.
     * @param objB The first instantiation to be compared.
     * @return Whether or not objA and objB are equal.
     */
    private static boolean comparePrimitiveBasedOrCollectionInstances(DebugContext context, Object objA, Object objB) { 
        boolean same = false;

        Class<?> objClass = objA.getClass();
        Method [] methods = getMethodToRetrieveCollection(objClass);
        try { 
            if( methods[TO_ARRAY] != null ) { 
                same = compareArrayBasedObjects(context, methods[TO_ARRAY], objA, objB);
            }
            else if( methods[ENTRY_SET] != null) {
                same = compareEntrySetBasedObjects(context, methods[ENTRY_SET], objA, objB);
            }
            else if( objClass.isArray() ) { 
                same = compareArrays(context, objA, objB);
            }
            else if( atomicPrimitiveClasses.contains(objClass) ) { 
                same = compareAtomicPrimitives(objA, objB);
            }
            else if( atomicArrayClasses.contains(objClass) ) { 
                same = compareAtomicArrays(context, objA, objB);
            }
            else {
                same = objA.equals(objB);
            }
            context.name += ": " + (same == true ? "=" : "X");
        }
        catch( Exception e ) { 
            e.printStackTrace();
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }

        return same;
    }

    protected static boolean compareAtomicPrimitives(Object objA, Object objB) { 
        boolean same = false;
        try {
            Method getMethod = objA.getClass().getMethod("get", new Class[0]);
            Object valA = getMethod.invoke(objA, (Object []) null);
            Object valB = getMethod.invoke(objB, (Object []) null);
            if( valA.equals(valB) ) { 
                same = true;
            }
        }
        catch( Exception e ) { 
            e.printStackTrace();
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }
        return same;
    }
   
    protected static boolean compareAtomicArrays(DebugContext context, Object objA, Object objB) { 
        boolean same = false;
        int length = 0;
        try {
            Method lengthMethod = objA.getClass().getMethod("length", new Class[0]);
            Object valA = lengthMethod.invoke(objA, (Object []) null);
            Object valB = lengthMethod.invoke(objB, (Object []) null);
            if( valA.equals(valB) ) { 
                same = true;
                length = (Integer) valA;
            }
            else { 
                return false;
            }
        }
        catch( Exception e ) { 
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }

        try {
            Method getMethod = objA.getClass().getMethod("get", new Class[] { int.class } );
            for( int i = 0; i < length && same; ++i ) { 
                Object subObjA = getMethod.invoke(objA, i);
                Object subObjB = getMethod.invoke(objB, i);

                if( subObjA == null && subObjB == null ) { 
                    continue;
                }

                DebugContext subContext = context.clone();
                subContext.level = context.level + 1;
                subContext.name = context.name + ": (" + i + ") ";
                same = compareInstances(subContext, subObjA, subObjB);
            }
        } catch (Exception e) {
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ": " + e.getMessage() );
        }

        return same;
    }
    
    /**
     * Collection based objects (Array based or Set based), should basically always implement
     * one of two methods:<ul>
     * <li>.toArray()</li>
     * <li>.entrySet()</li>
     * </ul>
     * <p/>
     * This method retrieves the appropriate method for the given object (so that we can later retrieve
     * the list/array/set that this Object is based on).
     * @param objA The object that we want to retrieve this method for.
     * @return The requested Method.
     */
    private static Method [] getMethodToRetrieveCollection(Class<?> objClass) { 
        Method [] methods = new Method[2];

        do { 
            Method [] objMethods = objClass.getDeclaredMethods();
            for( int m = 0; m < objMethods.length; ++m ) {
                if(objMethods[m].getName().equals("toArray") 
                        && objMethods[m].getParameterTypes().length == 0 
                        && objMethods[m].getReturnType().equals(OBJECT_ARRAY_CLASS) ) {
                    methods[TO_ARRAY] = objMethods[m];
                    methods[TO_ARRAY].setAccessible(true);
                    break;
                }
                else if(objMethods[m].getName().equals("entrySet") 
                        && objMethods[m].getParameterTypes().length == 0 
                        && objMethods[m].getReturnType().equals(Set.class) ) {
                    methods[ENTRY_SET] = objMethods[m];
                    methods[ENTRY_SET].setAccessible(true);
                    break;
                }
            }
            objClass = objClass.getSuperclass();
        } while( objClass != null && methods[TO_ARRAY] == null && methods[ENTRY_SET] == null );
        
        return methods;
    }

    private static boolean compareArrayBasedObjects(DebugContext context, Method toArrayMethod, Object objA, Object objB) throws Exception { 
        boolean same = true; 

        Object [] arrayA = (Object []) toArrayMethod.invoke(objA, (Object []) null);
        Object [] arrayB = (Object []) toArrayMethod.invoke(objB, (Object []) null);

        // Simple checks
        if( arrayA == null && arrayB == null ) { 
            return true;
        }
        else if( arrayA == null || arrayB == null ) { 
            return false;
        }
        else { 
            if( arrayA.length != arrayB.length ) { 
                return false;
            }
            if( arrayA.length == 0 ) { 
                return true;
            }

            // Check whether order matters
            Class<?> superClass = objA.getClass().getSuperclass();
            boolean isSet = false;
            while( superClass != null ) { 
                if( superClass.equals(AbstractSet.class) ) { 
                    isSet = true; 
                }
                superClass = superClass.getSuperclass();
            }

            // Check content
            for( int a = 0; same && a < arrayA.length; ++a ) { 
                Object subObjA = arrayA[a];

                if( ! isSet ) { 
                    // order matters, compare element a from both arrays
                    Object subObjB = arrayB[a];
                    
                    DebugContext subContext = context.clone();
                    subContext.level = context.level + 1;
                    subContext.name = context.name + ": " + "<elem> ";
                    same = compareInstances(subContext, subObjA, subObjB);
                }
                else { 
                    // order doesn't matter, check if a matching element exists in arrayB
                    boolean elementIsSame = false;
                    for( int b = 0; ! elementIsSame && b < arrayB.length; ++b ) { 
                        Object subObjB = arrayB[b];
                        
                        DebugContext subContext = context.clone();
                        subContext.level = context.level + 1;
                        subContext.name = context.name + ": " + "<elem> ";
                        elementIsSame = compareInstances(subContext, subObjA, subObjB);
                    }
                    if( elementIsSame == false ) { 
                       same = false; 
                    }
                } 
            }
                
        }

        return same;
    }

    private static boolean compareEntrySetBasedObjects(DebugContext context, Method entrySetMethod, Object objA, Object objB) throws Exception { 
        boolean same = true; 

        @SuppressWarnings("unchecked")
        Set<Map.Entry<?,?>> entrySetA = (Set<Map.Entry<?,?>>) entrySetMethod.invoke(objA, (Object []) null);
        @SuppressWarnings("unchecked")
        Set<Map.Entry<?,?>> entrySetB = (Set<Map.Entry<?,?>>) entrySetMethod.invoke(objB, (Object []) null);

        // Simple checks
        if( entrySetA == null && entrySetB == null ) { 
            return true;
        }
        else if( entrySetA == null || entrySetB == null ) { 
            return false;
        }
        
        if( entrySetA.size() != entrySetB.size() ) { 
            return false;
        }
        if( entrySetA.size() == 0 ) { 
            return true;
        }

        // Check content
        for( Entry<?, ?> entryA : entrySetA ) { 
            boolean elementIsSame = false;

            Object keyA = entryA.getKey();
            for( Entry<?, ?> entryB : entrySetB ) { 
                Object keyB = entryB.getKey();
                DebugContext entryContext = context.clone();

                String name = context.name + ": ";
                entryContext.level = context.level + 1;
                entryContext.name = name + "<key> ";
                entryContext.print = false;
                if( compareInstances(entryContext, keyA, keyB) ) { 
                    logger.trace( entryContext.name );
                    entryContext.name = name + "<entry> ";
                    entryContext.print = true;
                    elementIsSame = compareInstances(entryContext, entryA.getValue(), entryB.getValue());
                    break;
                }
            }

            if( ! elementIsSame ) { 
                // a matching element was not found in arrayB
                same = false;
                break;
            }
        }

        return same;
    } 

    private static class DebugContext { 
       public int level;
       public String name;
       public boolean print;
      
       public DebugContext(int level, String name, boolean print) {
           this.level = level;
           this.name = name;
           this.print = print;
       }
       
       public DebugContext clone() { 
          DebugContext newDebugContext = new DebugContext(this.level, this.name, this.print);
          return newDebugContext;
       }
    }

}
