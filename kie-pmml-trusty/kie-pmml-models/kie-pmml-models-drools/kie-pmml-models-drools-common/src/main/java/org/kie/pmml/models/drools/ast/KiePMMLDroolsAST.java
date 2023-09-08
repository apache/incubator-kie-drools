/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.ast;

import java.util.List;

/**
 * Data-class used to store information needed to generate a whole <b>Drools descr</b>
 */
public class KiePMMLDroolsAST {

    private final List<KiePMMLDroolsType> types;
    private final List<KiePMMLDroolsRule> rules;

    public KiePMMLDroolsAST(List<KiePMMLDroolsType> types, List<KiePMMLDroolsRule> rules) {
        this.types = types;
        this.rules = rules;
    }

    public List<KiePMMLDroolsType> getTypes() {
        return types;
    }

    public List<KiePMMLDroolsRule> getRules() {
        return rules;
    }
}
