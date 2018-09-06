/*
 * Copyright 2005 JBoss Inc
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

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.List;
import java.util.function.BiPredicate;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;

public class CompiledDTTest implements BiPredicate<EvaluationContext, Object> {

    private final BiPredicate<EvaluationContext, Object> predicate;
    private final String name;

    public CompiledDTTest(CompiledFEELUnaryTests test) {
        this.predicate = toPredicate(test);
        this.name = test.getClass().getSimpleName();
    }

    @Override
    public boolean test( EvaluationContext evalCtx, Object obj ) {
        return predicate.test( evalCtx, obj );
    }

    public String getName() {
        return name;
    }

    private static BiPredicate<EvaluationContext, Object> toPredicate(CompiledFEELUnaryTests test) {
        List<UnaryTest> fs = test.getUnaryTests();
        return fs.size() == 1 ? new UnaryPredicate(fs.get(0)) : (a,b) -> fs.stream().map( UnaryPredicate::new ).anyMatch( f -> f.test( a,b ) );
    }

    public static class UnaryPredicate implements BiPredicate<EvaluationContext, Object> {
        private final UnaryTest unaryTest;

        public UnaryPredicate( UnaryTest unaryTest ) {
            this.unaryTest = unaryTest;
        }

        @Override
        public boolean test( EvaluationContext evalCtx, Object obj ) {
            return unaryTest.apply( evalCtx, obj ) == Boolean.TRUE;
        }
    }
}
