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
import java.net.URL;

import org.drools.definition.KnowledgePackage;
import org.drools.rule.Package;

/**
 * A nicely mockable Http client interface.
 * 
 * IM IN YR HTTP MOCKIN UR CLEINT
 * 
 * @author Michael Neale
 *
 */
public interface IHttpClient {

    public LastUpdatedPing checkLastUpdated(URL url) throws IOException;

    public Package fetchPackage(URL url, boolean enableBasicAuthentication, String username, String password) throws IOException,
                                        ClassNotFoundException;

}

/**
 * This is returned when pinging for changes.
 * 
 * @author Michael Neale
 */
class LastUpdatedPing {
    public long   lastUpdated = -1;
    public String responseMessage;

    public boolean isError() {
        if ( lastUpdated == -1 ) return true;
        if ( responseMessage == null ) return true;
        if ( responseMessage.indexOf( "200 OK" ) == -1 ) return true;
        return false;
    }

    public String toString() {
        return "Last updated: " + lastUpdated + "\n" + "Reponse header: " + responseMessage;
    }

}
