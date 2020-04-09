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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.List;
import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

/**
 * Abstract class to be extended to generate <code>KiePMMLDroolsRule</code>s out of a <code>Predicate</code>s
 */
public class KiePMMLTreeModelAbstractPredicateASTFactory {

    protected final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    protected final List<KiePMMLOutputField> outputFields;
    protected final List<KiePMMLDroolsRule> rules;

    protected KiePMMLTreeModelAbstractPredicateASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        this.fieldTypeMap = fieldTypeMap;
        this.outputFields = outputFields;
        this.rules = rules;
    }
}
