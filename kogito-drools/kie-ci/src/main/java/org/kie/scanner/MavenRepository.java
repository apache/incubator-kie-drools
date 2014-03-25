package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.scanner.embedder.MavenSettings;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResolutionException;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.SubArtifact;
import org.sonatype.aether.version.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.kie.scanner.DependencyDescriptor.isRangedVersion;

public class MavenRepository {

    private static MavenRepository defaultMavenRepository;

    private final Aether aether;
    private static final Collection<RemoteRepository> extraRepositories = new HashSet<RemoteRepository>();
    
    private MavenRepository(Aether aether) {
        this.aether = aether;
    }

    public static synchronized MavenRepository getMavenRepository() {
        if (defaultMavenRepository == null) {
            Aether defaultAether = Aether.getAether();
            defaultMavenRepository = new MavenRepository(defaultAether);
            initSettings();
        }
        return defaultMavenRepository;
    }

    private static void initSettings() {
        Settings settings = MavenSettings.getSettings();
        for (Profile profile : settings.getProfiles()) {
            if (isProfileActive(settings, profile)) {
                for (Repository repository : profile.getRepositories()) {
                    addExtraRepository( toRemoteRepository(settings, repository) );
                }
                for (Repository repository : profile.getPluginRepositories()) {
                    addExtraRepository( toRemoteRepository(settings, repository) );
                }
            }
        }
    }

    private static boolean isProfileActive(Settings settings, Profile profile) {
        return settings.getActiveProfiles().contains(profile.getId()) ||
               (profile.getActivation() != null && profile.getActivation().isActiveByDefault());
    }

    private static RemoteRepository toRemoteRepository(Settings settings, Repository repository) {
        RemoteRepository remote = new RemoteRepository( repository.getId(), repository.getLayout(), repository.getUrl() );
        setPolicy(remote, repository.getSnapshots(), true);
        setPolicy(remote, repository.getReleases(), false);
        Server server = settings.getServer( repository.getId() );
        if (server != null) {
            remote.setAuthentication( new Authentication(server.getUsername(), server.getPassword()) );
        }
        return remote;
    }

    private static void setPolicy(RemoteRepository remote, RepositoryPolicy policy, boolean snapshot) {
        if (policy != null) {
            remote.setPolicy(snapshot,
                             new org.sonatype.aether.repository.RepositoryPolicy(policy.isEnabled(),
                                                                                 policy.getUpdatePolicy(),
                                                                                 policy.getChecksumPolicy()));
        }
    }

    public static void addExtraRepository(RemoteRepository r) {
        extraRepositories.add(r);
    }

    public static Collection<RemoteRepository> getExtraRepositories() {
        return extraRepositories;
    }
    
    public static void clearExtraRepositories() {
        extraRepositories.clear();
    }

    private Collection<RemoteRepository> getRemoteRepositoryForRequest() {
        Collection<RemoteRepository> remoteRepos = new HashSet<RemoteRepository>();
        remoteRepos.addAll(extraRepositories);
        remoteRepos.addAll(aether.getRepositories());
        return remoteRepos;
    }

    public static MavenRepository getMavenRepository(MavenProject mavenProject) {
        return new MavenRepository(new Aether(mavenProject));
    }

    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        CollectRequest collectRequest = new CollectRequest();
        Dependency root = new Dependency( artifact, "" );
        collectRequest.setRoot( root );
        for (RemoteRepository repo : getRemoteRepositoryForRequest()) {
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
            // skip root to not add artifact as dependency
            if (node.getDependency().equals(root)) {
                continue;
            }
            descriptors.add(new DependencyDescriptor(node.getDependency().getArtifact()));
        }
        return descriptors;
    }

    public Artifact resolveArtifact(ReleaseId releaseId) {
        String artifactName = releaseId.toString();
        if (isRangedVersion(releaseId.getVersion())) {
            Version v = resolveVersion(artifactName);
            if (v == null) {
                return null;
            }
            artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":" + v;
        }
        return resolveArtifact(artifactName);
    }

    public Artifact resolveArtifact(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        for (RemoteRepository repo : getRemoteRepositoryForRequest()) {
            artifactRequest.addRepository(repo);
        }
        try {
            ArtifactResult artifactResult = aether.getSystem().resolveArtifact(aether.getSession(), artifactRequest);
            return artifactResult.getArtifact();
        } catch (ArtifactResolutionException e) {
            return null;
        }
    }

    public Version resolveVersion(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        VersionRangeRequest versionRequest = new VersionRangeRequest();
        versionRequest.setArtifact(artifact);
        for (RemoteRepository repo : getRemoteRepositoryForRequest()) {
            versionRequest.addRepository(repo);
        }
        VersionRangeResult artifactResult;
        try {
            VersionRangeResult versionRangeResult = aether.getSystem().resolveVersionRange(aether.getSession(), versionRequest);
            return versionRangeResult.getHighestVersion();
        } catch (VersionRangeResolutionException e) {
            return null;
        }
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

    public void deployArtifact(ReleaseId releaseId, byte[] jarContent, byte[] pomContent ) {
        File jarFile = new File( System.getProperty( "java.io.tmpdir" ), toFileName(releaseId, null) + ".jar");
        try {
            FileOutputStream fos = new FileOutputStream(jarFile);
            fos.write(jarContent);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomContent);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deployArtifact(releaseId, jarFile, pomFile);
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

    public void renewSession() {
        aether.renewSession();
    }
}
