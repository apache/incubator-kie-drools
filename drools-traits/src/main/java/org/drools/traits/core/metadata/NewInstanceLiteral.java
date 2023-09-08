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
package org.drools.traits.core.metadata;

import java.net.URI;

public abstract class NewInstanceLiteral<T extends Metadatable> extends AbstractWMTask<T> implements NewInstance<T> {
    protected URI uri;
    protected T result;

    protected ModifyLiteral setter;

    protected With[] with;

    public NewInstanceLiteral( With... args ) {
        this.with = args;
    }

    public NewInstanceLiteral( Object identifier, With... args ) {
        this.uri = URI.create( identifier.toString() );
        this.with = args;
    }

    public boolean isInterface() {
        return false;
    }

    @Override
    public KIND kind() {
        return KIND.ASSERT;
    }

    @Override
    public Object getTargetId() {
        return uri;
    }

    @Override
    public T call() {
        result = (T) construct();
        if ( setter != null ) {
            setter.setTarget( result );
            setter.call();
        }
        return result;
    }

    protected abstract Object construct();

    @Override
    public URI getUri() {
        return URI.create( uri.toString() + "?create" );
    }

    @Override
    public Object getId() {
        return uri;
    }

    @Override
    public ModifyLiteral<T> getSetters() {
        return setter;
    }

    @Override
    public T getTarget() {
        return result;
    }


    @Override
    public int hashCode() {
        return System.identityHashCode( this );
    }
}

