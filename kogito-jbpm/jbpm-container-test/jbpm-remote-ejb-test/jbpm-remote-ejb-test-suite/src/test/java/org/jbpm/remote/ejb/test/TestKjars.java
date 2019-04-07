/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test;

import org.jbpm.remote.ejb.test.maven.MavenProject;

public class TestKjars {

    private static final String KJAR_VERSION = System.getProperty("project.version");

    public static final MavenProject INTEGRATION = new MavenProject("org.jbpm:test-kjar-integration:" + KJAR_VERSION);
    public static final MavenProject BPMN_BUILD_TEST = new MavenProject("org.jbpm:test-kjar-bpmn-build:" + KJAR_VERSION);
    public static final MavenProject EVALUATION = new MavenProject("org.jbpm:test-kjar-evaluation:" + KJAR_VERSION);
}
