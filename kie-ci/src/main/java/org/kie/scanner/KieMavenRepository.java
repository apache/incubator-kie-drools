/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.scanner;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.repository.RemoteRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.maven.integration.Aether;
import org.kie.maven.integration.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieMavenRepository extends MavenRepository {

    private static final Logger log = LoggerFactory.getLogger( KieMavenRepository.class );

    public static KieMavenRepository defaultMavenRepository;

    protected KieMavenRepository( Aether aether ) {
        super( aether );
    }

    public static synchronized KieMavenRepository getKieMavenRepository() {
        if ( defaultMavenRepository == null ) {
            Aether defaultAether = Aether.getAether();
            defaultMavenRepository = new KieMavenRepository( defaultAether );
        }
        return defaultMavenRepository;
    }

    public static KieMavenRepository getKieMavenRepository( MavenProject mavenProject ) {
        return new KieMavenRepository( new Aether( mavenProject ) );
    }

    /**
     * Deploys the kjar in the given kieModule on the remote repository defined in the
     * distributionManagement tag of the provided pom file. If the pom file doesn't define
     * a distributionManagement no deployment will be performed and a warning message will be logged.
     *
     * @param releaseId The releaseId with which the deployment will be made
     * @param kieModule The kieModule containing the kjar to be deployed
     * @param pomfile The pom file to be deployed together with the kjar
     */
    public void deployArtifact( ReleaseId releaseId,
                                InternalKieModule kieModule,
                                File pomfile ) {
        RemoteRepository repository = getRemoteRepositoryFromDistributionManagement( pomfile );
        if (repository == null) {
            log.warn( "No Distribution Management configured: unknown repository" );
            return;
        }
        deployArtifact( repository, releaseId, kieModule, pomfile );
    }

    /**
     * Deploys the kjar in the given kieModule on a remote repository.
     *
     * @param repository The remote repository where the kjar will be deployed
     * @param releaseId The releaseId with which the deployment will be made
     * @param kieModule The kieModule containing the kjar to be deployed
     * @param pomfile The pom file to be deployed together with the kjar
     */
    public void deployArtifact( RemoteRepository repository,
                                ReleaseId releaseId,
                                InternalKieModule kieModule,
                                File pomfile ) {
        File jarFile = bytesToFile( releaseId, kieModule.getBytes(), ".jar" );
        deployArtifact( repository, releaseId, jarFile, pomfile );
    }


    /**
     * Installs the kjar in the given kieModule into the local repository.
     *
     * @param releaseId The releaseId with which the kjar will be installed
     * @param kieModule The kieModule containing the kjar to be installed
     * @param pomfile The pom file to be installed together with the kjar
     */
    public void installArtifact( ReleaseId releaseId,
                                 InternalKieModule kieModule,
                                 File pomfile ) {
        File jarFile = bytesToFile( releaseId, kieModule.getBytes(), ".jar" );
        installArtifact( releaseId, jarFile, pomfile );
    }
}
