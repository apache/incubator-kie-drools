package org.jbpm.services.task.jaxb;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ComparePair {
    
    private Object orig;
    private Object copy;
    private Class<?> objInterface;
    
    private String [] nullFields = null;
    private boolean useGetMethods = true;

    public static void compareOrig(Object origObj, Object newObj, Class objClass) { 
        ComparePair compare = new ComparePair(origObj, newObj, objClass);
        Queue<ComparePair> compares = new LinkedList<ComparePair>();
        compares.add(compare);
        while (!compares.isEmpty()) {
            compares.addAll(compares.poll().compare());
        }
    }
    
    public ComparePair(Object a, Object b, Class<?> c) {
        this.orig = a;
        this.copy = b;
        this.objInterface = c;
    }

    public List<ComparePair> compare() {
        if( useGetMethods ) { 
            return compareObjectsViaGetMethods(orig, copy, objInterface);
        } else { 
            compareObjectsViaFields(orig, copy, nullFields);
            return null;
        }
        
    }

    private List<ComparePair> compareObjectsViaGetMethods(Object orig, Object copy, Class<?> objInterface) {
        List<ComparePair> cantCompare = new ArrayList<ComparePair>();
        for (Method getIsMethod : objInterface.getDeclaredMethods()) {
            String methodName = getIsMethod.getName();
            String fieldName;
            if (methodName.startsWith("get")) {
                fieldName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                fieldName = methodName.substring(2);
            } else {
                continue;
            }
            // getField -> field (lowercase f)
            fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            try {
                Object origField = getIsMethod.invoke(orig, new Object[0]);
                Object copyField = getIsMethod.invoke(copy, new Object[0]);
                if (origField == null) {
                    fail("Please fill in the " + fieldName + " field in the " + objInterface.getSimpleName() + "!");
                }
                if( !(origField instanceof Enum) && origField.getClass().getPackage().getName().startsWith("org.")) {
                    cantCompare.add(new ComparePair(origField, copyField, getInterface(origField)));
                    continue;
                } else if (origField instanceof List<?>) {
                    List<?> origList = (List) origField;
                    List<?> copyList = (List) copyField;
                    for (int i = 0; i < origList.size(); ++i) {
                        Class<?> newInterface = origField.getClass();
                        while (newInterface.getInterfaces().length > 0) {
                            newInterface = newInterface.getInterfaces()[0];
                        }
                        cantCompare.add(new ComparePair(origList.get(i), copyList.get(i), getInterface(origList.get(i))));
                    }
                    continue;
                }
                assertEquals(fieldName, origField, copyField);
            } catch (Exception e) {
                throw new RuntimeException("Unable to compare " + fieldName, e);
            }
        }
        return cantCompare;
    }

    private Class<?> getInterface(Object obj) {
        Class<?> newInterface = obj.getClass();
        Class<?> parent = newInterface;
        while (parent != null) {
            parent = null;
            if (newInterface.getInterfaces().length > 0) {
                Class<?> newParent = newInterface.getInterfaces()[0];
                if (newParent.getPackage().getName().startsWith("org.")) {
                    parent = newInterface = newParent;
                }
            }
        }
        return newInterface;
    }

    public static void compareObjectsViaFields( Object orig, Object copy ) {
        compareObjectsViaFields(orig, copy, new String [] {} );
    }
    
    public static void compareObjectsViaFields( Object orig, Object copy, String... nullFields ) {
        Class<?> origClass = orig.getClass();
        assertEquals( "copy is not an instance of " + origClass + " (" + copy.getClass().getSimpleName() + ")",  
                origClass, copy.getClass() );
        for (Field field : orig.getClass().getDeclaredFields() ) {
            try { 
                field.setAccessible(true);
                Object origFieldVal = field.get(orig);
                Object copyFieldVal = field.get(copy);
    
                boolean nullFound = false;
                if( origFieldVal == null || copyFieldVal == null ) { 
                    nullFound = true;
                    String fieldName = field.getName();
                    for( String nullFieldName : nullFields ) { 
                        if( nullFieldName.matches(fieldName) ) { 
                            nullFound = false;
                        }
                    }
                }
                String failMsg = origClass.getSimpleName() + "." + field.getName() + " is null";
                assertFalse( failMsg + "!", nullFound );
    
                if( copyFieldVal != origFieldVal ) { 
                    if( copyFieldVal == null ) { 
                        fail( failMsg + " in copy!" );
                    } else if( origFieldVal == null ) { 
                        fail( failMsg + "in original!" );
                    }
                    if( origFieldVal.getClass().getPackage().getName().startsWith("java.") ) { 
                        assertEquals( origClass.getSimpleName() + "." + field.getName(), origFieldVal, copyFieldVal );
                    } else { 
                        compareObjectsViaFields(origFieldVal, copyFieldVal, nullFields);
                    }
                }
            } catch( Exception e ) { 
                throw new RuntimeException("Unable to access " + field.getName() + " when testing " + origClass.getSimpleName() + ".", e ); 
            }
    
        }
    }
}