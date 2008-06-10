package org.drools.verifier.dao;

public class VerifierResultFactory {

	public static VerifierResult createVerifierResult() {

		return new VerifierResultNormal();
	}
}
