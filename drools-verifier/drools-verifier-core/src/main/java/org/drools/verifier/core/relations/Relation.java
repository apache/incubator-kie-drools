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
package org.drools.verifier.core.relations;

import org.drools.verifier.core.index.keys.UUIDKey;

public abstract class Relation<T extends Relation> {

    protected final T origin;
    protected T parent = null;

    public Relation(final T origin) {
        this.origin = origin;
        if (origin != null) {
            origin.setParent(this);
        }
    }

    public T getOrigin() {
        if (origin == null) {
            return (T) this;
        } else {
            return origin;
        }
    }

    public abstract boolean foundIssue();

    public abstract UUIDKey otherUUID();

    public abstract boolean doesRelationStillExist();

    protected void setParent(final T parent) {
        this.parent = parent;
    }
}
