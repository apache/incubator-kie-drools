package org.drools.base.facttemplates;

import java.io.Externalizable;
import java.util.Collection;

import org.drools.base.definitions.InternalKnowledgePackage;


public interface FactTemplate
    extends
    Externalizable {

    InternalKnowledgePackage getPackage();

    /**
     * The name of the template may be the fully qualified
     * class name, or an alias.
     * @return
     */
    String getName();

    /**
     * templates may have 1 or more slots. A slot is a named
     * pattern with a specific type of value.
     * @return
     */
    int getNumberOfFields();

    Collection<String> getFieldNames();

    /**
     * Return the slot with the String name
     * @return
     */
    FieldTemplate getFieldTemplate(String name);

    /**
     * Get the pattern index with the given name
     * @param name
     * @return
     */
    int getFieldTemplateIndex(String name);

    Fact createFact();
}
