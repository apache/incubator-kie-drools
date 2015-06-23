/*
 * Copyright 2015 JBoss Inc
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class MemoryURLConnection extends URLConnection {

    private URL url;
    
    private byte[] bytes;

    protected MemoryURLConnection(URL url, byte[] bytes) {
        super( url );
        this.url = url;
        this.bytes = bytes;
    }

    @Override
    public void connect() throws IOException {
        
    }
    
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream( bytes );
                
    }

}
