/*
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
package org.kie.kogito.maven.plugin.testing.stubs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.XmlStreamReader;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

public class GenerateModelMojoProjectStub extends MavenProject {

    public GenerateModelMojoProjectStub() {
        try {

            MavenXpp3Reader pomReader = new MavenXpp3Reader();
            File pomFile = new File(getBasedir(), "pom.xml");
            Model model = pomReader.read(XmlStreamReader.builder().setFile(pomFile).get());
            setModel(model);
            setFile(pomFile);
            setGroupId(model.getGroupId());
            setArtifactId(model.getArtifactId());
            setVersion(model.getVersion());
            setName(model.getName());
            setUrl(model.getUrl());
            setPackaging(model.getPackaging());

            Build build = new Build();
            build.setFinalName(model.getArtifactId());
            build.setSourceDirectory(getBasedir() + "/src/main/java");
            build.setTestSourceDirectory(getBasedir() + "/src/test/java");
            build.setDirectory(getTargetdir().toString());

            build.setTestOutputDirectory(getTargetdir() + "/test-classes");
            build.setOutputDirectory(getTargetdir() + "/classes");

            Resource resource = new Resource();
            resource.setDirectory(getBasedir() + "/src/main/resources");
            addResource(resource);

            setBuild(build);

            setCompileSourceRoots(new ArrayList<>(List.of(getBasedir() + "/src/main/java")));
            setTestCompileSourceRoots(new ArrayList<>(List.of(getBasedir() + "/src/test/java")));

            getTargetdir().mkdirs();
            new File(build.getOutputDirectory()).mkdirs();
            new File(build.getTestOutputDirectory()).mkdirs();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getBasedir() {
        return new File("src/test/resources/unit/generate-model/").getAbsoluteFile();
    }

    public File getTargetdir() {
        return new File("target/unit/generate-model/target").getAbsoluteFile();
    }
}