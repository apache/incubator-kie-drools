package org.kie.api.builder.helper;

/**
 * This class provides users with the ability to programmatically create
 * kjars and deploy them to the available maven repositories. 
 * </p>
 * Both a fluent and "single-method" interface are provided.
 */
public class KieModuleDeploymentHelper {

    public static final FluentKieModuleDeploymentHelper newFluentInstance() { 
        return new KieModuleDeploymentHelperImpl();
    }
    
    public static final SingleKieModuleDeploymentHelper newSingleInstance() { 
        return new KieModuleDeploymentHelperImpl();
    }
}
