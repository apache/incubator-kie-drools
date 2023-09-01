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
            column = new ArrayList<>();
        }
        return this.column;
    }

    @Override
    public java.util.List<List> getRow() {
        if (row == null) {
            row = new ArrayList<>();
        }
        return this.row;
    }

}
