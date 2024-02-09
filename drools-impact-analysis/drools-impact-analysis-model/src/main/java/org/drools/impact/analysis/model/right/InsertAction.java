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
package org.drools.impact.analysis.model.right;

import java.util.ArrayList;
import java.util.List;

public class InsertAction extends ConsequenceAction {

    private final List<InsertedProperty> insertedProperties = new ArrayList<>();

    public InsertAction( Class<?> actionClass ) {
        super( Type.INSERT, actionClass );
    }

    public List<InsertedProperty> getInsertedProperties() {
        return insertedProperties;
    }

    public void addInsertedProperty(InsertedProperty insertedProperty) {
        insertedProperties.add( insertedProperty );
    }

    @Override
    public String toString() {
        return "InsertAction{" +
                "actionClass=" + actionClass +
                ", insertedProperties=" + insertedProperties +
                '}';
    }
}
