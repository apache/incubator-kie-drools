/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

// credits for https://www.cs.mun.ca/java-api-1.5/guide/deployment/deployment-guide/upgrade-guide/article-17.html

package org.optaplanner.examples.conferencescheduling.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionFollowRedirects {

    private URLConnection connection;
    private boolean isRedirect;
    private int redirects = 0;

    public ConnectionFollowRedirects(String url) throws IOException {
        this.connection = new URL(url).openConnection();
    }

    public URLConnection getConnection() {
        return connection;
    }

    public int getRedirects() {
        return redirects;
    }

    public InputStream getInputStream() throws IOException {
        InputStream in = null;
        do {
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
            }
            // We want to open the input stream before getting headers
            // because getHeaderField() et al swallow IOExceptions.
            in = connection.getInputStream();
            followRedirects();
        } while (isRedirect);
        return in;
    }

    private void followRedirects() throws IOException {
        isRedirect = false;
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection http = (HttpURLConnection) connection;
            int stat = http.getResponseCode();
            if (stat >= 300 && stat <= 307 && stat != 306 &&
                    stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
                redirectConnection(http);
            }
        }
    }

    private void redirectConnection(HttpURLConnection http) throws IOException {
        URL base = http.getURL();
        String location = http.getHeaderField("Location");
        URL target = null;
        if (location != null) {
            target = new URL(base, location);
        }
        http.disconnect();
        // Redirection should be allowed only for HTTP and HTTPS
        // and should be limited to 5 redirections at most.
        if (target == null || !(target.getProtocol().equals("http")
                || target.getProtocol().equals("https"))
                || redirects >= 5) {
            throw new SecurityException("illegal URL redirect");
        }
        isRedirect = true;
        connection = target.openConnection();
        redirects++;
    }
}
