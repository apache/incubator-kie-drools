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
package org.kie.pmml.evaluator.core.implementations;

import org.kie.pmml.api.enums.PMML_STEP;

/**
 * <code>PMMLStep</code>> common to all models, i.e. to overall execution.
 */
public class PMMLRuntimeStep extends AbstractPMMLStep {

    private static final long serialVersionUID = -881985972308818180L;
    private final PMML_STEP pmmlStep;

    public PMMLRuntimeStep(PMML_STEP pmmlStep) {
        this.pmmlStep = pmmlStep;
    }

    public PMML_STEP getPmmlStep() {
        return pmmlStep;
    }
}
