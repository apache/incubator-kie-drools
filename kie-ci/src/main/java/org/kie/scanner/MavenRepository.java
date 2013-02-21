package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.kie.builder.ReleaseId;
import org.kie.builder.impl.InternalKieModule;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.SubArtifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MavenRepository {

    private static final MavenRepository DEFAUL_MAVEN_REPOSITORY = new MavenRepository(Aether.DEFUALT_AETHER);

    private final Aether aether;
    private static final List<RemoteRepository> extraRepositories = new ArrayList<RemoteRepository>();
    
    private MavenRepository(Aether aether) {
        this.aether = aether;
    }
    
    public static MavenRepository getMavenRepository() {
        return DEFAUL_MAVEN_REPOSITORY;
    }
    
    public static void addExtraRepository(RemoteRepository r){
      extraRepositories.add(r);
    }
    
    public static List<RemoteRepository> getExtraRepositories(){
      return extraRepositories;
    }
    
    public static void clearExtraRepositories(){
      extraRepositories.clear();
    }

    public static MavenRepository getMavenRepository(MavenProject mavenProject) {
        return new MavenRepository(new Aether(mavenProject));
    }

    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency( artifact, "" ) );
        for (RemoteRepository repo : aether.getRepositories()) {
            collectRequest.addRepository(repo);
        }
        for (RemoteRepository repo : extraRepositories) {
            collectRequest.addRepository(repo);
        }
        CollectResult collectResult;
        try {
            collectResult = aether.getSystem().collectDependencies(aether.getSession(), collectRequest);
        } catch (DependencyCollectionException e) {
            throw new RuntimeException(e);
        }
        CollectDependencyVisitor visitor = new CollectDependencyVisitor();
        collectResult.getRoot().accept( visitor );

        List<DependencyDescriptor> descriptors = new ArrayList<DependencyDescriptor>();
        for (DependencyNode node : visitor.getDependencies()) {
            descriptors.add(new DependencyDescriptor(node.getDependency().getArtifact()));
        }
        return descriptors;
    }

    public Artifact resolveArtifact(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        for (RemoteRepository repo : aether.getRepositories()) {
            artifactRequest.addRepository(repo);
        }
        for (RemoteRepository repo : extraRepositories) {
            artifactRequest.addRepository(repo);
        }
        ArtifactResult artifactResult;
        try {
            artifactResult = aether.getSystem().resolveArtifact(aether.getSession(), artifactRequest);
        } catch (ArtifactResolutionException e) {
            return null;
        }

        return artifactResult.getArtifact();
    }
    
  

    public void deployArtifact(ReleaseId releaseId, InternalKieModule kieModule, File pomfile) {
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ), toFileName(releaseId, null) + ".jar");
        try {
            FileOutputStream fos = new FileOutputStream(jarFile);
            fos.write(kieModule.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deployArtifact(releaseId, jarFile, pomfile);
    }

    public void deployArtifact(ReleaseId releaseId, File jar, File pomfile) {
        Artifact jarArtifact = new DefaultArtifact( releaseId.getGroupId(), releaseId.getArtifactId(), "jar", releaseId.getVersion() );
        jarArtifact = jarArtifact.setFile( jar );

        Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
        pomArtifact = pomArtifact.setFile( pomfile );

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact( jarArtifact )
                .addArtifact( pomArtifact )
                .setRepository(aether.getLocalRepository());

        try {
            aether.getSystem().deploy(aether.getSession(), deployRequest);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    public void deployPomArtifact(String groupId, String artifactId, String version, File pomfile) {
        Artifact pomArtifact = new DefaultArtifact( groupId, artifactId, "pom", version );
        pomArtifact = pomArtifact.setFile( pomfile );

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact(pomArtifact)
                .setRepository(aether.getLocalRepository());

        try {
            aether.getSystem().deploy(aether.getSession(), deployRequest);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CollectDependencyVisitor implements DependencyVisitor {

        private final List<DependencyNode> dependencies = new ArrayList<DependencyNode>();

        public boolean visitEnter(DependencyNode node) {
            dependencies.add(node);
            return true;
        }

        public boolean visitLeave(DependencyNode node) {
            return true;
        }

        public List<DependencyNode> getDependencies() {
            return dependencies;
        }
    }
    
    public static String toFileName(ReleaseId releaseId, String classifier) {
        if (classifier != null) {
            return releaseId.getArtifactId() + "-" + releaseId.getVersion() + "-" + classifier;
        }

        return releaseId.getArtifactId() + "-" + releaseId.getVersion();
    }
}
