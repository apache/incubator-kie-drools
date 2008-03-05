package org.drools.brms.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.brl.PortableObject;

public class GuidedDecisionTable implements PortableObject {

	public List conditionCols = new ArrayList();
	public List actionCols = new ArrayList();

	/**
	 * First column is always row number.
	 * Second column is description.
	 * Subsequent ones follow the above column definitions.
	 */
	public String[][] data;


	public GuidedDecisionTable() {}

}
