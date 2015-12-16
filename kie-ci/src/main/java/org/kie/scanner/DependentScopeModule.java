/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.scanner;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * A handler for @Dependent scoped beans used by sisu-guice.
 * <p/>
 * The DefaultPlexusContainer created by MavenEmbedderUtils for KIE's programmatic use of maven-core
 * and Aether tries to wire-up all beans on the classpath. The KIE Workbenches contain @Dependent classes
 * defining client-side Uberfire Perspective definitions. These are filtered from the webapp WARs as they are
 * client-side only and handled by Errai's CDI implementation; however when ran from within an IDE
 * the classes exist on the classpath and sisu-guice fails to bind @Dependent scope.
 * <p/>
 * This class is a work-around for running the KIE Workbenches in GWT Hosted Mode.
 * <p/>
 * See https://github.com/sonatype/sisu-guice/issues/10
 */
@Named
public class DependentScopeModule
        extends AbstractModule {

    @Override
    protected void configure() {
        bindScope( Dependent.class,
                   Scopes.NO_SCOPE );
    }
}