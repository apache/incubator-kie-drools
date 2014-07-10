package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.repository.internal.MavenServiceLocator;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.kie.scanner.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;

import static org.kie.scanner.embedder.MavenProjectLoader.loadMavenProject;

public class Aether {

    private static final Logger log = LoggerFactory.getLogger(Aether.class);

    private String localRepoDir;
    private final boolean offline;

    public static Aether instance;

    private final RepositorySystem system;
    private RepositorySystemSession session;
    private final Collection<RemoteRepository> repositories;

    private RemoteRepository localRepository;

    private Aether(String localRepoDir, boolean offline) {
        this(loadMavenProject(), localRepoDir, offline);
    }

    Aether(MavenProject mavenProject) {
        this(mavenProject, getAether().localRepoDir, getAether().offline);
    }

    public static synchronized Aether getAether() {
        if (instance == null) {
            Settings settings = MavenSettings.getSettings();
            instance = new Aether(settings.getLocalRepository(), settings.isOffline());
        }
        return instance;
    }

    private Aether(MavenProject mavenProject, String localRepoDir, boolean offline) {
        this.localRepoDir = localRepoDir;
        this.offline = offline;
        system = newRepositorySystem();
        session = newRepositorySystemSession( system );
        repositories = initRepositories(mavenProject);
    }

    private Collection<RemoteRepository> initRepositories(MavenProject mavenProject) {
        Collection<RemoteRepository> reps = new HashSet<RemoteRepository>();
        reps.add(newCentralRepository());
        if (mavenProject != null) {
            reps.addAll(mavenProject.getRemoteProjectRepositories());
        }

        RemoteRepository localRepo = newLocalRepository();
        if (localRepo != null) {
            reps.add(localRepo);
            localRepository = localRepo;
        } else {
            localRepoDir = IoUtils.getTmpDirectory().getAbsolutePath();
        }
        return reps;
    }

    private RepositorySystem newRepositorySystem() {
        MavenServiceLocator locator = new MavenServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class );
        locator.addService( RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class );
        locator.setServices( WagonProvider.class, new ManualWagonProvider() );

        return locator.getService( RepositorySystem.class );
    }

    private DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system ) {
        LocalRepository localRepo = new LocalRepository(localRepoDir);
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );
        session.setOffline(offline);
        return session;
    }

    private RemoteRepository newCentralRepository() {
        return new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
    }

    private RemoteRepository newLocalRepository() {
        File m2RepoDir = new File( localRepoDir );
        if (!m2RepoDir.exists()) {
            return null;
        }
        try {
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository( "local", "default", localRepositoryUrl );
        } catch (MalformedURLException e) { }
        return null;
    }

    public RepositorySystem getSystem() {
        return system;
    }

    public RepositorySystemSession getSession() {
        return session;
    }

    public void renewSession() {
        session = newRepositorySystemSession( system );
    }

    public Collection<RemoteRepository> getRepositories() {
        return repositories;
    }

    public RemoteRepository getLocalRepository() {
        return localRepository;
    }

    private static class ManualWagonProvider implements WagonProvider {

        public Wagon lookup( String roleHint ) throws Exception {
            if ( "http".equals( roleHint ) || "https".equals( roleHint ) ) {
                return new HttpWagon();
            }
            if ( "sramp".equals( roleHint ) ) {
                try {
                    return (Wagon) Class.forName("org.overlord.dtgov.jbpm.util.SrampWagonProxy").newInstance();
                } catch (ClassNotFoundException cnfe) {
                    log.warn("Cannot find sramp wagon implementation class", cnfe);
                }
            }
            return null;
        }

        public void release( Wagon wagon ) { }
    }
}
