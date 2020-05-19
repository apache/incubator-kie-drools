/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.backend.util;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.soup.project.datamodel.commons.IUpgradeHelper;

/**
 * Adds rule name column
 */
public class GuidedDecisionTableUpgradeHelper4
        implements
        IUpgradeHelper<GuidedDecisionTable52, GuidedDecisionTable52> {

    private static final long VERSION_WHERE_RULE_NAME_WAS_ADDED = 739l;

    public GuidedDecisionTable52 upgrade(GuidedDecisionTable52 source) {

        source.getRuleNameColumn(); // Visit to make sure the column is added

        if (source.getVersion() < VERSION_WHERE_RULE_NAME_WAS_ADDED) {
            for (List<DTCellValue52> row : source.getData()) {
                row.add(1, new DTCellValue52(""));
            }
        }

        return source;
    }
}
