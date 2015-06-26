package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.scanner.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.eclipse.aether.version.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.kie.scanner.DependencyDescriptor.isRangedVersion;

public class MavenRepository {

    private static final Logger log = LoggerFactory.getLogger(MavenRepository.class);

    private static MavenRepository defaultMavenRepository;

    private final Aether aether;
    private final Collection<RemoteRepository> extraRepositories;
    private final Collection<RemoteRepository> remoteRepositoriesForRequest;

    protected MavenRepository(Aether aether) {
        this.aether = aether;
        Settings settings = getSettings();
        extraRepositories = initExtraRepositories(settings);
        remoteRepositoriesForRequest = initRemoteRepositoriesForRequest(settings);
    }

    protected Settings getSettings() {
        return MavenSettings.getSettings();
    }

    Collection<RemoteRepository> getRemoteRepositoriesForRequest() {
        return remoteRepositoriesForRequest;
    }

    public static synchronized MavenRepository getMavenRepository() {
        if (defaultMavenRepository == null) {
            Aether defaultAether = Aether.getAether();
            defaultMavenRepository = new MavenRepository(defaultAether);
        }
        return defaultMavenRepository;
    }

    private Collection<RemoteRepository> initExtraRepositories(Settings settings) {
        Collection<RemoteRepository> extraRepositories = new HashSet<RemoteRepository>();
        for (Profile profile : settings.getProfiles()) {
            if (isProfileActive(settings, profile)) {
                for (Repository repository : profile.getRepositories()) {
                    extraRepositories.add( toRemoteRepositoryBuilder(settings, repository).build() );
                }
                for (Repository repository : profile.getPluginRepositories()) {
                    extraRepositories.add( toRemoteRepositoryBuilder(settings, repository).build() );
                }
            }
        }
        return extraRepositories;
    }

    private Collection<RemoteRepository> initRemoteRepositoriesForRequest(Settings settings) {
        Collection<RemoteRepository> remoteRepos = new HashSet<RemoteRepository>();
        for (RemoteRepository repo : extraRepositories) {
            remoteRepos.add( resolveMirroredRepo(settings, repo) );
        }
        for (RemoteRepository repo : aether.getRepositories()) {
            remoteRepos.add( resolveMirroredRepo(settings, repo) );
        }
        return remoteRepos;
    }

    private RemoteRepository resolveMirroredRepo(Settings settings, RemoteRepository repo) {
        for (Mirror mirror : settings.getMirrors()) {
            if (isMirror(repo, mirror.getMirrorOf())) {
                return toRemoteRepositoryBuilder(settings, mirror.getId(), mirror.getLayout(), mirror.getUrl()).build();
            }
        }
        return repo;
    }

    private boolean isMirror(RemoteRepository repo, String mirrorOf)  {
        return mirrorOf.equals("*") ||
               ( mirrorOf.equals("external:*") && !repo.getUrl().startsWith("file:") ) ||
               ( mirrorOf.contains("external:*") && !repo.getUrl().startsWith("file:") && !mirrorOf.contains("!" + repo.getId()) ) ||
               ( mirrorOf.startsWith("*") && !mirrorOf.contains("!" + repo.getId()) ) ||
               ( !mirrorOf.startsWith("*") && !mirrorOf.contains("external:*") && mirrorOf.contains(repo.getId()) );
    }

    private boolean isProfileActive(Settings settings, Profile profile) {
        return settings.getActiveProfiles().contains(profile.getId()) ||
               (profile.getActivation() != null && profile.getActivation().isActiveByDefault());
    }

    private static RemoteRepository.Builder toRemoteRepositoryBuilder(Settings settings, Repository repository) {
        RemoteRepository.Builder remoteBuilder = toRemoteRepositoryBuilder( settings, repository.getId(), repository.getLayout(), repository.getUrl() );
        setPolicy(remoteBuilder, repository.getSnapshots(), true);
        setPolicy(remoteBuilder, repository.getReleases(), false);
        return remoteBuilder;
    }

    private static RemoteRepository.Builder toRemoteRepositoryBuilder(Settings settings, String id, String layout, String url) {
        RemoteRepository.Builder remoteBuilder = new RemoteRepository.Builder( id, layout, url );
        Server server = settings.getServer( id );
        if (server != null) {
            remoteBuilder.setAuthentication( new AuthenticationBuilder().addUsername(server.getUsername())
                                                                        .addPassword(server.getPassword())
                                                                        .build() );
        }
        return remoteBuilder;

    }

    private static void setPolicy(RemoteRepository.Builder builder, RepositoryPolicy policy, boolean snapshot) {
        if (policy != null) {
            org.eclipse.aether.repository.RepositoryPolicy repoPolicy =
                    new org.eclipse.aether.repository.RepositoryPolicy(policy.isEnabled(),
                                                                       policy.getUpdatePolicy(),
                                                                       policy.getChecksumPolicy());
            if (snapshot) {
                builder.setSnapshotPolicy(repoPolicy);
            } else {
                builder.setReleasePolicy(repoPolicy);
            }
        }
    }

    public static MavenRepository getMavenRepository(MavenProject mavenProject) {
        return new MavenRepository(new Aether(mavenProject));
    }

    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        CollectRequest collectRequest = new CollectRequest();
        Dependency root = new Dependency( artifact, "" );
        collectRequest.setRoot( root );
        for (RemoteRepository repo : remoteRepositoriesForRequest) {
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
        return resolveArtifact(artifactName, true);
    }

    public Artifact resolveArtifact(String artifactName, boolean logUnresolvedArtifact) {
        Artifact artifact = new DefaultArtifact( artifactName );
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        for (RemoteRepository repo : remoteRepositoriesForRequest) {
            artifactRequest.addRepository(repo);
        }
        try {
            ArtifactResult artifactResult = aether.getSystem().resolveArtifact(aether.getSession(), artifactRequest);
            return artifactResult.getArtifact();
        } catch (ArtifactResolutionException e) {
            if (logUnresolvedArtifact) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to resolve artifact: " + artifactName, e);
                } else {
                    log.warn("Unable to resolve artifact: " + artifactName);
                }
            }
            return null;
        }
    }

    public Version resolveVersion(String artifactName) {
        Artifact artifact = new DefaultArtifact( artifactName );
        VersionRangeRequest versionRequest = new VersionRangeRequest();
        versionRequest.setArtifact(artifact);
        for (RemoteRepository repo : remoteRepositoriesForRequest) {
            versionRequest.addRepository(repo);
        }
        try {
            VersionRangeResult versionRangeResult = aether.getSystem().resolveVersionRange(aether.getSession(), versionRequest);
            return versionRangeResult.getHighestVersion();
        } catch (VersionRangeResolutionException e) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to resolve version range for artifact: " + artifactName, e);
            } else {
                log.warn("Unable to resolve version range for artifact: " + artifactName);
            }
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
