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
package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.GroupByDescrBuilder;

public class GroupByDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends AccumulateDescrBuilderImpl<P> implements GroupByDescrBuilder<P> {

    public GroupByDescrBuilderImpl(P parent) {
        super(parent, new GroupByDescr());
    }

    @Override
    public GroupByDescrBuilder<P> groupingFunction(String block) {
        ((GroupByDescr) descr).setGroupingFunction(block);
        return this;
    }

    @Override
    public GroupByDescrBuilder<P> groupingFunction(String block, String key) {
        ((GroupByDescr) descr).setGroupingFunction(block);
        ((GroupByDescr) descr).setGroupingKey(key);
        return this;
    }
}
