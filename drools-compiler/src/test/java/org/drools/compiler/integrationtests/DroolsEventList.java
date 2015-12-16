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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import ca.odell.glazedlists.AbstractEventList;

public class DroolsEventList extends AbstractEventList<Row> implements ViewChangedEventListener {
    List<Row> data = new ArrayList<Row>();
    
    public Row get(int index) {
        return this.data.get( index );
    }

    public int size() {
        return this.data.size();
    }

    public void dispose() {

    }

    public void rowInserted(Row row) {
        int index = size();
        // create the change event
        updates.beginEvent();
        updates.elementInserted(index, row);
        // do the actual add
        boolean result = data.add(row);
        // fire the event
        updates.commitEvent();

    }

    public void rowDeleted(Row row) {
        int index = this.data.indexOf( row );
        // create the change event
        updates.beginEvent();
        // do the actual remove
        Row removed = data.remove( index );
        updates.elementDeleted(index, removed);
        
        updates.commitEvent();

    }

    public void rowUpdated(Row row) {
        int index = this.data.indexOf( row );
        // create the change event
        updates.beginEvent();
        // fire the event
        updates.elementUpdated(index, row, row);
        updates.commitEvent();

    }

}
