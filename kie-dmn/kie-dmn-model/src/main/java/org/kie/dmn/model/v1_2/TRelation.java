/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_2;


import java.util.ArrayList;

import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.List;
import org.kie.dmn.model.api.Relation;

public class TRelation extends TExpression implements Relation {

    protected java.util.List<InformationItem> column;
    protected java.util.List<List> row;

    @Override
    public java.util.List<InformationItem> getColumn() {
        if (column == null) {
            column = new ArrayList<InformationItem>();
        }
        return this.column;
    }

    @Override
    public java.util.List<List> getRow() {
        if (row == null) {
            row = new ArrayList<List>();
        }
        return this.row;
    }

}
