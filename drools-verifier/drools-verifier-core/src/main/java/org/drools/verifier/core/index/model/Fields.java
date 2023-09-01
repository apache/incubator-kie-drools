package org.drools.verifier.core.index.model;

import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.MultiMap;

public class Fields
        extends FieldsBase<Field> {

    public Where<FieldSelector, FieldListen> where(final Matcher matcher) {
        return new Where<FieldSelector, FieldListen>() {
            @Override
            public FieldSelector select() {
                return new FieldSelector(matcher);
            }

            @Override
            public FieldListen listen() {
                return new FieldListen(matcher);
            }
        };
    }

    public class FieldSelector
            extends Select<Field> {

        public FieldSelector(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }

        public Conditions conditions() {
            final Conditions conditions = new Conditions();

            final MultiMap<Value, Field, List<Field>> subMap = asMap();
            if (subMap != null) {
                final Collection<Field> fields = subMap.allValues();
                for (final Field field : fields) {
                    conditions.merge(field.getConditions());
                }
            }

            return conditions;
        }

        public Actions actions() {
            final Actions actions = new Actions();

            final MultiMap<Value, Field, List<Field>> subMap = asMap();
            if (subMap != null) {
                final Collection<Field> fields = subMap.allValues();
                for (final Field field : fields) {
                    actions.merge(field.getActions());
                }
            }

            return actions;
        }
    }

    public class FieldListen
            extends Listen<Field> {

        public FieldListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
