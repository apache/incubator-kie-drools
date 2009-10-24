package org.drools.verifier.data;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierReportFactory {

	public static VerifierReport newVerifierReport() {
		return new VerifierReportImpl(newVerifierData());
	}

	public static VerifierReport newVerifierReport(VerifierData data) {
		return new VerifierReportImpl(data);
	}

	public static VerifierData newVerifierData() {
		return new VerifierDataMaps();
	}
}
