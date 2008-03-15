package org.drools.brms.client.modeldriven.dt;

import org.drools.brms.client.modeldriven.brl.PortableObject;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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

	//TODO: add in precondition(s)


	public GuidedDecisionTable() {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tableName       = (String)in.readObject();
        attributeCols   = (List)in.readObject();
        conditionCols   = (List)in.readObject();
        actionCols      = (List)in.readObject();
        data   = (String[][])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tableName);
        out.writeObject(attributeCols);
        out.writeObject(conditionCols);
        out.writeObject(actionCols);
        out.writeObject(data);
    }
}
