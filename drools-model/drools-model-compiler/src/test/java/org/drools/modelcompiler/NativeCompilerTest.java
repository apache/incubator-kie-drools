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

package org.drools.modelcompiler;

import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.memorycompiler.JavaConfiguration;

import static org.junit.Assert.assertEquals;

public class NativeCompilerTest extends BaseModelTest {

    public NativeCompilerTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test(timeout = 5000)
    public void testPropertyReactivity() {
        // DROOLS-6580
        // Since ecj is transitively imported by drools-compiler (we may want to review this with drools 8)
        // by default the executable model compiler always use it. This test also tries it with the native compiler.

        JavaConfiguration.CompilerType defaultCompiler = JavaDialectConfiguration.getDefaultCompilerType();
        JavaDialectConfiguration.setDefaultCompilerType(JavaConfiguration.CompilerType.NATIVE);

        try {
            String str =
                    "import " + Person.class.getCanonicalName() + ";" +
                    "rule R when\n" +
                    "  $p : Person(name == \"Mario\")\n" +
                    "then\n" +
                    "  modify($p) { setAge($p.getAge()+1) }\n" +
                    "end";

            KieSession ksession = getKieSession(str);

            Person me = new Person("Mario", 40);
            ksession.insert(me);
            ksession.fireAllRules();

            assertEquals(41, me.getAge());
        } finally {
            JavaDialectConfiguration.setDefaultCompilerType(defaultCompiler);
        }
    }
}
