package org.drools.verifier.core.index.model;

import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;

public class ObjectFields
        extends FieldsBase<ObjectField> {

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
            extends Select<ObjectField> {

        public FieldSelector(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }

    public class FieldListen
            extends Listen<ObjectField> {

        public FieldListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
