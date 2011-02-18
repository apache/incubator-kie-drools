/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.commons.jci.compilers;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.commons.jci.problems.CompilationProblem;

/**
 * A CompilationResult represents the result of a compilation.
 * It includes errors (which failed the compilation) or warnings
 * (that can be ignored and do not affect the creation of the
 * class files)
 * 
 * @author tcurdt
 */
public final class CompilationResult {
    
    private final CompilationProblem[] errors;
    private final CompilationProblem[] warnings;
        
    public CompilationResult( final CompilationProblem[] pProblems ) {
        final Collection errorsColl = new ArrayList();
        final Collection warningsColl = new ArrayList();

        for (int i = 0; i < pProblems.length; i++) {
            final CompilationProblem problem = pProblems[i];
            if (problem.isError()) {
                errorsColl.add(problem);
            } else {
                warningsColl.add(problem);
            }
        }
        
        errors = new CompilationProblem[errorsColl.size()];
        errorsColl.toArray(errors);

        warnings = new CompilationProblem[warningsColl.size()];
        warningsColl.toArray(warnings);
    }
    
    public CompilationProblem[] getErrors() {
        final CompilationProblem[] res = new CompilationProblem[errors.length];
        System.arraycopy(errors, 0, res, 0, res.length);
        return res;
    }

    public CompilationProblem[] getWarnings() {
        final CompilationProblem[] res = new CompilationProblem[warnings.length];
        System.arraycopy(warnings, 0, res, 0, res.length);
        return res;
    }
}
