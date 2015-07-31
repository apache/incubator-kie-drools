/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.compiler.GuidedRuleTemplateConverter;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.guided.template.shared.TemplateModel;

public class GuidedRuleTemplateConverterImpl implements GuidedRuleTemplateConverter {

    @Override
    public byte[] convert( byte[] input ) {
        String xml = new String( input, IoUtils.UTF8_CHARSET );
        TemplateModel model = (TemplateModel) RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        String drl = new StringBuilder().append( RuleTemplateModelDRLPersistenceImpl.getInstance().marshal( model ) ).toString();
        return drl.getBytes( IoUtils.UTF8_CHARSET );
    }
}
