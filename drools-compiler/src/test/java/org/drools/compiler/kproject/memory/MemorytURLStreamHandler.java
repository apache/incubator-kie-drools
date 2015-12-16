/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kproject.memory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class MemorytURLStreamHandler extends URLStreamHandler {
    
    private byte[] bytes;
    
    public MemorytURLStreamHandler(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        String protocol = url.getProtocol();
        if ( !"memory".equals( protocol ) ) {
            throw new RuntimeException( "Memory protocol unable to handle:" + url.toExternalForm() );
        }
        
        return new MemoryURLConnection( url, bytes );
    }

}
