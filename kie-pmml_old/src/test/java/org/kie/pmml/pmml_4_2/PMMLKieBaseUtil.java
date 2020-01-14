/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2;

import org.assertj.core.api.Assertions;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class PMMLKieBaseUtil {

    public static KieBase createKieBaseWithPMML(String modelResourcePath) {
        Resource res = ResourceFactory.newClassPathResource(modelResourcePath);
        KieBase kieBase = new KieHelper().addResource(res, ResourceType.PMML).build();

        Assertions.assertThat(kieBase).isNotNull();

        return kieBase;
    }
}
