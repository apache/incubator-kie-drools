package org.drools.verifier.core.index.model;

import java.util.Collection;

import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.IndexedKeyTreeMap;

public class Columns {

    public final IndexedKeyTreeMap<Column> map = new IndexedKeyTreeMap<>(Column.keyDefinitions());

    public Columns() {

    }

    public Columns(final Collection<Column> columns) {
        for (final Column column : columns) {
            add(column);
        }
    }

    public void add(final Column column) {
        map.put(column,
                column.getIndex());
    }

    public void merge(final Columns columns) {
        map.merge(columns.map);
    }

    public Where<ColumnSelect, ColumnListen> where(final Matcher matcher) {
        return new Where<ColumnSelect, ColumnListen>() {

            @Override
            public ColumnSelect select() {
                return new ColumnSelect(matcher);
            }

            @Override
            public ColumnListen listen() {
                return new ColumnListen(matcher);
            }
        };
    }

    public void remove(final Column column) {
        column.getUuidKey().retract();
    }

    public class ColumnSelect
            extends Select<Column> {

        public ColumnSelect(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }

    public class ColumnListen
            extends Listen<Column> {

        public ColumnListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
