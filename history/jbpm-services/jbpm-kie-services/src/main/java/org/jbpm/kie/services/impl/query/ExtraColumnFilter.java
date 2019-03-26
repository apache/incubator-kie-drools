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

package org.jbpm.kie.services.impl.query;

import java.io.Serializable;

public class ExtraColumnFilter implements Serializable {

    private static final long serialVersionUID = -3715417647758217121L;

    private String newColumnId;
    private String columnId;
    
    public ExtraColumnFilter(String columnId, String newColumnId) {        
        this.columnId = columnId;
        this.newColumnId = newColumnId;
    }

    public String getNewColumnId() {
        return newColumnId;
    }
    
    public void setNewColumnId(String newColumnId) {
        this.newColumnId = newColumnId;
    }
    
    public String getColumnId() {
        return columnId;
    }
    
    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}
