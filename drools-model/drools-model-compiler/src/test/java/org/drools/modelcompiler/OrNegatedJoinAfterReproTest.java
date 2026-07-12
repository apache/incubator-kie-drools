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
package org.drools.modelcompiler;

import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.drools.model.DSL.after;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.and;
import static org.drools.model.PatternDSL.or;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class OrNegatedJoinAfterReproTest {

    @Role(Role.Type.EVENT) public static class EvC { public int id; public EvC(int id){this.id=id;} public int getId(){return id;} }
    @Role(Role.Type.EVENT) public static class EvD { public int id; public EvD(int id){this.id=id;} public int getId(){return id;} }
    @Role(Role.Type.EVENT) public static class EvE { public int id; public EvE(int id){this.id=id;} public int getId(){return id;} }

    private static void buildAndReplay(Rule r) {
        Model model = new ModelImpl().addRule(r);
        KieBase kb = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(ClockTypeOption.PSEUDO);
        KieSession ks = kb.newKieSession(conf, null);
        SessionPseudoClock clock = ks.getSessionClock();
        List<Object> events = List.of(
            new EvC(1), new EvD(1), new EvE(1),
            new EvC(2), new EvD(2), new EvE(2));
        try {
            for (Object e : events) {
                clock.advanceTime(1, TimeUnit.MILLISECONDS);
                ks.insert(e);
                ks.fireAllRules();
            }
            ks.fireAllRules();
        } finally {
            ks.dispose();
        }
    }

    @Test
    public void orWithNegatedJoinBranchPlusDownstreamAfterRunsWithoutNPE() {
        Variable<EvE> e1 = declarationOf(EvE.class);
        Variable<EvE> e2 = declarationOf(EvE.class);
        Variable<EvC> c  = declarationOf(EvC.class);
        Variable<EvE> et = declarationOf(EvE.class);
        Variable<EvD> d  = declarationOf(EvD.class);

        Rule r = rule("min-or-negjoin-after").build(
            or(
                pattern(e1).expr("e1", v -> true),
                and(
                        not(pattern(e2).expr("e2", v -> true)),
                        pattern(c).expr("c", v -> true)
                )
            ),
            pattern(et).expr("et", v -> true),
            pattern(d).expr("d", v -> true).expr("d_after_et", et, after()),
            execute(() -> {})
        );

        buildAndReplay(r);   // must not throw
    }

    @Test
    public void controlDropAfterRunsWithoutNPE() {
        Variable<EvE> e1 = declarationOf(EvE.class);
        Variable<EvE> e2 = declarationOf(EvE.class);
        Variable<EvC> c  = declarationOf(EvC.class);
        Variable<EvE> et = declarationOf(EvE.class);
        Variable<EvD> d  = declarationOf(EvD.class);

        Rule r = rule("ctrl-no-after").build(
            or(
                pattern(e1).expr("e1", v -> true),
                and(
                        not(pattern(e2).expr("e2", v -> true)),
                        pattern(c).expr("c", v -> true)
                )
            ),
            pattern(et).expr("et", v -> true),
            pattern(d).expr("d", v -> true),
            execute(() -> {})
        );

        buildAndReplay(r);
    }

    @Test
    public void controlDropOrRunsWithoutNPE() {
        Variable<EvE> e2 = declarationOf(EvE.class);
        Variable<EvC> c  = declarationOf(EvC.class);
        Variable<EvE> et = declarationOf(EvE.class);
        Variable<EvD> d  = declarationOf(EvD.class);

        Rule r = rule("ctrl-no-or").build(
            and(
                    not(pattern(e2).expr("e2", v -> true)),
                    pattern(c).expr("c", v -> true)
            ),
            pattern(et).expr("et", v -> true),
            pattern(d).expr("d", v -> true).expr("d_after_et", et, after()),
            execute(() -> {})
        );

        buildAndReplay(r);
    }
}
