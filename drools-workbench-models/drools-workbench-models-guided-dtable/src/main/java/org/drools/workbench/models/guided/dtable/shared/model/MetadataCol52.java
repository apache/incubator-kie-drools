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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

/**
 * This is a rule metadata - eg @foo(bar) etc.
 */
public class MetadataCol52 extends DTColumnConfig52 {

    private String metadata;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_METADATA = "metadata";

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata( String metadata ) {
        this.metadata = metadata;
    }

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        MetadataCol52 other = (MetadataCol52) otherColumn;

        // Field: metadata.
        if ( !isEqualOrNull( this.getMetadata(),
                             other.getMetadata() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_METADATA,
                                                     this.getMetadata(),
                                                     other.getMetadata() ) );
        }

        return result;
    }

    /**
     * Clones this metadata column instance.
     * @return The cloned instance.
     */
    public MetadataCol52 cloneColumn() {
        MetadataCol52 cloned = new MetadataCol52();
        cloned.setMetadata( getMetadata() );
        cloned.cloneCommonColumnConfigFrom( this );
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetadataCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        MetadataCol52 that = (MetadataCol52) o;

        return metadata != null ? metadata.equals(that.metadata) : that.metadata == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result=~~result;
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        result=~~result;
        return result;
    }
}
