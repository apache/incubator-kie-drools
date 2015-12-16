/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kproject;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

public interface KProjectTestClass {
    public KieBase getKBase1();

    public KieBase getKBase2();

    public KieBase getKBase3();
    
    public StatelessKieSession getKBase1KSession1();
    
    public KieSession getKBase1KSession2();
    
    public KieSession getKBase2KSession3();

    public StatelessKieSession getKBase3KSession4();
}
