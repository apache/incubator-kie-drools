package org.kie.dmn.model.v1x;

import java.util.List;

public interface OrganizationUnit extends BusinessContextElement {

    List<DMNElementReference> getDecisionMade();

    List<DMNElementReference> getDecisionOwned();

}
