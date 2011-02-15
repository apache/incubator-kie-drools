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

package org.drools.planner.examples.examination.solver;

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
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.domain.Room;
import org.drools.planner.examples.examination.persistence.ExaminationDaoImpl;
import org.drools.planner.examples.examination.solver.move.ExaminationMoveHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExaminationScoreRulesTest extends LoggingTest {

    @Test
    public void moveRoom() {
        LocalSearchSolverScope localSearchSolverScope = new LocalSearchSolverScope();
        localSearchSolverScope.setRuleBase(buildRuleBase());
        localSearchSolverScope.setWorkingScoreCalculator(new DefaultHardAndSoftConstraintScoreCalculator());
        Examination examination = (Examination) new ExaminationDaoImpl().readSolution(getClass().getResourceAsStream(
                "/org/drools/planner/examples/examination/data/testExaminationScoreRules.xml"));
        localSearchSolverScope.setWorkingSolution(examination);
        WorkingMemory workingMemory = localSearchSolverScope.getWorkingMemory();

        localSearchSolverScope.calculateScoreFromWorkingMemory();
        // do RoomChangeMove
        Exam exam = findExamById(examination, 123L);
        Room room = findRoomById(examination, 0L);
        ExaminationMoveHelper.moveRoom(workingMemory, exam, room);
        Score score = localSearchSolverScope.calculateScoreFromWorkingMemory();
        localSearchSolverScope.assertWorkingScore(score);
    }

    private RuleBase buildRuleBase() {
        PackageBuilder packageBuilder = new PackageBuilder();
        InputStream scoreDrlIn = getClass().getResourceAsStream("/org/drools/planner/examples/examination/solver/examinationScoreRules.drl");
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

    private Exam findExamById(Examination examination, long id) {
        for (Exam exam : examination.getExamList()) {
            if (exam.getId() == id) {
                return exam;
            }
        }
        throw new IllegalArgumentException("Invalid id (" + id + ")");
    }

    private Room findRoomById(Examination examination, long id) {
        for (Room room : examination.getRoomList()) {
            if (room.getId() == id) {
                return room;
            }
        }
        throw new IllegalArgumentException("Invalid id (" + id + ")");
    }

}
