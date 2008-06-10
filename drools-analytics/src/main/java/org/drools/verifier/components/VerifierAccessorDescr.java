package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierAccessorDescr extends VerifierComponent {

	private static int index = 0;

	public VerifierAccessorDescr() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.ACCESSOR;
	}
}
