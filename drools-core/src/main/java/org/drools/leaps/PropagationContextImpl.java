package org.drools.leaps;

import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * RETEOO copy for leaps
 * 
 * @author Alexander Bagerman
 * 
 */
public class PropagationContextImpl implements PropagationContext {
	private final int type;

	private final Rule rule;

	private final Activation activation;

	private final long propagationNumber;

	public PropagationContextImpl(long number, int type, Rule rule,
			Activation activation) {
		this.type = type;
		this.rule = rule;
		this.activation = activation;
		this.propagationNumber = number;
	}

	public long getPropagationNumber() {
		return this.propagationNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.reteoo.PropagationContext#getRuleOrigin()
	 */
	public Rule getRuleOrigin() {
		return this.rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.reteoo.PropagationContext#getActivationOrigin()
	 */
	public Activation getActivationOrigin() {
		return this.activation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.reteoo.PropagationContext#getType()
	 */
	public int getType() {
		return this.type;
	}

}
