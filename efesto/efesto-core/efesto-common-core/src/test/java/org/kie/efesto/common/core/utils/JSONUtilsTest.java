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
package org.kie.efesto.common.core.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.core.identifiers.componentroots.ComponentFoo;
import org.kie.efesto.common.core.identifiers.componentroots.ComponentRootB;
import org.kie.efesto.common.core.identifiers.componentroots.LocalComponentIdFoo;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

class JSONUtilsTest {

    @Test
    void getGeneratedResourceString() throws JsonProcessingException {
        String fullClassName = "full.class.Name";
        GeneratedResource generatedResource = new GeneratedClassResource(fullClassName);
        String expected = String.format("{\"step-type\":\"class\",\"fullClassName\":\"%s\"}", fullClassName);
        String retrieved = JSONUtils.getGeneratedResourceString(generatedResource);
        assertThat(retrieved).isEqualTo(expected);

        LocalUri modelLocalUriId = new ReflectiveAppRoot("test")
                .get(ComponentFoo.class)
                .get("this", "is", "modelLocalUriId")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);

        String target = LocalComponentIdFoo.PREFIX;
        generatedResource = new GeneratedRedirectResource(localUriId, target);
        expected = String.format("{\"step-type\":\"redirect\",\"modelLocalUriId\":%s,\"target\":\"%s\"}",
                                 JSONUtils.getModelLocalUriIdString(localUriId), target);
        retrieved = JSONUtils.getGeneratedResourceString(generatedResource);
        assertThat(retrieved).isEqualTo(expected);

        generatedResource = new GeneratedExecutableResource(localUriId, Collections.singletonList(fullClassName));
        expected = String.format("{\"step-type\":\"executable\",\"modelLocalUriId\":%s,\"fullClassNames\":[\"%s\"]}",
                                 JSONUtils.getModelLocalUriIdString(localUriId), fullClassName);
        retrieved = JSONUtils.getGeneratedResourceString(generatedResource);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getGeneratedResourceObject() throws JsonProcessingException {
        String generatedResourceString = "{\"step-type\":\"redirect\",\"modelLocalUriId\":{\"model\":\"this\"," +
                "\"basePath\":\"/is/fri_foo\",\"fullPath\":\"/this/is/fri_foo\"},\"target\":\"foo\"}";
        GeneratedResource retrieved = JSONUtils.getGeneratedResourceObject(generatedResourceString);
        assertThat(retrieved).isNotNull().isInstanceOf(GeneratedRedirectResource.class);

        generatedResourceString = "{\"step-type\":\"class\",\"fullClassName\":\"full.class.Name\"}\"";
        retrieved = JSONUtils.getGeneratedResourceObject(generatedResourceString);
        assertThat(retrieved).isNotNull().isInstanceOf(GeneratedClassResource.class);

        generatedResourceString = "{\"step-type\":\"executable\"," +
                "\"modelLocalUriId\":{\"model\":\"this\",\"basePath\":\"/this/is/fri_foo\",\"fullPath\":\"/this/is/fri_foo\"},\"fullClassNames\":[\"full.class.Name\"]}";
        retrieved = JSONUtils.getGeneratedResourceObject(generatedResourceString);
        assertThat(retrieved).isNotNull().isInstanceOf(GeneratedExecutableResource.class);
    }

    @Test
    void getGeneratedResourcesString() throws JsonProcessingException {
        String fullClassName = "full.class.Name";
        GeneratedResource generatedIntermediateResource = new GeneratedClassResource(fullClassName);
        String model = "foo";
        LocalUri modelLocalUriId = new ReflectiveAppRoot(model)
                .get(ComponentRootB.class)
                .get("this", "is", "modelLocalUriId")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);
        GeneratedResource generatedFinalResource = new GeneratedExecutableResource(localUriId,
                                                                                   Collections.singletonList(fullClassName));
        GeneratedResources generatedResources = new GeneratedResources();
        generatedResources.add(generatedIntermediateResource);
        generatedResources.add(generatedFinalResource);
        String retrieved = JSONUtils.getGeneratedResourcesString(generatedResources);
        String expected1 = String.format("{\"step-type\":\"class\",\"fullClassName\":\"%s\"}", fullClassName);
        String expected2 = String.format("{\"step-type\":\"executable\",\"modelLocalUriId\":%s,\"fullClassNames\":[\"%s\"]}",
                                         JSONUtils.getModelLocalUriIdString(localUriId), fullClassName);
        assertThat(retrieved).contains(expected1);
        assertThat(retrieved).contains(expected2);
    }

    @Test
    void getGeneratedResourcesObjectFromString() throws JsonProcessingException {
        String generatedResourcesString = "[{\"step-type\":\"executable\"," +
                "\"modelLocalUriId\":{\"model\":\"foo\",\"basePath\":\"/this/is/fri\",\"fullPath\":\"/foo/this/is/fri\"}}," +
                "{\"step-type\":\"class\",\"fullClassName\":\"full.class.Name\"}]";
        GeneratedResources retrieved = JSONUtils.getGeneratedResourcesObject(generatedResourcesString);
        assertThat(retrieved).isNotNull();
        String fullClassName = "full.class.Name";
        GeneratedResource expected1 = new GeneratedClassResource(fullClassName);
        LocalUri modelLocalUriId = new ReflectiveAppRoot("test")
                .get(ComponentFoo.class)
                .get("this", "is", "fri")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);
        GeneratedResource expected2 = new GeneratedExecutableResource(localUriId, Collections.singletonList(fullClassName));
        assertThat(retrieved).contains(expected1);
        assertThat(retrieved).contains(expected2);
    }

    @Test
    void getGeneratedResourcesObjectFromFile() throws Exception {
        String fileName = "IndexFile.test_json";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
        assert resource != null;
        IndexFile indexFile = new IndexFile(new File(resource.toURI()));
        GeneratedResources retrieved = JSONUtils.getGeneratedResourcesObject(indexFile);
        assertThat(retrieved).isNotNull();
        String fullClassName = "full.class.Name";
        GeneratedResource expected1 = new GeneratedClassResource(fullClassName);
        LocalUri modelLocalUriId = new ReflectiveAppRoot("test")
                .get(ComponentFoo.class)
                .get("this", "is", "fri")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);
        GeneratedResource expected2 = new GeneratedExecutableResource(localUriId, Collections.singletonList(fullClassName));
        assertThat(retrieved).contains(expected1);
        assertThat(retrieved).contains(expected2);
    }

    @Test
    void getGeneratedResourcesObjectFromJar() throws Exception {
        ClassLoader originalClassLoader = addJarToClassLoader();
        Optional<File> optionalIndexFile = getFileFromFileName("IndexFile.testb_json");
        assertThat(optionalIndexFile).isNotNull().isPresent();
        assertThat(optionalIndexFile).get().isInstanceOf(MemoryFile.class);
        MemoryFile memoryFile = (MemoryFile) optionalIndexFile.get();
        IndexFile indexFile = new IndexFile((MemoryFile) optionalIndexFile.get());
        assertThat(indexFile.getContent()).isEqualTo(memoryFile.getContent());
        GeneratedResources retrieved = JSONUtils.getGeneratedResourcesObject(indexFile);
        assertThat(retrieved).isNotNull();
        String fullClassName = "full.class.Name";
        GeneratedResource expected1 = new GeneratedClassResource(fullClassName);
        LocalUri modelLocalUriId = new ReflectiveAppRoot("test")
                .get(ComponentFoo.class)
                .get("this", "is", "fri")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);
        GeneratedResource expected2 = new GeneratedExecutableResource(localUriId,
                                                                      Collections.singletonList(fullClassName));
        assertThat(retrieved).contains(expected1);
        assertThat(retrieved).contains(expected2);
        restoreClassLoader(originalClassLoader);
    }

    @Test
    void getModelLocalUriIdString() throws JsonProcessingException {
        String model = "foo";
        String basePath = "this/is/modelLocalUriId";
        LocalUri modelLocalUriId = new ReflectiveAppRoot("test")
                .get(ComponentFoo.class)
                .get("this", "is", "modelLocalUriId")
                .asLocalUri();
        ModelLocalUriId localUriId = new ModelLocalUriId(modelLocalUriId);
        String retrieved = JSONUtils.getModelLocalUriIdString(localUriId);
        String expected = String.format("{\"model\":\"%1$s\",\"basePath\":\"/%2$s\",\"fullPath\":\"/%1$s/%2$s\"}", model, basePath);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getModelLocalUriIdObject() throws JsonProcessingException {
        String localUriIdString = "{\"model\":\"foo\",\"basePath\":\"/this/is/modelLocalUriId\",\"fullPath\":\"/foo/this/is/modelLocalUriId\"}";
        ModelLocalUriId retrieved = JSONUtils.getModelLocalUriIdObject(localUriIdString);
        assertThat(retrieved).isNotNull();
    }

    private ClassLoader addJarToClassLoader() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        URL jarUrl = Thread.currentThread().getContextClassLoader().getResource("TestJar.jar");
        assertThat(jarUrl).isNotNull();
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("IndexFile.testb_json");
        assertThat(fileUrl).isNull();
        URL[] urls = {jarUrl};
        URLClassLoader testClassLoader = URLClassLoader.newInstance(urls, originalClassLoader);
        Thread.currentThread().setContextClassLoader(testClassLoader);
        return originalClassLoader;
    }

    private void restoreClassLoader(ClassLoader toRestore) {
        Thread.currentThread().setContextClassLoader(toRestore);
    }
}