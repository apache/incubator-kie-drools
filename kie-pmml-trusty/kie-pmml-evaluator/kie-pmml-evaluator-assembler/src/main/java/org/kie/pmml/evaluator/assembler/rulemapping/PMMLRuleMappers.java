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
package org.kie.pmml.evaluator.assembler.rulemapping;

import java.util.List;

/**
 * Class used to retrieve <b>all</b> the <code>org.drools.model.Model</code>s associated to a <code>KiePMMLModel</code>.
 *
 * <p>For <code>KiePMMLDroolsModel</code>, there is a 1:1 relationship.</p>
 * <p>For <code>KiePMMLMiningModel</code>, there is a 1:n relationship, where <i>n</i> represent the nested <code>KiePMMLDroolsModel</code>s.</p>
 * <p>For all other <code>KiePMMLModel</code>s, this is unused.</p>
 */
public interface PMMLRuleMappers {

    List<PMMLRuleMapper> getPMMLRuleMappers();

}
