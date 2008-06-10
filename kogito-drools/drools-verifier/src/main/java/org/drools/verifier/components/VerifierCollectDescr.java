package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierCollectDescr extends VerifierComponent {

	private static int index = 0;

	private int insidePatternId;
	private String classMethodName;

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.COLLECT;
	}

	public int getInsidePatternId() {
		return insidePatternId;
	}

	public void setInsidePatternId(int insidePatternId) {
		this.insidePatternId = insidePatternId;
	}

	public String getClassMethodName() {
		return classMethodName;
	}

	public void setClassMethodName(String classMethodName) {
		this.classMethodName = classMethodName;
	}

	public VerifierCollectDescr() {
		super(index++);
	}
}
