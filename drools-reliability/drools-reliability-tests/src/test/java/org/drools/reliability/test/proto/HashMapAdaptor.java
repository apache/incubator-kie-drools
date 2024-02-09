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
package org.drools.reliability.test.proto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * This adaptor is specialized for HashMap<String, Object>. If you have a different type of map, you need to enhance this adaptor.
 */
@ProtoAdapter(HashMap.class)
public class HashMapAdaptor {

    @ProtoFactory
    HashMap<String, Object> create(List<EntryImpl> entryList) {
        HashMap<String, Object> map = new HashMap<>();
        for (EntryImpl entry : entryList) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @ProtoField(number = 1, collectionImplementation = ArrayList.class)
    List<EntryImpl> getEntryList(HashMap<String, Object> map) {
        return map.entrySet()
                .stream()
                .map(e -> new EntryImpl(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

