/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.codegen;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.ruleunits.impl.AssignableChecker;
import org.drools.ruleunits.impl.ReflectiveRuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.SingletonStore;

public class RuleUnitHelper {
    private AssignableChecker defaultChecker;
    private AssignableChecker assignableChecker;

    public RuleUnitHelper() {
    }

    public RuleUnitHelper(ClassLoader cl, boolean hotReloadMode) {
        this.defaultChecker = AssignableChecker.create(cl, hotReloadMode);
    }

    public RuleUnitHelper(AssignableChecker assignableChecker) {
        this.assignableChecker = assignableChecker;
    }

    void initRuleUnitHelper(RuleUnitDescription ruleUnitDesc) {
        if (ruleUnitDesc instanceof ReflectiveRuleUnitDescription) {
            assignableChecker = ((ReflectiveRuleUnitDescription) ruleUnitDesc).getAssignableChecker();
        } else {
            if (assignableChecker == null) {
                assignableChecker = defaultChecker;
            }
        }
    }

    public AssignableChecker getAssignableChecker() {
        return assignableChecker;
    }

    public boolean isAssignableFrom(Class<?> source, Class<?> target) {
        return assignableChecker.isAssignableFrom(source, target);
    }

    BlockStmt fieldInitializer(RuleUnitVariable ruleUnitVariable, String genericType, boolean isDataSource) {
        BlockStmt supplierBlock = new BlockStmt();

        if (!isDataSource) {
            if (ruleUnitVariable.setter() != null) {
                supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
            }
        } else if (isAssignableFrom(DataStream.class, ruleUnitVariable.getType())) {
            if (ruleUnitVariable.setter() != null) {
                supplierBlock.addStatement(String.format("org.kie.kogito.rules.DataStream<%s> %s = org.kie.kogito.rules.DataSource.createStream();", genericType, ruleUnitVariable.getName()));
                supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
            }
            supplierBlock.addStatement(String.format("this.%s.forEach( unit.%s()::append);", ruleUnitVariable.getName(), ruleUnitVariable.getter()));
        } else if (isAssignableFrom(DataStore.class, ruleUnitVariable.getType())) {
            if (ruleUnitVariable.setter() != null) {
                supplierBlock.addStatement(String.format("org.kie.kogito.rules.DataStore<%s> %s = org.kie.kogito.rules.DataSource.createStore();", genericType, ruleUnitVariable.getName()));
                supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
            }
            supplierBlock.addStatement(String.format("this.%s.forEach( unit.%s()::add);", ruleUnitVariable.getName(), ruleUnitVariable.getter()));
        } else if (isAssignableFrom(SingletonStore.class, ruleUnitVariable.getType())) {
            supplierBlock.addStatement(String.format("unit.%s().set(this.%s );", ruleUnitVariable.getter(), ruleUnitVariable.getName()));
        } else {
            throw new IllegalArgumentException("Unknown data source type " + ruleUnitVariable.getType());
        }

        return supplierBlock;
    }

    String createDataSourceMethodName(Class<?> dsClass) {
        if (isAssignableFrom(DataStream.class, dsClass)) {
            return "createStream";
        }
        if (isAssignableFrom(DataStore.class, dsClass)) {
            return "createStore";
        }
        if (isAssignableFrom(SingletonStore.class, dsClass)) {
            return "createSingleton";
        }
        throw new IllegalArgumentException("Unknown data source type " + dsClass.getCanonicalName());
    }
}
