/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.query.preprocessor;

import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_TASKID;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserTasksPreprocessor implements DataSetPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTasksPreprocessor.class);
    
    private DataSetMetadata metadata;

    public UserTasksPreprocessor(DataSetMetadata metadata) {        
        this.metadata = metadata;
    }

    @Override
    public void preprocess(DataSetLookup lookup) {
        
        if (lookup.getLastGroupOp() == null) {
            LOGGER.debug("There is no group operation, adding one to eliminate duplicated tasks");
            DataSetGroup gOp = new DataSetGroup();
            gOp.setColumnGroup(new ColumnGroup(COLUMN_TASKID, COLUMN_TASKID));
            for (String columnId : metadata.getColumnIds()) {                
                gOp.addGroupFunction(new GroupFunction(columnId, columnId, null));
            }
            LOGGER.debug("Group operation {} added to dataset lookup {}", gOp, lookup);
            lookup.addOperation(gOp);
        }        
    }

}
