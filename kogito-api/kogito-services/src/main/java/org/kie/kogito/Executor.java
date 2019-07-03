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

package org.kie.kogito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.RuleUnitMemory;
import org.kie.kogito.rules.impl.RuleUnitRegistry;

public class Executor {

    private final ExecutorService executorService;

    public static Executor create() {
        return new Executor();
    }

    private Executor() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public Future<Integer> submit(RuleUnitMemory ruleUnitMemory) {
        RuleUnitInstance<?> instance = RuleUnitRegistry.instance(ruleUnitMemory);
        return this.submit(instance);
    }

    public Future<Integer> submit(RuleUnitInstance<?> instance) {
        return this.executorService.submit(() -> {
            int result = instance.fire();
//            this.submit(instance);
            return result;
        });
    }
}
