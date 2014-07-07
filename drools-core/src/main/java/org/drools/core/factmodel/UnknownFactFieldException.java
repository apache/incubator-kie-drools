package org.drools.core.factmodel;

import org.kie.api.definition.type.FactType;


/**
 * Thrown by FactType when get/set is called on a unknown field
 */
public class UnknownFactFieldException extends FactModelException {

	private static final long serialVersionUID = 1L;

	public UnknownFactFieldException(FactType type, String unknownfield) {
		super("Could not find field " + unknownfield + " in " + type);
	}
}
