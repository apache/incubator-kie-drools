package org.kie.api.builder.helper;

import java.util.List;

import org.kie.api.builder.KieModule;

public interface SingleKieModuleDeploymentHelper {

    /**
     * General API
     */

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths);

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar);

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar, 
            List<String> dependencies);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName, 
            List<String> resourceFilePaths, List<Class<?>> classesForKjar, 
            List<String> dependencies);

}