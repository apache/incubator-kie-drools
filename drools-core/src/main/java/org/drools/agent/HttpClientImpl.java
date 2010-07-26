/**
 * Copyright 2010 JBoss Inc
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

package org.drools.agent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.drools.core.util.DroolsStreamUtils;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;
import org.drools.util.codec.Base64;


public class HttpClientImpl
    implements
    IHttpClient {

    public LastUpdatedPing checkLastUpdated(URL url) throws IOException {
        URLConnection con = url.openConnection();
        HttpURLConnection httpCon = (HttpURLConnection) con;
        try {
            httpCon.setRequestMethod( "HEAD" );

            String lm = httpCon.getHeaderField( "lastModified" );
            LastUpdatedPing ping = new LastUpdatedPing();

            ping.responseMessage = httpCon.getHeaderFields().toString();

            if ( lm != null ) {
                ping.lastUpdated = Long.parseLong( lm );
            } else {
                long httpLM = httpCon.getLastModified();
                if ( httpLM > 0 ) {
                    ping.lastUpdated = httpLM;
                }
            }

            return ping;
        } finally {
            httpCon.disconnect();
        }

    }

    public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException,
                                        ClassNotFoundException {
        URLConnection con = url.openConnection();
        HttpURLConnection httpCon = (HttpURLConnection) con;
        try {
            httpCon.setRequestMethod( "GET" );

            if (enableBasicAuthentication) {
				String userpassword = username + ":" + password;
				byte[] authEncBytes = Base64.encodeBase64(userpassword
						.getBytes());
				httpCon.setRequestProperty("Authorization", "Basic "
						+ new String(authEncBytes));
			} 

            
            Object o = DroolsStreamUtils.streamIn( httpCon.getInputStream() );

            if ( o instanceof KnowledgePackageImp ) {
                return ((KnowledgePackageImp) o).pkg;
            } else {
                return (Package) o;
            }
        } finally {
            httpCon.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClientImpl cl = new HttpClientImpl();
        URL url = new URL( "http://localhost:8888/org.drools.guvnor.Guvnor/package/com.billasurf.manufacturing.plant/SNAP" );

        LastUpdatedPing ping = cl.checkLastUpdated( url );

        Package p = cl.fetchPackage( url, false, null, null );

        System.err.println( ping );
        System.err.println( ping.isError() );
    }

}
