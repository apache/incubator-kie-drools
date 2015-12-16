/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.solver;

import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;

/**
 * Takes a list of Constraints and makes possibilities from them.
 */
class PatternSolver extends Solver {

    private Pattern pattern;

    public PatternSolver(Pattern pattern) {
        super( OperatorDescrType.OR );
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
