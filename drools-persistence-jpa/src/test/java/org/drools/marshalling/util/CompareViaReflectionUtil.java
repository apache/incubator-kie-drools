package org.drools.marshalling.util;

import static java.lang.System.out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.conf.EventProcessingOption;
import org.drools.rule.GroupElement;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.impl.TrackableTimeJobFactoryManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CompareViaReflectionUtil {

    @Test
    @Ignore
    public void scratch() throws Exception { 
        out.println( "Class: " + GroupElement.AND.getClass() );
        out.println( "Dec Class: " + GroupElement.AND.getDeclaringClass() );
        out.println( "Enc Class: " + GroupElement.AND.getClass().getEnclosingClass() );
        out.println( "Package: " + GroupElement.AND.getClass().getPackage() );

        Object obj = GroupElement.AND;
        Class superClass = obj.getClass().getSuperclass();
        while( superClass != null ) { 
            out.println( "Super: " + superClass );
            superClass = superClass.getSuperclass(); 
        }

        out.println( "== :" + (GroupElement.AND == GroupElement.EXISTS) );
        out.println( "equals :" + (GroupElement.AND.equals(GroupElement.EXISTS)) );
        out.println( "== :" + (GroupElement.AND.equals(GroupElement.AND)) );
        out.println( "equals :" + (GroupElement.AND.equals(GroupElement.AND)) );
        out.println( "== :" + (GroupElement.EXISTS.equals(GroupElement.EXISTS)) );
        out.println( "equals :" + (GroupElement.EXISTS.equals(GroupElement.EXISTS)) );
    }

    @Test
    public void testThisClass() throws Exception { 

        StatefulKnowledgeSession ksessionA = null;
        {
            KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            config.setOption( EventProcessingOption.STREAM );
            KnowledgeBase knowledgeBaseA = KnowledgeBaseFactory.newKnowledgeBase( config );
            KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
            ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
            ksessionA = knowledgeBaseA.newStatefulKnowledgeSession(ksconf, null);
        }

        StatefulKnowledgeSession ksessionB = null;
        {
            KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(); 
            config.setOption( EventProcessingOption.STREAM );
            KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
            KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
            ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
            ksessionB = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        }


        Assert.assertTrue(CompareViaReflectionUtil.class.getSimpleName() + " is broken!", 
                compareInstances(null, ksessionA, ksessionB) );
    }

    public static HashSet<Object> seenObjects = new HashSet<Object>();
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
        javaPackages.add(Long.class.getPackage());
        javaPackages.add(BlockingQueue.class.getPackage());
    }

    private static HashSet<Class<?>> droolsCollectionClasses = new HashSet<Class<?>>();
    static {
        // droolsCollectionClasses.add(
    }

    public static boolean compareInstances(DebugContext context, Object objA, Object objB  ) { 
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
                // OCRAM: Al die stomme drools impl's ook hier behandelen.. 
                same = comparePrimitiveBasedOrCollectionInstances(context, objA, objB);
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
                if( seenObjects.add(objA) ) { 
                    same = compareInstancesOfSameClass(context, objA, objB);
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
            out.println( context.name );
        }
        return same;
    }

    private static boolean compareInstancesOfSameClass(DebugContext context, Object objA, Object objB) { 
        boolean same = false;
        try { 
            Field [] fields = objA.getClass().getDeclaredFields();
            if( fields.length == 0 ) { 
                same = true;
            }
            else { 
                for( int i = 0; i < fields.length; ++i ) { 
                    DebugContext subContext = context.clone();
                    subContext.level = context.level + 1;
                    subContext.name = context.name + ": " + fields[i].getName() + " > ";

                    fields[i].setAccessible(true);
                    Object subObjA = fields[i].get(objA);
                    Object subObjB = fields[i].get(objB);
                    same = compareInstances(subContext, subObjA, subObjB);
                }
            }
            context.name += ": " + (same == true ? "=" : "X");
        }
        catch( Exception e ) { 
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ":" + e.getMessage() );
        }
        return same;
    }

    private static boolean comparePrimitiveBasedOrCollectionInstances(DebugContext context, Object objA, Object objB) { 
        boolean same = false;

        Method [] methods = getMethodToRetrieveCollection(objA);
        try { 
            if( methods[TO_ARRAY] != null ) { 
                same = compareArrayBasedObjects(context, methods[TO_ARRAY], objA, objB);
            }
            else if( methods[ENTRY_SET] != null) {
                same = compareEntrySetBasedObjects(context, methods[ENTRY_SET], objA, objB);
            }
            else { 
                same = objA.equals(objB);
            }
            context.name += ": " + (same == true ? "=" : "X");
        }
        catch( Exception e ) { 
            same = false;
            Assert.fail(e.getClass().getSimpleName() + ":" + e.getMessage() );
        }

        return same;
    }

    private static Method [] getMethodToRetrieveCollection(Object objA) { 
        Method [] methods = new Method[2];

        Class<?> objClass = objA.getClass(); 
        Method [] objMethods = objClass.getDeclaredMethods();
        for( int m = 0; m < objMethods.length; ++m ) {
            if(objMethods[m].getName().equals("toArray") 
                    && objMethods[m].getParameterTypes().length == 0 
                    && objMethods[m].getReturnType().equals(OBJECT_ARRAY_CLASS) ) {
                methods[TO_ARRAY] = objMethods[m];
                methods[TO_ARRAY].setAccessible(true);
            }
            else if(objMethods[m].getName().equals("entrySet") 
                    && objMethods[m].getParameterTypes().length == 0 
                    && objMethods[m].getReturnType().equals(Set.class) ) {
                methods[ENTRY_SET] = objMethods[m];
                methods[ENTRY_SET].setAccessible(true);
            }
        }
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
            for( int a = 0; a < arrayA.length; ++a ) { 
                Object subObjA = arrayA[a];
                boolean elementIsSame = false;

                if( ! isSet ) { 
                    // order matters, compare element a from both arrays
                    Object subObjB = arrayB[a];
                    elementIsSame = compareInstances(context, subObjA, subObjB);
                }
                else { 
                    // order doesn't matter, check if a matching element exists in arrayB
                    for( int b = 0; b < arrayB.length; ++b ) { 
                        Object subObjB = arrayB[b];
                        if( elementIsSame = compareInstances(context, subObjA, subObjB) ) { 
                            break;
                        }
                    }
                } 
                if( ! elementIsSame ) { 
                    // a matching element was not found in arrayB
                    same = false;
                    break;
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
        else { 
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
                        out.println( entryContext.name );
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
