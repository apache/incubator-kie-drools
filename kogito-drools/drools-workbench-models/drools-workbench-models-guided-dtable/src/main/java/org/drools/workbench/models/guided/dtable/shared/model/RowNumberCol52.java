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
 * This is a rule index column (i.e. just displays row's number)
 */
public class RowNumberCol52 extends DTColumnConfig52 {

    private static final long serialVersionUID = -2272148755430209968L;

    private static final DTCellValue52 DEFAULT_ROW_NUMBER = new DTCellValue52( new Integer( 0 ) );

    @Override
    public DTCellValue52 getDefaultValue() {
        return DEFAULT_ROW_NUMBER;
    }

}
