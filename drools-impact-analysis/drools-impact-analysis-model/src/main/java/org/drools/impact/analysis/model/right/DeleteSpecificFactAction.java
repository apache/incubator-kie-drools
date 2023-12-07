/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

/**
 * This class represents a delete action for a specific fact.
 * Use this action when you know the fact's properties to be deleted, assuming other facts of the same class still
 * exist in the working memory. (Usually, you don't know the fact's properties)
 * This action is introduced to support retract_fact action in drools-ansible-rulebook-integration-visualization.
 */
public class DeleteSpecificFactAction extends ConsequenceAction {

    private final List<SpecificProperty> specificProperties = new ArrayList<>();

    public DeleteSpecificFactAction(Class<?> actionClass) {
        super(Type.DELETE, actionClass);
    }

    public List<SpecificProperty> getSpecificProperties() {
        return specificProperties;
    }

    public void addSpecificProperty(SpecificProperty specificProperty) {
        specificProperties.add(specificProperty);
    }

    @Override
    public String toString() {
        return "DeleteSpecificFactAction{" +
                "actionClass=" + actionClass +
                ", specificProperties=" + specificProperties +
                '}';
    }
}
