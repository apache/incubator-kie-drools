package org.kie.api.builder.helper;

import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;

/**
 * A fluent interface to the {@link KieModuleDeploymentHelper} functionality. See
 * the {@link KieModuleDeploymentHelper} for more info.
 */
public abstract class FluentKieModuleDeploymentHelper extends KieModuleDeploymentHelper {

    /**
     * Fluent API
     */

    /**
     * Set the group id of the Kjar
     * @param groupId The group id
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setGroupId(String groupId);

    /**
     * Set the artifact id of the Kjar
     * @param artifactId The artifact id
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setArtifactId(String artifactId);

    /**
     * Set the (pom) version of the Kjar
     * @param version The version
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setVersion(String version);

    /**
     * Set a {@link KieBase} name. </p> If you want to add multiple {@link KieBase}'s, use
     * the {@link FluentKieModuleDeploymentHelper#getKieModuleModel()} method. 
     * @param kbaseName The {@link KieBase} name
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setKBaseName(String kbaseName);
   
    /**
     * Set the {@link KieSession} name. </p> If you want to add multiple {@link KieSession}'s, use
     * the {@link FluentKieModuleDeploymentHelper#getKieModuleModel()} method. 
     * @param ksessionName The {@link KieSession} name
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setKieSessionname(String ksessionName);

    /**
     * Set the list of paths containing resources. If the path refers to a directory, 
     * all files in that directory will be added as resource files. 
     * @param resourceFilePaths The list of resource file paths
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setResourceFilePaths(List<String> resourceFilePaths);

    /**
     * Add a path containing one or more resources. If the path is a directory,
     * all files in the directory will be added as resource files. 
     * @param resourceFilePath The resource file path
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper addResourceFilePath(String... resourceFilePath);

    /**
     * Set the list of classes to be added to the Kjar.
     * @param classesForKjar The list of classes
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setClasses(List<Class<?>> classesForKjar);

    /**
     * Add a class that should be included in the Kjar.
     * @param classForKjar The class
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper addClass(Class<?>... classForKjar);
   
    /**
     * Set the list of dependencies that the Kjar should use. 
     * @param dependencies The list of dependencies
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper setDependencies(List<String> dependencies);

    /**
     * Add one or more dependencies (specified by a "G:A:V" string) that the Kjar should use. 
     * @param dependency One or more strings specifying a dependency
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper addDependencies(String... dependency);
   
    /**
     * Get the {@link KieModuleModel}. Use the {@link KieModuleModel} instance to add
     * more {@link KieBase} or {@link KieSession} instances as well as add or change the 
     * default configuration of the {@link KieSession}'s.
     * @return The {@link KieModuleModel} instance
     */
    public abstract KieModuleModel getKieModuleModel();

    /**
     * Reset the helper. This clears <i>ALL</i> configuration that has been done up to this point
     * on the helper instance.
     * @return The helper instance
     */
    public abstract FluentKieModuleDeploymentHelper resetHelper();
   
    /**
     * Create the Kjar
     * @return The {@link KieModule} that represents the Kjar
     */
    public abstract KieModule createKieJar();
   
    /**
     * Create the Kjar and deploy (install) it to the local maven repository.
     */
    public abstract void createKieJarAndDeployToMaven();
    
}    
