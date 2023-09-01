package org.kie.dmn.model.api;

import java.util.List;

public interface ElementCollection extends NamedElement {

    List<DMNElementReference> getDrgElement();

}
