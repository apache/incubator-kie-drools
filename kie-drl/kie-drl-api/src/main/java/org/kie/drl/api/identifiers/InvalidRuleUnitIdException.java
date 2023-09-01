package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;

public class InvalidRuleUnitIdException extends IllegalArgumentException {
    public InvalidRuleUnitIdException(LocalId id) {
        super("Not a valid rule unit ID" + id.asLocalUri());
    }
}
