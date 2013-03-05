/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.domain.solution.cloner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drools.planner.api.domain.solution.cloner.SolutionCloner;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.drools.planner.core.testdata.domain.TestdataSolution;
import org.drools.planner.core.testdata.domain.TestdataValue;
import org.drools.planner.core.testdata.domain.chained.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.chained.TestdataChainedEntity;
import org.drools.planner.core.testdata.domain.chained.TestdataChainedObject;
import org.drools.planner.core.testdata.domain.chained.TestdataChainedSolution;
import org.drools.planner.core.testdata.domain.setbased.TestdataSetBasedEntity;
import org.drools.planner.core.testdata.domain.setbased.TestdataSetBasedSolution;
import org.junit.Test;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;

public class FieldAccessingSolutionClonerTest extends AbstractSolutionClonerTest {

    @Override
    protected <Sol extends Solution> FieldAccessingSolutionCloner<Sol> createSolutionCloner(
            SolutionDescriptor solutionDescriptor) {
        return new FieldAccessingSolutionCloner<Sol>(solutionDescriptor);
    }

}
