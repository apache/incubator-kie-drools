package org.drools.base.rule.accessor;

import java.io.Serializable;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.Declaration;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;

public interface Salience extends Serializable {

    int DEFAULT_SALIENCE_VALUE = 0;

    int getValue(final Match activation,
                 final Rule rule,
                 final ValueResolver valueResolver);

    int getValue();

    boolean isDynamic();

    default boolean isDefault() {
        return getValue() == DEFAULT_SALIENCE_VALUE;
    }

    default Declaration[] findDeclarations( Map<String, Declaration> decls) {
        return null;
    }
}
