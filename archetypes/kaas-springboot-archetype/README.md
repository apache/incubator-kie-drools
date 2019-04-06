# Generate KaaS project based on Quarkus runtime

To generate new project for KaaS (Kjar as a Service) based on Quarkus use following command

    ```
     mvn archetype:generate 
    -DarchetypeGroupId=org.kie
    -DarchetypeArtifactId=kaas-springboot-archetype 
    -DarchetypeVersion=8.0.0-SNAPSHOT 
    -DgroupId=com.company 
    -DartifactId=sample-kaas     
    ```
    
 or cut/paste this one-liner:
 
    ```
      mvn archetype:generate -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kaas-springboot-archetype -DarchetypeVersion=8.0.0-SNAPSHOT -DgroupId=com.company -DartifactId=sample-kaas     
    ```

