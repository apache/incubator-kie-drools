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
package org.drools.core.util;

import java.io.IOException;
import java.io.StringReader;

import org.drools.io.ReaderInputStream;
import org.drools.util.IoUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IoUtilsTest {

    @Test
    public void testReadEmptyStream() throws IOException {
        // DROOLS-971
        byte[] bytes = IoUtils.readBytesFromInputStream( new ReaderInputStream( new StringReader( "" ) ) );
        assertThat(bytes).isEmpty();
    }

    @Test
    public void testAsSystemSpecificPath() {
        String urlPath = "c:\\workdir\\server-local\\instance\\tmp\\vfs\\deployment\\deploymentf7b5abe7c4c1cb56\\rules-with-kjar-1.0.jar-57cc270a5729d6b2\\rules-with-kjar-1.0.jar";
        String specificPath = IoUtils.asSystemSpecificPath(urlPath, 1);
        // Check that windows drive (even in lower case) is not removed
        assertThat(specificPath).isEqualTo(urlPath);
    }
}
