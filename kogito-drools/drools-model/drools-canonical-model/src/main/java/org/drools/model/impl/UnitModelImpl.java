/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.drools.model.EntryPoint;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.TypeMetaData;

public class UnitModelImpl implements Model {

    private final Model model;
    private final List<Rule> rules;

    public UnitModelImpl(Model model, String unitClass) {
        this.model = model;
        this.rules = model.getRules().stream()
                .filter(r -> r.getUnit().equals(unitClass)).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public List<Global> getGlobals() {
        return model.getGlobals();
    }

    @Override
    public List<Query> getQueries() {
        return model.getQueries();
    }

    @Override
    public List<TypeMetaData> getTypeMetaDatas() {
        return model.getTypeMetaDatas();
    }

    @Override
    public List<EntryPoint> getEntryPoints() {
        return model.getEntryPoints();
    }
}
