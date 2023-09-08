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
package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TKnowledgeRequirement extends KieDMNModelInstrumentedBase implements KnowledgeRequirement, NotADMNElementInV11 {

    private DMNElementReference requiredKnowledge;

    @Override
    public DMNElementReference getRequiredKnowledge() {
        return requiredKnowledge;
    }

    @Override
    public void setRequiredKnowledge(final DMNElementReference value) {
        this.requiredKnowledge = value;
    }

}
