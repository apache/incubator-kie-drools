package org.kie.efesto.runtimemanager.core.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedResourceUtilsTest {

    @Test
    void getGeneratedExecutableResource() {
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/test/testmod"));
        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        Optional<GeneratedExecutableResource> retrieved = GeneratedResourceUtils.getGeneratedExecutableResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isPresent();
        modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/test/notestmod"));
        retrieved = GeneratedResourceUtils.getGeneratedExecutableResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void getGeneratedRedirectResourceFromFile() {
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/test/redirecttestmod"));
        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        Optional<GeneratedRedirectResource> retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isPresent();
        modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/test/redirectnotestmod"));
        retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void getGeneratedRedirectResourceFromJar() {
        ClassLoader originalClassLoader = addJarToClassLoader();
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/testb/redirecttestmod"));


        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        Optional<GeneratedRedirectResource> retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isPresent();
        modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/testb/redirectnotestmod"));
        retrieved = GeneratedResourceUtils.getGeneratedRedirectResource(modelLocalUriId, context.getGeneratedResourcesMap());
        assertThat(retrieved).isNotNull().isNotPresent();
        restoreClassLoader(originalClassLoader);
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