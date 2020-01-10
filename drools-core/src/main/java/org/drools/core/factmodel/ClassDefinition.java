package org.drools.core.factmodel;

import java.util.List;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;

public interface ClassDefinition extends FactType {
    void setDefinedClass(final Class< ? > definedClass);

    List<FactField> getFields();

    boolean isTraitable();
}
