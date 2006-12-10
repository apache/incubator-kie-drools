/**
 * 
 */
package org.codehaus.jfdi.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jfdi.interpreter.operations.Expr;

public class AnonListValue implements ValueHandler {

	private static final long serialVersionUID = 320L;

	private final Expr[] values;

	private Object cachedValue = null;
	
	public AnonListValue(final List values) {
		this( (Expr[]) values.toArray( new Expr[ values.size() ] ) );
	}

	public AnonListValue(final Expr[] values) {
		this.values = values;
	}

	public Object getValue() {
		if (this.cachedValue == null) {
			final List resolvedList = new ArrayList();

			for (int i = 0; i < values.length; ++i) {
				resolvedList.add(values[i].getValue());
			}

			this.cachedValue = resolvedList;
		}
		return this.cachedValue;
	}

	public Class getExtractToClass() {
		return List.class;
	}

	public void reset() {
		this.cachedValue = null;
	}

	public String toString() {
		return "[ListValue list=" + Arrays.asList(values) + "]";
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + values.hashCode();
		return result;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		final AnonListValue other = (AnonListValue) object;
		
		if (values.length != other.values.length) {
			return false;
		}
		
		for ( int i = 0 ; i < values.length ; ++i ) {
			if ( values[i] != other.values[i] ) {
				return false;
			}
		}
		
		return true;
	}

	public Class getType() {
		return List.class;
	}

	public boolean isFinal() {
		return true;
	}

	public boolean isLiteral() {
		return false;
	}

	public boolean isLocal() {
		return false;
	}

	public void setValue(Object variable) {
		throw new UnsupportedOperationException(
				"Not allowed to set the value of a list.");
	}

}