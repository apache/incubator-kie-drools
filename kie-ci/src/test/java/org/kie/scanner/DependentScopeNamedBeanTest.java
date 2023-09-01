package org.kie.scanner;

import java.io.InputStream;

import org.junit.Test;
import org.kie.maven.integration.MavenPomModelGenerator;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.PomModelGenerator;

import static org.assertj.core.api.Assertions.fail;

public class DependentScopeNamedBeanTest {

    @Test
    public void testPlexusBeanScanning() {
        //This is the shortest way I can find a root to com.google.inject.internal.Scoping#makeInjectable()
        //where the @Dependent scoped bean on the classpath causes guice to fail. The below call goes through
        //the following classes:-
        //
        // - MavenPomModelGenerator#parse()
        // - MavenProjectLoader#parseMavenPom()
        // - MavenEmbedder#constructor
        // - MavenEmbedderUtils#buildPlexusContainer()
        //
        // This builds a PlexusContainer with classpath scanning enabled (to detect classes needing guice injection)
        final InputStream is = getClass().getResourceAsStream( "/kjar/pom-kjar.xml" );
        final PomModelGenerator generator = new MavenPomModelGenerator();

        try {
            final PomModel pom = generator.parse( "pom.xml",
                                                  is );
        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
            fail( "This should not fail" );
        }

    }

}
