package org.drools.scanner;

import org.kie.builder.KieJar;
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

class MavenRepository {

    private final Aether aether;

    public MavenRepository() {
        this.aether = Aether.INSTANCE;
    }

    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency( artifact, "" ) );
        for (RemoteRepository repo : aether.getRepositories()) {
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

        ArtifactResult artifactResult;
        try {
            artifactResult = aether.getSystem().resolveArtifact(aether.getSession(), artifactRequest);
        } catch (ArtifactResolutionException e) {
            throw new RuntimeException(e);
        }

        return artifactResult.getArtifact();
    }

    public void deployArtifact(String groupId, String artifactId, String version, KieJar kieJar, File pomfile) {
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ), groupId + ":" + artifactId + ":" + version + ".jar");
        try {
            FileOutputStream fos = new FileOutputStream(jarFile);
            fos.write(kieJar.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deployArtifact(groupId, artifactId, version, jarFile, pomfile);
    }

    public void deployArtifact(String groupId, String artifactId, String version, File jar, File pomfile) {
        Artifact jarArtifact = new DefaultArtifact( groupId, artifactId, "", "jar", version );
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
}
