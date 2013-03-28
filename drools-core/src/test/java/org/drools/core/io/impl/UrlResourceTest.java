/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.io.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.junit.After;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Basic tests for UrlResource caching logic.
 * <p/>
 * Focused on https://issues.jboss.org/browse/DROOLS-66.
 */
public class UrlResourceTest {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    private UrlResource urlResource;

    @After
    public void cleanup() {
        if (urlResource != null && urlResource.getCacheFile().exists()) {
            urlResource.getCacheFile().delete();
        }
    }

    /**
     * Tests reading a UrlResource contents over HTTP with local cache turned on.
     */
    @Test
    public void testHttpWithCache() throws IOException {
        final File origCacheDir = UrlResource.CACHE_DIR;
        UrlResource.CACHE_DIR = TEMP_DIR;
        try {
            this.doTestGetInputStream(new URL("http://localhost/fakeResourceHttp"),
                                      "mock http response");
        } finally {
            UrlResource.CACHE_DIR = origCacheDir;
        }
    }

    /**
     * Tests reading a UrlResource contents over HTTPS with local cache turned on.
     */
    @Test
    public void testHttpsWithCache() throws IOException {
        final File origCacheDir = UrlResource.CACHE_DIR;
        UrlResource.CACHE_DIR = TEMP_DIR;
        try {
            this.doTestGetInputStream(new URL("https://localhost/fakeResourceHttps"),
                                      "mock https response");
        } finally {
            UrlResource.CACHE_DIR = origCacheDir;
        }
    }

    /**
     * Tests reading a UrlResource contents over HTTP with local cache turned off.
     */
    @Test
    public void testHttpWithNoCache() throws IOException {
        final File origCacheDir = UrlResource.CACHE_DIR;
        UrlResource.CACHE_DIR = null;
        try {
            this.doTestGetInputStream(new URL("http://localhost/fakeResourceHttp"),
                                      "mock http response");
        } finally {
            UrlResource.CACHE_DIR = origCacheDir;
        }
    }

    /**
     * Tests reading a UrlResource contents over HTTPS with local cache turned off.
     */
    @Test
    public void testHttpsWithNoCache() throws IOException {
        final File origCacheDir = UrlResource.CACHE_DIR;
        UrlResource.CACHE_DIR = null;
        try {
            this.doTestGetInputStream(new URL("https://localhost/fakeResourceHttps"),
                                      "mock https response");
        } finally {
            UrlResource.CACHE_DIR = origCacheDir;
        }
    }

    /**
     * Executes single UrlResource test with given url which returns expectedResponse.
     */
    private void doTestGetInputStream(final URL url, final String expectedResponse) throws IOException {
        urlResource = getTestableUrlResource(url, expectedResponse);

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlResource.getInputStream(), CHARSET));
        final String actualResponse = reader.readLine();
        
        assertEquals("Unexpected response", expectedResponse, actualResponse);
        if (UrlResource.CACHE_DIR != null) {
            assertTrue("Cache file should exist", urlResource.getCacheFile().exists());
        } else {
            assertFalse("Cache file should not exist", urlResource.getCacheFile().exists());
        }
    }
    
    /**
     * Creates UrlResource returning given response instead of real connecting to given URL.
     */
    private UrlResource getTestableUrlResource(final URL url, final String response) {
        return new UrlResource(url) {

            @Override
            protected URLConnection openURLConnection() throws IOException {
                return mockURLConnection(response);
            }
        };
    }

    /**
     * Returns mock URLConnection returning fixed response String and
     * lastModified value.
     * 
     * @param response String to be returned by the InputStream of this connection
     * @return mocked URLConnection
     */
    private URLConnection mockURLConnection(final String response) {
        final URLConnection mockURLConnection = Mockito.mock(URLConnection.class);
        try {
            Mockito.when(mockURLConnection.getLastModified()).thenReturn(System.currentTimeMillis());
            Mockito.when(mockURLConnection.getInputStream()).thenReturn(
                    new ByteArrayInputStream(response.getBytes(CHARSET)));

        } catch (IOException e) {
            throw new RuntimeException("Exception during mocking.", e);
        }

        return mockURLConnection;
    }
}
