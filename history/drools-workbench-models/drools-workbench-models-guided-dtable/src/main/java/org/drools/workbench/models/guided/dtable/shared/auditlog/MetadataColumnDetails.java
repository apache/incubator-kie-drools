/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.auditlog;

import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;

/**
 * Details of a Metadata column
 */
public class MetadataColumnDetails extends ColumnDetails {

    private static final long serialVersionUID = -4815318257058328788L;

    private String metadata;

    public MetadataColumnDetails() {
    }

    public MetadataColumnDetails( final MetadataCol52 column ) {
        super( column );
        this.metadata = column.getMetadata();
    }

    public String getMetadata() {
        return metadata;
    }

    /**
     * Returns the header for the metadata column.
     *
     * @return The metadata column's header. If the header is not present, returns the metadata string.
     */
    public String getColumnHeader() {
        String result = super.getColumnHeader();
        if (result == null || result.trim().length() == 0) result = metadata;
        return result;
    }

}
