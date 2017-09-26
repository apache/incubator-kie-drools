package org.drools.pmml.pmml_4_2.model.regression;

import org.kie.api.definition.type.PropertyReactive;

/*
 * declare @{ pmmlPackageName }.RegNormalizationFactor
@propertyReactive
    context         : String        @key
    target          : String        @key
    den             : double
end

 */
@PropertyReactive
public class RegNormalizationFactor {
	private String context;
	private String target;
	private Double den;
	
	public RegNormalizationFactor() {
		
	}
	
	public RegNormalizationFactor(String context, String target, double den) {
		this.context = context;
		this.target = target;
		this.den = den;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Double getDen() {
		return den;
	}

	public void setDen(Double den) {
		this.den = den;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RegNormalizationFactor other = (RegNormalizationFactor) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (target == null) {
			if (other.target != null) {
				return false;
			}
		} else if (!target.equals(other.target)) {
			return false;
		}
		return true;
	}

	
}
