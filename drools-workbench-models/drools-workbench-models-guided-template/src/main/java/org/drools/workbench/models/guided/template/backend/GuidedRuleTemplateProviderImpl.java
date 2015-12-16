/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.workbench.models.guided.template.backend;

import org.drools.compiler.compiler.GuidedRuleTemplateProvider;
import org.drools.compiler.compiler.ResourceConversionResult;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.io.InputStream;

public class GuidedRuleTemplateProviderImpl implements GuidedRuleTemplateProvider {

    @Override
    public ResourceConversionResult loadFromInputStream(InputStream is) throws IOException {
        String xml = new String(IoUtils.readBytesFromInputStream(is), IoUtils.UTF8_CHARSET);
        TemplateModel model = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal(xml);
        String content = RuleTemplateModelDRLPersistenceImpl.getInstance().marshal(model);
        if (model.hasDSLSentences()) {
            return new ResourceConversionResult(content, ResourceType.DSLR);
        } else {
            return new ResourceConversionResult(content, ResourceType.DRL);
        }
    }

}
