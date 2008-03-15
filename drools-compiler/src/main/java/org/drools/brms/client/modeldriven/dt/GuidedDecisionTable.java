package org.drools.brms.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        conditionCols   = (List)in.readObject();
        actionCols   = (List)in.readObject();
        data   = (String[][])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(conditionCols);
        out.writeObject(actionCols);
        out.writeObject(data);
    }
}
