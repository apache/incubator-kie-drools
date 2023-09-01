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
package org.drools.reliability.infinispan.proto;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

/**
 * Currently this class supports only one object or an ArrayList of objects. Users should implement Adaptor classes for their own objects.
 */
public class ProtoStreamGlobal {

    private final Object object;
    private boolean typeArrayList;

    public ProtoStreamGlobal(Object object) {
        this.object = object;
        this.typeArrayList = object instanceof ArrayList;
    }

    @ProtoFactory
    public ProtoStreamGlobal(AnySchema.Any protoObject, List<AnySchema.Any> protoObjectList, boolean typeArrayList) {
        if (typeArrayList) {
            List<Object> list = new ArrayList<>();
            for (AnySchema.Any any : protoObjectList) {
                list.add(ProtoStreamUtils.fromAnySchema(any));
            }
            this.object = list;
        } else {
            this.object = ProtoStreamUtils.fromAnySchema(protoObject);
        }
    }

    @ProtoField(number = 1)
    public AnySchema.Any getProtoObject() {
        if (typeArrayList) {
            return null;
        } else {
            return ProtoStreamUtils.toAnySchema(object);
        }
    }

    @ProtoField(number = 2, collectionImplementation = ArrayList.class)
    public List<AnySchema.Any> getProtoObjectList() {
        if (typeArrayList) {
            List<Object> list = (List<Object>) object;
            List<AnySchema.Any> protoObjectList = new ArrayList<>();
            for (Object o : list) {
                AnySchema.Any any = ProtoStreamUtils.toAnySchema(o);
                protoObjectList.add(any);
            }
            return protoObjectList;
        } else {
            return new ArrayList<>();
        }
    }

    @ProtoField(number = 3, required = true)
    public boolean isTypeArrayList() {
        return typeArrayList;
    }

    public Object getObject() {
        return object;
    }
}
