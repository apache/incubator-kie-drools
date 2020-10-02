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

package org.drools.verifier.jarloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarInputStream;

import com.google.common.collect.TreeMultimap;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.mvel.asm.ClassFieldInspectorImpl;

public class PackageHeaderLoader {

    private final static Collection<String> IGNORED_FIELDS = getIgnoredFields();

    private Set<String> classNames = new HashSet<String>();

    private Map<String, String> fieldTypesByClassAndFieldNames = new HashMap<String, String>();

    private TreeMultimap<String, String> fieldsByClassNames = TreeMultimap.create();
    private List<String> missingClasses = new ArrayList<String>();

    public PackageHeaderLoader(Collection<String> imports, List<JarInputStream> jarInputStreams) throws IOException {
        findImportsFromJars(imports, jarInputStreams);
    }

    private void findImportsFromJars(Collection<String> imports, List<JarInputStream> jarInputStreams) throws IOException {
        ClassTypeResolver resolver = new ClassTypeResolver(new HashSet<String>(), new VerifierMapBackedClassLoader(jarInputStreams));

        for (String className : imports) {

            classNames.add(className);

            try {

                Class clazz = resolver.resolveType(className);
                addFields(clazz);
            } catch (ClassNotFoundException e) {
                missingClasses.add(className);
            }
        }
    }

    private void addFields(Class clazz) throws IOException {
        String className = clazz.getName();

        ClassFieldInspectorImpl inspector = new ClassFieldInspectorImpl(clazz);
        Set<String> fieldNames = inspector.getFieldNames().keySet();
        Map<String, Class<?>> fieldTypes = inspector.getFieldTypes();

        addThisField(className);

        for (String field : fieldNames) {
            if (IGNORED_FIELDS.contains(field)) {
                continue;
            }
            fieldsByClassNames.put(className, field);
            fieldTypesByClassAndFieldNames.put(className + "." + field, fieldTypes.get(field).getName());
        }
    }

    private void addThisField(String className) {
        fieldsByClassNames.put(className, "this");
        fieldTypesByClassAndFieldNames.put(className + "." + "this", className);
    }

    public Collection<String> getClassNames() {
        return classNames;
    }

    public Collection<String> getFieldNames(String className) {
        return fieldsByClassNames.get(className);
    }

    public String getFieldType(String className, String fieldName) {
        return fieldTypesByClassAndFieldNames.get(className + "." + fieldName);
    }

    public Collection<String> getMissingClasses() {
        return missingClasses;
    }

    private static Collection<String> getIgnoredFields() {
        Collection<String> fields = new ArrayList<String>();

        fields.add("toString");
        fields.add("hashCode");
        fields.add("class");

        return fields;
    }
}
