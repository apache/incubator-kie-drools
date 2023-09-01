package org.drools.impact.analysis.model.right;

import java.util.ArrayList;
import java.util.List;

public class ModifyAction extends ConsequenceAction {

    private final List<ModifiedProperty> modifiedProperties = new ArrayList<>();

    public ModifyAction( Class<?> actionClass ) {
        super( Type.MODIFY, actionClass );
    }

    public List<ModifiedProperty> getModifiedProperties() {
        return modifiedProperties;
    }

    public void addModifiedProperty(ModifiedProperty modifiedProperty) {
        modifiedProperties.add( modifiedProperty );
    }

    @Override
    public String toString() {
        return "ModifyAction{" +
                "actionClass=" + actionClass +
                ", modifiedProperties=" + modifiedProperties +
                '}';
    }
}
