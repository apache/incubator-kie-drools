package org.kie.dmn.model.api;

import java.util.List;

public interface OrganizationUnit extends BusinessContextElement {

    List<DMNElementReference> getDecisionMade();

    List<DMNElementReference> getDecisionOwned();

}
