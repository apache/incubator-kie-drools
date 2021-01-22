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
package org.drools.ruleunit.command.pmml.mock;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;
import org.kie.internal.pmml.PMMLCommandExecutor;

public class PMMLCommandExecutorMock implements PMMLCommandExecutor {

    @Override
    public PMML4Result execute(PMMLRequestData pmmlRequestData, Context context) {
        final PMML4Result toReturn = new PMML4Result();
        toReturn.setResultCode("PMMLCommandExecutorTest");
        return toReturn;
    }
}
