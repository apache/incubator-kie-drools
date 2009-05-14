package org.drools.guvnor.client.modeldriven.brl;

/**
 * This holds values for rule metadata (eg @foo(bar), @foo2(bar2)).
 * 
 * @author Michael Rhoden
 */
public class RuleMetadata implements PortableObject {

	public String attributeName;
	public String value;
	
	public RuleMetadata() {
	}

	public RuleMetadata(final String name, final String value) {
		this.attributeName = name;
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("@");
		ret.append(this.attributeName);
		if (this.value != null) {
			ret.append("(");
			ret.append(this.value);
			ret.append(")");
		}
		return ret.toString();
	}

}
