/*
 * Copyright 2019 Red Hat
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

package org.kie.remote.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import org.kie.remote.RemoteFactHandle;

public class RemoteFactHandleImpl implements RemoteFactHandle {

    private String id;

    private Serializable object;

    /* Empty constructor for serialization */
    public RemoteFactHandleImpl() {
        this.id = UUID.randomUUID().toString();
    }

    public RemoteFactHandleImpl( Serializable object ) {
        this.id = UUID.randomUUID().toString();
        this.object = object;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        RemoteFactHandleImpl that = ( RemoteFactHandleImpl ) o;
        return id.equals( that.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public String toString() {
        return "RemoteFactHandleImpl{" +
                "id='" + id + '\'' +
                ", object=" + object +
                '}';
    }
}
