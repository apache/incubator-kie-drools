/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen.operation;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;

public class TestGenKieSessionDelete implements TestGenKieSessionOperation {

    private final int id;
    private final TestGenFact fact;

    public TestGenKieSessionDelete(int id, TestGenFact fact) {
        this.id = id;
        this.fact = fact;
    }

    @Override
    public void invoke(KieSession kieSession) {
        kieSession.delete(kieSession.getFactHandle(fact.getInstance()));
    }

    @Override
    public void print(StringBuilder sb) {
        sb.append(String.format("        //%s\n", this));
        sb.append(String.format("        kieSession.delete(kieSession.getFactHandle(%s));\n", fact));
    }

    @Override
    public String toString() {
        return "operation D #" + id;
    }

}
