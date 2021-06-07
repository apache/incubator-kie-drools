/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.commons.utils;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.TransformationDictionary;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLTransformationDictionary</code> code-generators
 * out of <code>TransformationDictionary</code>s
 */
public class TransformationDictionaryUtils {

    /**
     *
     * @param transformationDictionary
     * @return
     */
    static MethodDeclaration getKiePMMLTransformationDictionaryInstantiationMethod(final TransformationDictionary transformationDictionary) {

    }

}
