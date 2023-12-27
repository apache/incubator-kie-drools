/*
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
package org.kie.kogito.app.audit.jpa.queries.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.app.audit.jpa.queries.DataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoMapper<T> implements DataMapper<T, Object[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PojoMapper.class);

    private Class<T> clazz;
    private Constructor<T> defaultConstructor;

    public PojoMapper(Class<T> clazz) {
        this.clazz = clazz;
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() > 0) {
                defaultConstructor = (Constructor<T>) constructor;
            }
        }
    }

    @Override
    public List<T> produce(List<Object[]> data) {
        List<T> transformed = new ArrayList<>();
        for (Object[] row : data) {
            try {
                transformed.add(defaultConstructor.newInstance(row));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.error("Could not transform data", e);
            }
        }
        return transformed;
    }

}
