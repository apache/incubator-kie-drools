/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.cdi.kproject;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

public class KProjectTestClassImpl implements KProjectTestClass {
    
    KieContainer kContainer;
    String namespace;
    
    public KProjectTestClassImpl(String namespace, KieContainer kContainer) {
        this.namespace = namespace;
        this.kContainer = kContainer;
    }

    @Override
    public KieBase getKBase1() {
        return this.kContainer.getKieBase( namespace + ".KBase1" );
    }

    @Override
    public KieBase getKBase2() {
        return this.kContainer.getKieBase( namespace + ".KBase2" );
    }

    @Override
    public KieBase getKBase3() {
        return this.kContainer.getKieBase( namespace + ".KBase3" );
    }

    @Override
    public StatelessKieSession getKBase1KSession1() {
        return this.kContainer.newStatelessKieSession(namespace + ".KSession1");
    }

    @Override
    public KieSession getKBase1KSession2() {
        return this.kContainer.newKieSession(namespace + ".KSession2");
    }

    @Override
    public KieSession getKBase2KSession3() {
        return this.kContainer.newKieSession(namespace + ".KSession3");
    }

    @Override
    public StatelessKieSession getKBase3KSession4() {
        return this.kContainer.newStatelessKieSession(namespace + ".KSession4");
    }

}
