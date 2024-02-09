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
package org.drools.model.impl;

import java.util.concurrent.TimeUnit;

import org.drools.model.WindowDefinition;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public abstract class AbstractWindow implements WindowDefinition, ModelComponent {

    private final Type type;
    private final long value;

    public AbstractWindow( Type type, long value ) {
        this(type, value, null);
    }

    public AbstractWindow( Type type, long value, TimeUnit timeUnit ) {
        this.type = type;
        this.value = unitToLong( value, timeUnit );
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof AbstractWindow) ) return false;

        AbstractWindow that = ( AbstractWindow ) o;
        return type == that.type && value == that.value;
    }
}
