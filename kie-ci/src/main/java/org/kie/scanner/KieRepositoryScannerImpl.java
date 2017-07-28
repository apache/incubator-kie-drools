/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import java.io.InputStream;
import org.appformer.maven.integration.ArtifactResolver;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.PomModel;
import org.drools.core.impl.InternalKieContainer;
import org.kie.scanner.adapters.InternalArtifactResolver;
import org.kie.scanner.adapters.MavenArtifactResolver;

public class KieRepositoryScannerImpl extends AbstractKieRepositoryScanner {

  @Override
  protected InternalArtifactResolver getArtifactResolver() {
    if (artifactResolver == null) {
      artifactResolver = new MavenArtifactResolver(new ArtifactResolver());
    }
    return artifactResolver;
  }

  protected InternalArtifactResolver getResolverFor (AFReleaseId releaseId, boolean allowDefaultPom) {
    return new MavenArtifactResolver (ArtifactResolver.getResolverFor(releaseId, allowDefaultPom));
  }

  protected InternalArtifactResolver getResolverFor( InternalKieContainer kieContainer, boolean allowDefaultPom ) {
    return new MavenArtifactResolver(ArtifactResolver.getResolverFor( kieContainer.getPomAsStream(), kieContainer.getReleaseId(), allowDefaultPom ));
  }

  @Override
  protected InternalArtifactResolver getResolverFor(InputStream pomXml) {
    return new MavenArtifactResolver(ArtifactResolver.getResolverFor(pomXml));
  }

  @Override
  protected InternalArtifactResolver getResolverFor(PomModel pomModel) {
    return new MavenArtifactResolver(ArtifactResolver.getResolverFor(pomModel));
  }
}
