package org.drools.brms.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.brl.PortableObject;

public class GuidedDecisionTable implements PortableObject {

	public List conditionCols = new ArrayList();
	public List actionCols = new ArrayList();

	public String[][] data;


	public GuidedDecisionTable() {}

}
