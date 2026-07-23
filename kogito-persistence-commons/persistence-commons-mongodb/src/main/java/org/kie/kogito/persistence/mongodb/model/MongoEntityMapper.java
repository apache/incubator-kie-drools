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
package org.kie.kogito.persistence.mongodb.model;

import java.util.regex.Pattern;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

/**
 * A mongo entity mapper is responsible for converting between a data model and the corresponding mongo storage entity
 * 
 * @param <V> the type of the data model
 * @param <E> the type of the mongo storage entity
 */
public interface MongoEntityMapper<V, E> {

    /**
     * Get the mongo storage entity class
     * 
     * @return the mongo storage entity class
     */
    Class<E> getEntityClass();

    /**
     * Convert data model to mongo storage entity
     * 
     * @param key the key of the data model
     * @param value the data model
     * @return the converted mongo storage entity
     */
    E mapToEntity(String key, V value);

    /**
     * Convert mongo storage entity to data model
     * 
     * @param entity the mongo storage entity
     * @return the converted data model
     */
    V mapToModel(E entity);

    /**
     * Convert the data model attribute name to mongo storage attribute name
     * 
     * @param attribute the data model attribute name
     * @return the corresponding mongo storage attribute name
     */
    default String convertToMongoAttribute(String attribute) {
        return ModelUtils.ID.equals(attribute) ? MONGO_ID : attribute;
    }

    /**
     * Convert mongo storage attribute name to the data model attribute name
     * 
     * @param attribute the mongo storage attribute name
     * @return the corresponding data model attribute name
     */
    default String convertToModelAttribute(String attribute) {
        if (MONGO_ID.equals(attribute)) {
            return ModelUtils.ID;
        }
        String[] attributes = attribute.split(Pattern.quote(ModelUtils.ATTRIBUTE_DELIMITER));
        return attributes[attributes.length - 1];
    }
}
