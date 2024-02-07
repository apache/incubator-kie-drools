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
package org.drools.core.reteoo;

public class ObjectTypeNodeId {

    public static final ObjectTypeNodeId DEFAULT_ID = new ObjectTypeNodeId(-1, 0);

    private final int otnId;
    private final int id;

    public ObjectTypeNodeId(int otnId, int id) {
        this.otnId = otnId;
        this.id    = id;
    }

    @Override
    public String toString() {
        return "ObjectTypeNode.Id[" + otnId + "#" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectTypeNodeId)) {
            return false;
        }

        ObjectTypeNodeId otherId = (ObjectTypeNodeId) o;
        return id == otherId.id && otnId == otherId.otnId;
    }

    @Override
    public int hashCode() {
        return 31 * otnId + 37 * id;
    }

    public boolean before(ObjectTypeNodeId otherId) {
        return otherId != null && (otnId < otherId.otnId || (otnId == otherId.otnId && id < otherId.id));
    }

    public int getId() {
        return id;
    }
}
