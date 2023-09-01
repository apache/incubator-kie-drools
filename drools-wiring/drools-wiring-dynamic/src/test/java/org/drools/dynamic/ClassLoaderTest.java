package org.drools.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.Test;

public class ClassLoaderTest {

    @Test(timeout = 20000)
    public void testParallelClassLoading() {

        final int THREAD_COUNT = 100;

        final ProjectClassLoader projectClassLoader = ProjectClassLoader.createProjectClassLoader();
        final ClassLoader internalTypesClassLoader = (ClassLoader) projectClassLoader.makeClassLoader();

        projectClassLoader.setInternalClassLoader((ProjectClassLoader.InternalTypesClassLoader) internalTypesClassLoader);

        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            final List<Future> futures = new ArrayList<>();

            for (int i = 0; i < THREAD_COUNT; i++) {
                if (i % 2 == 0) {
                    futures.add(executorService.submit(() -> {
                        try {
                            Class.forName("nonexistant", true, projectClassLoader);
                        } catch (ClassNotFoundException e) {
                            //
                        }
                    }));
                } else {
                    futures.add(executorService.submit(() -> {
                        try {
                            Class.forName("nonexistant", true, internalTypesClassLoader);
                        } catch (ClassNotFoundException e) {
                            //
                        }
                    }));
                }
            }

            for (int i = 1; i <= THREAD_COUNT; i++) {
                try {
                    futures.get(i - 1).get();
                } catch (final InterruptedException | ExecutionException e) {
                    // Nothing
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }

}
