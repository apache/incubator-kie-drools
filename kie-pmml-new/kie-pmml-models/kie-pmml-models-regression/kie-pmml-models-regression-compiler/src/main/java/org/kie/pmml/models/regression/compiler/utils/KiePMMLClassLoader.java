/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.regression.compiler.utils;

import java.util.HashMap;
import java.util.Map;

public class KiePMMLClassLoader extends ClassLoader {

    private Map<String, KiePMMLByteCode> customCompiledCode = new HashMap<>();

    public KiePMMLClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void addCode(KiePMMLByteCode cc) {
        customCompiledCode.put(cc.getName(), cc);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        KiePMMLByteCode cc = customCompiledCode.get(name);
        if (cc == null) {
            return super.findClass(name);
        }
        byte[] byteCode = cc.getByteCode();
        return defineClass(name, byteCode, 0, byteCode.length);
    }
}