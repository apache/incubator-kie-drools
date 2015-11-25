/*
 * Copyright 2015 JBoss Inc
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

package org.kie.test.util.compare;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections.CollectionUtils;

/**
 * This class does deep, configurable reflection based comparison,
 * meant for usage with round-tripped (serialized, deserialized) objects.
 */
public class ComparePair {

    private Object orig;
    private Object copy;
    private Class<?> objInterface;

    private String[] nullFields = null;
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
        if (useGetMethods) {
            return compareObjectsViaGetMethods(orig, copy, objInterface);
        } else {
            compareObjectsViaFields(orig, copy, nullFields);
            return null;
        }
    }

    public void recursiveCompare() {
        Queue<ComparePair> compares = new LinkedList<ComparePair>();
        compares.add(this);
        while (!compares.isEmpty()) {
            compares.addAll(compares.poll().compare());
        }
    }

    public void addNullFields(String... fieldNames) {
        if (fieldNames == null) {
            return;
        }
        if (nullFields == null) {
            nullFields = new String[0];
        }
        String[] newNullFields = new String[nullFields.length + fieldNames.length];
        for (int i = 0; i < nullFields.length; ++i) {
            newNullFields[i] = nullFields[i];
        }
        for (int i = 0; i < fieldNames.length; ++i) {
            newNullFields[i + nullFields.length] = fieldNames[i];
        }
        this.nullFields = newNullFields;
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
                boolean skip = false;
                if (origField == null) {
                    if (nullFields != null) {
                        for (String nullField : nullFields) {
                            if (fieldName.equals(nullField)) {
                                skip = true;
                                break;
                            }
                        }
                    }
                    if (!skip) {
                        fail("Please fill in the " + fieldName + " field in the " + objInterface.getSimpleName() + "!");
                    }
                }
                if (skip) {
                    continue;
                }
                if (origField != null && !(origField instanceof Enum) && origField.getClass().getPackage().getName().startsWith("org.")) {
                    ComparePair newComPair = new ComparePair(origField, copyField, getInterface(origField));
                    newComPair.addNullFields(this.nullFields);
                    cantCompare.add(newComPair);
                    continue;
                } else if (origField instanceof List<?>) {
                    List<?> origList = (List) origField;
                    List<?> copyList = (List) copyField;
                    for (int i = 0; i < origList.size(); ++i) {
                        Class<?> newInterface = origField.getClass();
                        while (newInterface.getInterfaces().length > 0) {
                            newInterface = newInterface.getInterfaces()[0];
                        }
                        ComparePair newCompair = new ComparePair(origList.get(i), copyList.get(i), getInterface(origList.get(i)));
                        newCompair.addNullFields(this.nullFields);
                        cantCompare.add(newCompair);
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

    public static void compareObjectsViaFields(Object orig, Object copy) {
        compareObjectsViaFields(orig, copy, new String[] {});
    }

    public static void compareObjectsViaFields(Object orig, Object copy, String... skipFields) {
        Class<?> origClass = orig.getClass();
        assertEquals("copy is not an instance of " + origClass + " (" + copy.getClass().getSimpleName() + ")", origClass,
                copy.getClass());
        for (Field field : orig.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object origFieldVal = field.get(orig);
                Object copyFieldVal = field.get(copy);
                String fieldName = field.getName();

                boolean skip = false;
                for (String skipFieldName : skipFields) {
                    if (skipFieldName.matches(fieldName)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }

                boolean nullFound = false;
                if (origFieldVal == null || copyFieldVal == null) {
                    nullFound = true;
                    for (String nullFieldName : skipFields) {
                        if (nullFieldName.matches(fieldName)) {
                            nullFound = false;
                        }
                    }
                }
                String failMsg = origClass.getSimpleName() + "." + field.getName() + " is null";
                assertFalse(failMsg + "!", nullFound);

                if (copyFieldVal != origFieldVal) {
                    assertNotNull(failMsg + "in copy!", copyFieldVal);
                    assertNotNull(failMsg + "in original!", origFieldVal);
                    Package pkg = origFieldVal.getClass().getPackage();
                    if (pkg == null || pkg.getName().startsWith("java.")) {
                        if( origFieldVal.getClass().isArray() ) {
                            if( origFieldVal instanceof byte[] ) {
                                assertArrayEquals(origClass.getSimpleName() + "." + field.getName(), (byte []) origFieldVal, (byte []) copyFieldVal);
                            }
                        } else if( origFieldVal instanceof Map<?, ?> && copyFieldVal instanceof Map<?, ?> ) {
                            CollectionUtils.disjunction(
                                    ((Map) origFieldVal).values(),
                                    ((Map) copyFieldVal).values());
                        } else {
                            assertEquals(origClass.getSimpleName() + "." + field.getName(), origFieldVal, copyFieldVal);
                        }
                    } else {
                        compareObjectsViaFields(origFieldVal, copyFieldVal, skipFields);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to access " + field.getName() + " when testing " + origClass.getSimpleName()
                        + ".", e);
            }
        }
    }

}