package org.drools.brms.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.brl.PortableObject;

/**
 * This is a decision table model for a guided editor. It is not template or XLS based.
 * (template could be done relatively easily by taking a template, as a String, and then String[][] data and driving the SheetListener
 * interface in the decision tables module).
 *
 * This works by taking the column definitions, and combining them with the table of data to produce rule models.
 *
 * @author Michael Neale
 */
public class GuidedDecisionTable implements PortableObject {

	/**
	 * The name - obviously.
	 */
	public String tableName;

	/**
	 * @gwt.typeArgs <org.drools.brms.client.modeldriven.dt.AttributeCol>
	 */
	public List attributeCols = new ArrayList();

	/**
	 * @gwt.typeArgs <org.drools.brms.client.modeldriven.dt.ConditionCol>
	 */
	public List conditionCols = new ArrayList();

	/**
	 * @gwt.typeArgs <org.drools.brms.client.modeldriven.dt.ActionCol>
	 */
	public List actionCols = new ArrayList();

	/**
	 * First column is always row number.
	 * Second column is description.
	 * Subsequent ones follow the above column definitions:
	 * attributeCols, then conditionCols, then actionCols, in that order, left to right.
	 */
	public String[][] data;


	public GuidedDecisionTable() {}

}
