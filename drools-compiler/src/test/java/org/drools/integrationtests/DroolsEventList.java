package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.drools.runtime.rule.Row;
import org.drools.runtime.rule.ViewChangedEventListener;

import ca.odell.glazedlists.AbstractEventList;

public class DroolsEventList extends AbstractEventList<Row> implements ViewChangedEventListener {
    List<Row> data = new ArrayList<Row>();
    
    
    @Override
    public Row get(int index) {
        return this.data.get( index );
    }

    @Override
    public int size() {
        return this.data.size();
    }

    public void dispose() {

    }

    public void rowAdded(Row row) {
        int index = size();
        // create the change event
        updates.beginEvent();
        updates.elementInserted(index, row);
        // do the actual add
        boolean result = data.add(row);
        // fire the event
        updates.commitEvent();

    }

    public void rowRemoved(Row row) {
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
