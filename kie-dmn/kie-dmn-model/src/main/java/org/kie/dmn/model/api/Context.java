package org.kie.dmn.model.api;

import java.util.List;

public interface Context extends Expression {

    List<ContextEntry> getContextEntry();

}
