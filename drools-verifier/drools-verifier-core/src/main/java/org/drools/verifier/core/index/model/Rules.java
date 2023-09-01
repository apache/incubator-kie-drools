package org.drools.verifier.core.index.model;

import java.util.Collection;

import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.IndexedKeyTreeMap;

public class Rules {

    public final IndexedKeyTreeMap<Rule> map = new IndexedKeyTreeMap<>(Rule.keyDefinitions());

    public Rules() {

    }

    public Rules(final Collection<Rule> rules) {
        for (final Rule rule : rules) {
            add(rule);
        }
    }

    public Where<RulesSelect, RulesListen> where(final Matcher matcher) {
        return new Where<RulesSelect, RulesListen>() {
            @Override
            public RulesSelect select() {
                return new RulesSelect(matcher);
            }

            @Override
            public RulesListen listen() {
                return new RulesListen(matcher);
            }
        };
    }

    public void merge(final Rules rules) {
        map.merge(rules.map);
    }

    public void add(final Rule rule) {
        map.put(rule,
                rule.getIndex());
    }

    public void remove(final Rule rule) {
        rule.getUuidKey().retract();
    }

    public class RulesSelect
            extends Select<Rule> {

        public RulesSelect(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }

        public Patterns patterns() {
            final Collection<Rule> rules = all();

            final Patterns patterns = new Patterns();

            for (final Rule rule : rules) {
                patterns.merge(rule.getPatterns());
            }

            return patterns;
        }
    }

    public class RulesListen
            extends Listen<Rule> {

        public RulesListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
