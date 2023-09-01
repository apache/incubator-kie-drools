package org.kie.dmn.model.v1_3;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.OrganizationUnit;

public class TOrganizationUnit extends TBusinessContextElement implements OrganizationUnit {

    protected List<DMNElementReference> decisionMade;
    protected List<DMNElementReference> decisionOwned;

    @Override
    public List<DMNElementReference> getDecisionMade() {
        if (decisionMade == null) {
            decisionMade = new ArrayList<>();
        }
        return this.decisionMade;
    }

    @Override
    public List<DMNElementReference> getDecisionOwned() {
        if (decisionOwned == null) {
            decisionOwned = new ArrayList<>();
        }
        return this.decisionOwned;
    }

}
