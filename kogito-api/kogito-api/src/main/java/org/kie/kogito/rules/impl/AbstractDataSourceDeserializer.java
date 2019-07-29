/*
 * Copyright 2005 JBoss Inc
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

package org.kie.kogito.rules.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import org.kie.kogito.rules.DataSource;

public abstract class AbstractDataSourceDeserializer<T extends DataSource> implements JsonbDeserializer<T> {

    @Override
    public T deserialize( JsonParser jsonParser, DeserializationContext deserializationContext, Type type ) {
        List deserialized = (List)deserializationContext.deserialize( new ListWrapperType(type), jsonParser );
        T ds = toDataSource( deserialized );
        return ds;
    }

    protected abstract T toDataSource( List deserialized );

    public static class ListWrapperType implements ParameterizedType {

        private final ParameterizedType originalType;

        public ListWrapperType( Type originalType ) {
            this.originalType = (ParameterizedType)originalType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return originalType.getActualTypeArguments();
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return originalType.getOwnerType();
        }
    }
}
