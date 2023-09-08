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
package org.drools.decisiontable.parser;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.template.parser.DataListener.NON_MERGED;

public class PropertiesSheetListenerTest {

    @Test
    public void testProperties() {
        final PropertiesSheetListener listener = new PropertiesSheetListener();
        listener.startSheet("test");

        listener.newRow(0, 4);

        listener.newCell(0, 0, "", NON_MERGED);

        listener.newCell(0, 1, "key1", NON_MERGED);
        listener.newCell(0, 2, "value1", NON_MERGED);

        listener.newRow(1, 4);
        listener.newCell(1, 1, "key2", NON_MERGED);
        listener.newCell(1, 3, "value2", NON_MERGED);

        listener.newRow(2, 4);
        listener.newCell(1, 1, "key3", NON_MERGED);

        final CaseInsensitiveMap props = listener.getProperties();

        assertThat(props.getSingleProperty("Key1")).isEqualTo("value1");
        assertThat(props.getSingleProperty("key2")).isEqualTo("value2");
        
    }

    @Test
    public void testCaseInsensitive() {
        CaseInsensitiveMap map = new PropertiesSheetListener.CaseInsensitiveMap();
        map.addProperty("x3", new String[]{ "hey", "B2" });
        map.addProperty("x4", new String[]{ "wHee", "C3" });
        map.addProperty("XXx", new String[]{ "hey2", "D4" });

        assertThat(map.getProperty("x")).isNull();
        assertThat(map.getSingleProperty("x3")).isEqualTo("hey");
        assertThat(map.getSingleProperty("X3")).isEqualTo("hey");
        assertThat(map.getSingleProperty("x4")).isEqualTo("wHee");
        assertThat(map.getSingleProperty("xxx")).isEqualTo("hey2");
        assertThat(map.getSingleProperty("XXX")).isEqualTo("hey2");
        assertThat(map.getSingleProperty("XXx")).isEqualTo("hey2");
        assertThat(map.getSingleProperty("x", "Whee2")).isEqualTo("Whee2");

    }

}
