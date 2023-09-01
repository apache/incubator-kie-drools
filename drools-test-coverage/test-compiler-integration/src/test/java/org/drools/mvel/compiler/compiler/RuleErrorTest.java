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
package org.drools.mvel.compiler.compiler;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.Test;
import org.kie.internal.jci.CompilationProblem;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleErrorTest {

    @Test
    public void testNewLineInMessage() {
        CompilationProblem[] probs = new CompilationProblem[3];
        probs[0] = new MockCompilationProblem();
        probs[1] = new MockCompilationProblem();
        probs[2] = new MockCompilationProblem();

        DescrBuildError err = new DescrBuildError( new RuleDescr( "ruleName" ),
                                                   new AndDescr(),
                                                   probs,
                                                   "IM IN YR EROR" );
        assertThat(err.toString()).isNotNull();
        String msg = err.getMessage();

        assertThat(msg.indexOf("IM IN YR EROR") != -1).isTrue();
        System.err.println( msg );
        assertThat(msg).isEqualTo("IM IN YR EROR problem\nproblem\nproblem");

    }

    class MockCompilationProblem
        implements
        CompilationProblem {

        public int getEndColumn() {
            return 0;
        }

        public int getEndLine() {
            return 0;
        }

        public String getFileName() {
            return "X";
        }

        public String getMessage() {
            return "problem";
        }

        public int getStartColumn() {
            return 0;
        }

        public int getStartLine() {
            return 0;
        }

        public boolean isError() {
            return true;
        }

    }

}
