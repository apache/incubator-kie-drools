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

/**
 * This is a rule description
 */
public class DescriptionCol52 extends DTColumnConfig52 {

    private static final long serialVersionUID = 729l;

    private static final DTCellValue52 DEFAULT_DESCRIPTION = new DTCellValue52( "" );

    @Override
    public DTCellValue52 getDefaultValue() {
        return DEFAULT_DESCRIPTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DescriptionCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
