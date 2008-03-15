package org.drools.brms.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class FactData implements Fixture {

    /**
     * The type (class)
     */
    public String type;

    /**
     * The name of the "variable"
     */
    public String name;

    /**
     * @gwt.typeArgs <org.drools.brms.client.modeldriven.testing.FactData>
     */
    public List fieldData = new ArrayList();

    /**
     * If its a modify, obviously we are modifying existing data in working memory.
     */
    public boolean isModify;

    public FactData() {}
    public FactData(String type, String name, List fieldData, boolean modify) {

        this.type = type;
        this.name = name;
        this.fieldData = fieldData;
        this.isModify = modify;

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type    = (String)in.readObject();
        name    = (String)in.readObject();
        fieldData   = (List)in.readObject();
        isModify    = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(type);
        out.writeObject(name);
        out.writeObject(fieldData);
        out.writeBoolean(isModify);
    }


}
