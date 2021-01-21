/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jitexecutor.dmn;

import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DynamicDMNContextBuilder;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.dmn.rest.DMNResult;

@ApplicationScoped
public class JITDMNServiceImpl implements JITDMNService {

    @Override
    public org.kie.kogito.dmn.rest.DMNResult evaluateModel(String modelXML, Map<String, Object> context) {
        Resource modelResource = ResourceFactory.newReaderResource(new StringReader(modelXML), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        DMNModel dmnModel = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = new DynamicDMNContextBuilder(dmnRuntime.newContext(), dmnModel).populateContextWith(context);
        org.kie.dmn.api.core.DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        return new DMNResult(dmnModel.getNamespace(), dmnModel.getName(), dmnResult);
    }
}
