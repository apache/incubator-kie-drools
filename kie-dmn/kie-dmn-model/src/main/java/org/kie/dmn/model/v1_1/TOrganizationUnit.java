package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.OrganizationUnit;

public class TOrganizationUnit extends TBusinessContextElement implements OrganizationUnit {

    private List<DMNElementReference> decisionMade;
    private List<DMNElementReference> decisionOwned;

    @Override
    public List<DMNElementReference> getDecisionMade() {
        if ( decisionMade == null ) {
            decisionMade = new ArrayList<>();
        }
        return this.decisionMade;
    }

    @Override
    public List<DMNElementReference> getDecisionOwned() {
        if ( decisionOwned == null ) {
            decisionOwned = new ArrayList<>();
        }
        return this.decisionOwned;
    }

}
