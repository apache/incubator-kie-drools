/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.api.solver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ClassLoaderTest {

    public ClassLoader mockDivertingClassLoader() throws ClassNotFoundException, IOException {
        final String divertedPrefix = "divertThroughClassLoader";
        final ClassLoader realClassLoader = getClass().getClassLoader();
        ClassLoader divertingClassLoader = mock(ClassLoader.class);
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        when(divertingClassLoader.loadClass(anyString())).thenAnswer(new Answer<Class<?>>() {
            @Override
            public Class<?> answer(InvocationOnMock invocation) throws Throwable {
                String className = (String) invocation.getArguments()[0];
                if (className.startsWith(divertedPrefix + ".")) {
                    className = className.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.loadClass(className);
            }
        });
        when(divertingClassLoader.getResource(anyString())).thenAnswer(new Answer<URL>() {
            @Override
            public URL answer(InvocationOnMock invocation) {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResource(resourceName);
            }
        });
        when(divertingClassLoader.getResourceAsStream(anyString())).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResourceAsStream(resourceName);
            }
        });
        when(divertingClassLoader.getResources(anyString())).thenAnswer(new Answer<Enumeration<URL>>() {
            @Override
            public Enumeration<URL> answer(InvocationOnMock invocation) throws Throwable {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResources(resourceName);
            }
        });
        // Mocking divertingClassLoader.getParent() fails because it's a final method
        return divertingClassLoader;
    }
}
