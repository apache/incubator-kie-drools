/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratedSources {

    private static final Logger logger = LoggerFactory.getLogger(GeneratedSources.class);

    private final Map<String, String> allClasses = new HashMap<>();

    private String alphaNetworkClass = null;

    public void addNewSourceClass(String classNameWithPackage, String classSourceCode) {
        allClasses.put(classNameWithPackage, classSourceCode);
    }

    public void addNewAlphaNetworkClass(String alphaNetworkClassWithPackage, String toString) {
        addNewSourceClass(alphaNetworkClassWithPackage, toString);
        this.alphaNetworkClass = alphaNetworkClassWithPackage;
    }

    public DMNCompiledAlphaNetwork newInstanceOfAlphaNetwork(Map<String, Class<?>> compiledClasses) {
        Class<?> inputSetClass = compiledClasses.get(alphaNetworkClass);
        Object inputSetInstance;
        try {
            inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (DMNCompiledAlphaNetwork) inputSetInstance;
    }

    public Map<String, String> getAllClasses() {
        return Collections.unmodifiableMap(allClasses);
    }

    public void logGeneratedClasses() {
        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, String> kv : allClasses.entrySet()) {
                logger.debug("Generated class {}", kv.getKey());
                logger.debug(kv.getValue());
            }
        }
    }

    public void addUnaryTestClasses(Map<String, String> unaryTestClasses) {
        allClasses.putAll(unaryTestClasses);
    }
}
