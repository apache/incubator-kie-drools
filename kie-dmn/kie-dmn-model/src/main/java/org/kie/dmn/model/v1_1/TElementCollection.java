package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.ElementCollection;

public class TElementCollection extends TNamedElement implements ElementCollection {

    private List<DMNElementReference> drgElement;

    @Override
    public List<DMNElementReference> getDrgElement() {
        if ( drgElement == null ) {
            drgElement = new ArrayList<>();
        }
        return this.drgElement;
    }

}
