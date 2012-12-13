package org.kie.builder;

import java.util.Map;

/**
 * KieModuleModel is a model allowing to programmatically define a KieModule
 * @see KieModule
 */
public interface KieModuleModel {

    /**
     * Creates a new KieBaseModel with the given name and adds it to this KieModuleModel
     * @param name The name of the new KieBaseModel to be created
     * @return The new KieBaseModel
     */
    KieBaseModel newKieBaseModel(String name);

    /**
     * Removes the KieBaseModel with the give name from this KieModuleModel
     * @param name The name of the KieBaseModel to be removed
     */
    void removeKieBaseModel(String name);

    /**
     * Returns all the KieBaseModel defined in this KieModuleModel mapped by their names
     */
    Map<String, KieBaseModel> getKieBaseModels();

    /**
     * Provides an XML representation of this KieModuleModel
     * @return
     */
    String toXML();
}