package org.drools.kiesession.session;

import java.util.Arrays;
import java.util.Objects;

class QuerySpecification {
	
	private String name;
	private Object[] arguments;

	QuerySpecification(String name, Object[] arguments) {
		this.name = name;
		this.arguments = arguments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(arguments);
		result = prime * result + Objects.hash(name);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuerySpecification other = (QuerySpecification) obj;
		return Arrays.deepEquals(arguments, other.arguments) && Objects.equals(name, other.name);
	}
	
}