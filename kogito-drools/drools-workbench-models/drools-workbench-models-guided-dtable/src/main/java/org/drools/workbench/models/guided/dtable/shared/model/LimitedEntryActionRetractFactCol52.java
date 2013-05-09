/*
 * Copyright 2011 JBoss Inc
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
 * A column representing the retraction of a Fact on a Limited Entry decision
 * table. The Value will be the String identifier of the Fact Pattern being
 * retracted.
 */
public class LimitedEntryActionRetractFactCol52 extends ActionRetractFactCol52
        implements
        LimitedEntryCol {

    private static final long serialVersionUID = 510l;

    private DTCellValue52 value;

    public DTCellValue52 getValue() {
        return value;
    }

    public void setValue( DTCellValue52 value ) {
        this.value = value;
    }

}
