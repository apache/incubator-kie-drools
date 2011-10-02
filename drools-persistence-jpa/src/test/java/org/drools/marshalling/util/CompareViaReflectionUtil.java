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
    @Ignore
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
      
        Object [] debugContext = new Object[3];
        debugContext[0] = 0;
        debugContext[2] = true;
        compareInstances(debugContext, ksessionA, ksessionB);
    }

    public static HashSet<Object> seenObjects = new HashSet<Object>();
    
    public static HashSet<Package> javaPackages = new HashSet<Package>();
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
    
    public static HashSet<Class<?>> droolsCollectionClasses = new HashSet<Class<?>>();
    static {
//        droolsCollectionClasses.add(
    }
    
    public static boolean compareInstances(Object [] context, Object objA, Object objB  ) throws Exception { 
        int level = (Integer) context[0];
        String name = (String) context[1];
        
        boolean same = false; 
        
        if( level > 20 ) { 
            out.println(name);
            name += "** 20 **";
            same = true;
        }

        if( objA == null && objB == null ) { 
            name += "0";
            same = true;
        }
        else if( objA == objB ) {
            name += objA.getClass().getSimpleName() + " ";
            String stateSymbol = "==";
            if( objA instanceof Class<?> | objA instanceof Class) {
                stateSymbol = "(=)";
            }
            name += stateSymbol;
            same = true;
        }
        else if( objA != null && objB != null && objA.getClass().getName().equals(objB.getClass().getName()) ) { 
            if( name == null ) { 
                name = new String();
            }
            Class<?> objClass = objA.getClass();
            name += "|" + level + "| " + objClass.getSimpleName();

            boolean primitiveBasedObjectOrCollection = false;
            if( javaPackages.contains(objClass.getPackage()) ) { 
                primitiveBasedObjectOrCollection = true;
                // OCRAM: Al die stomme drools impl's ook hier behandelen.. 
                // OCRAM: controle op toArray ÉN entrySet method in een keer??
                Method toArrayMethod = getToArrayMethod(objA);
                if( toArrayMethod != null ) { 
                    same = compareArrayBasedObjects(context, toArrayMethod, objA, objB);
                }
                else {
                    Method entrySetMethod = getEntrySetMethod(objA);
                    if( entrySetMethod != null ) { 
                        same = compareEntrySetBasedObjects(context, entrySetMethod, objA, objB);
                    }
                    else { 
                        same = objA.equals(objB);
                    }
                }
                name += ": " + (same == true ? "=" : "X");
                
            }
            else {
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
                    try { 
                        Field [] fields = objClass.getDeclaredFields();
                        if( fields.length == 0 ) { 
                            same = true;
                        }
                        else { 
                            for( int i = 0; i < fields.length; ++i ) { 
                                Object [] subContext = context.clone();
                                subContext[0] = level + 1;
                                subContext[1] = name + ": " + fields[i].getName() + " > ";

                                fields[i].setAccessible(true);
                                Object subObjA = fields[i].get(objA);
                                Object subObjB = fields[i].get(objB);
                                same = compareInstances(subContext, subObjA, subObjB);
                            }
                        }
                        name += ": " + (same == true ? "=" : "X");
                    }
                    catch( Exception e ) { 
                        same = false;
                        Assert.fail(e.getClass().getSimpleName() + ":" + e.getMessage() );
                    }

                }
                else { 
                    name += ": !";
                    same = true;
                }
            }
        }
        else { 
            name += ": X";
        }

        if( (Boolean) context[2] ) { 
            out.println( name );
        }
        else { 
            context[1] = name;
        }
        return same;
    }

    private static Class<?> objectArrayClass = (new Object[0]).getClass();
    
    private static Method getToArrayMethod(Object objA) { 
        Method toArrayMethod = null;
        Class<?> objClass = objA.getClass(); 
        Method [] objMethods = objClass.getDeclaredMethods();
        for( int m = 0; m < objMethods.length; ++m ) {
            if(objMethods[m].getName().equals("toArray") 
               && objMethods[m].getParameterTypes().length == 0 
               && objMethods[m].getReturnType().equals(objectArrayClass) ) {
                toArrayMethod = objMethods[m];
                break;
            }
       }
       return toArrayMethod;
    }
   
    public static boolean compareArrayBasedObjects(Object [] context, Method toArrayMethod, Object objA, Object objB) throws Exception { 
       boolean same = true; 
     
       Object [] arrayA = (Object []) toArrayMethod.invoke(objA, (Object []) null);
       Object [] arrayB = (Object []) toArrayMethod.invoke(objB, (Object []) null);
      
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
    
    private static Method getEntrySetMethod(Object objA) { 
        Method entrySetMethod = null;
        Class<?> objClass = objA.getClass(); 
        Method [] objMethods = objClass.getDeclaredMethods();
        for( int m = 0; m < objMethods.length; ++m ) {
            if(objMethods[m].getName().equals("entrySet") 
               && objMethods[m].getParameterTypes().length == 0 
               && objMethods[m].getReturnType().equals(Set.class) ) {
                entrySetMethod = objMethods[m];
                entrySetMethod.setAccessible(true);
                break;
            }
       }
       return entrySetMethod;
    }

    public static boolean compareEntrySetBasedObjects(Object [] context, Method entrySetMethod, Object objA, Object objB) throws Exception { 
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
                    Object [] entryContext = context.clone();

                    String name = (String) entryContext[1] + ": ";
                    entryContext[0] = ((Integer) entryContext[0]) + 1;
                    entryContext[1] = name + "<key> ";
                    entryContext[2] = false;
                    if( compareInstances(entryContext, keyA, keyB) ) { 
                        out.println( entryContext[1] );
                        entryContext[1] = name + "<entry> ";
                        entryContext[2] = true;
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
    
     private static MessageDigest algorithm;
     static { 
         try { 
             algorithm = MessageDigest.getInstance("SHA-1");
         }
         catch( Exception e ) { 
             // do nothing..
         }
     }
    
    public static double generateHashCode(String className) { 
        StringBuffer hashCode = new StringBuffer();
        try {
            byte messageDigest[];
            synchronized (algorithm) {
                algorithm.reset();
                algorithm.update(className.getBytes());
                messageDigest = algorithm.digest();
            }

            for (int i=0;i<messageDigest.length;i++) {
                hashCode.append(Integer.toOctalString(0xFF & messageDigest[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return Double.parseDouble(hashCode.toString());
    } 

    
}
