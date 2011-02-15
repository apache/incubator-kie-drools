/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.nurserostering.solver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.calculator.DefaultHardAndSoftConstraintScoreCalculator;
import org.drools.planner.examples.common.app.LoggingTest;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.persistence.NurseRosteringDaoImpl;
import org.drools.planner.examples.nurserostering.solver.move.NurseRosteringMoveHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class NurseRosteringScoreRulesTest extends LoggingTest {

    @Test
    public void switchEmployeeAndUndo() {
        LocalSearchSolverScope localSearchSolverScope = new LocalSearchSolverScope();
        localSearchSolverScope.setRuleBase(buildRuleBase());
        localSearchSolverScope.setWorkingScoreCalculator(new DefaultHardAndSoftConstraintScoreCalculator());
        NurseRoster nurseRoster = (NurseRoster) new NurseRosteringDaoImpl().readSolution(getClass().getResourceAsStream(
                "/org/drools/planner/examples/nurserostering/data/testNurseRosteringScoreRules.xml"));
        localSearchSolverScope.setWorkingSolution(nurseRoster);
        WorkingMemory workingMemory = localSearchSolverScope.getWorkingMemory();

        Score firstScore = localSearchSolverScope.calculateScoreFromWorkingMemory();
        // do AssignmentSwitchMove
        Employee leftEmployee = findEmployeeById(nurseRoster, 0L);
        Assignment leftAssignment = findAssignmentById(nurseRoster, 200204001L);
        assertEquals(leftEmployee, leftAssignment.getEmployee());
        Employee rightEmployee = findEmployeeById(nurseRoster, 12L);
        Assignment rightAssignment = findAssignmentById(nurseRoster, 200204002L);
        assertEquals(rightEmployee, rightAssignment.getEmployee());
        NurseRosteringMoveHelper.moveEmployee(workingMemory, leftAssignment, rightEmployee);
        NurseRosteringMoveHelper.moveEmployee(workingMemory, rightAssignment, leftEmployee);
        localSearchSolverScope.calculateScoreFromWorkingMemory();
        // undo AssignmentSwitchMove;
        NurseRosteringMoveHelper.moveEmployee(workingMemory, rightAssignment, rightEmployee);
        NurseRosteringMoveHelper.moveEmployee(workingMemory, leftAssignment, leftEmployee);
        Score secondScore = localSearchSolverScope.calculateScoreFromWorkingMemory();
        assertEquals(firstScore, secondScore);
    }

    private RuleBase buildRuleBase() {
        PackageBuilder packageBuilder = new PackageBuilder();
        InputStream scoreDrlIn = getClass().getResourceAsStream("/org/drools/planner/examples/nurserostering/solver/nurseRosteringScoreRules.drl");
        try {
            packageBuilder.addPackageFromDrl(new InputStreamReader(scoreDrlIn, "utf-8"));
        } catch (DroolsParserException e) {
            throw new IllegalArgumentException("scoreDrl could not be loaded.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("scoreDrl could not be loaded.", e);
        } finally {
            IOUtils.closeQuietly(scoreDrlIn);
        }
        RuleBaseConfiguration ruleBaseConfiguration = new RuleBaseConfiguration();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase(ruleBaseConfiguration);
        if (packageBuilder.hasErrors()) {
            throw new IllegalStateException("There are errors in the scoreDrl:"
                    + packageBuilder.getErrors().toString());
        }
        ruleBase.addPackage(packageBuilder.getPackage());
        return ruleBase;
    }

    private Employee findEmployeeById(NurseRoster nurseRoster, long id) {
        for (Employee employee : nurseRoster.getEmployeeList()) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        throw new IllegalArgumentException("Invalid id (" + id + ")");
    }

    private Assignment findAssignmentById(NurseRoster nurseRoster, long id) {
        for (Assignment assignment : nurseRoster.getAssignmentList()) {
            if (assignment.getId() == id) {
                return assignment;
            }
        }
        throw new IllegalArgumentException("Invalid id (" + id + ")");
    }

}
