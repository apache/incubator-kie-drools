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
package org.drools.model.functions.accumulate;

import java.util.Objects;

import org.drools.model.DomainClassMetadata;

public class GroupKey {
    private final String topic;
    private final Object key;

    public GroupKey( String topic, Object key ) {
        this.topic = topic;
        this.key = key;
    }

    public String getTopic() {
        return topic;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        GroupKey groupKey = ( GroupKey ) o;
        return topic.equals( groupKey.topic ) &&
                Objects.equals( key, groupKey.key );
    }

    @Override
    public int hashCode() {
        return Objects.hash( topic, key );
    }

    public enum Metadata implements DomainClassMetadata {

        INSTANCE;

        @Override
        public Class<?> getDomainClass() {
            return GroupKey.class;
        }

        @Override
        public int getPropertiesSize() {
            return 2;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "key": return 0;
                case "topic": return 1;
            }
            throw new RuntimeException("Unknown property '" + name + "' for class class class org.drools.model.functions.accumulate.GroupKey");
        }
    }
}
