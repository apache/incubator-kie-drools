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
*/

package org.drools.workbench.models.guided.dtable.backend;

import org.drools.compiler.compiler.GuidedDecisionTableProvider;
import org.drools.compiler.compiler.ResourceConversionResult;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.io.InputStream;

public class GuidedDecisionTableProviderImpl implements GuidedDecisionTableProvider {

    @Override
    public ResourceConversionResult loadFromInputStream(InputStream is) throws IOException {
        String xml = new String(IoUtils.readBytesFromInputStream(is), IoUtils.UTF8_CHARSET);
        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal(xml);
        String content = GuidedDTDRLPersistence.getInstance().marshal(model);
        if (hasDSLSentences(model)) {
            return new ResourceConversionResult(content, ResourceType.DSLR);
        } else {
            return new ResourceConversionResult(content, ResourceType.DRL);
        }
    }

    // Check if the model uses DSLSentences and hence requires expansion. This code is copied from GuidedDecisionTableUtils.
    // GuidedDecisionTableUtils also handles data-types, enums etc and hence requires a DataModelOracle to function. Loading
    // a DataModelOracle just to determine whether the model has DSLs is an expensive operation and not needed here.
    public static boolean hasDSLSentences(final GuidedDecisionTable52 model) {
        for (CompositeColumn<? extends BaseColumn> column : model.getConditions()) {
            if (column instanceof BRLConditionColumn) {
                final BRLConditionColumn brlColumn = (BRLConditionColumn) column;
                for (IPattern pattern : brlColumn.getDefinition()) {
                    if (pattern instanceof DSLSentence) {
                        return true;
                    }
                }
            }
        }
        for (ActionCol52 column : model.getActionCols()) {
            if (column instanceof BRLActionColumn) {
                final BRLActionColumn brlColumn = (BRLActionColumn) column;
                for (IAction action : brlColumn.getDefinition()) {
                    if (action instanceof DSLSentence) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
