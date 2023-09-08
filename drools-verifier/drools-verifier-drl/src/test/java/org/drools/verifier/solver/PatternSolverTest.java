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
package org.drools.verifier.solver;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.data.VerifierComponent;

public class PatternSolverTest {

    /**
     * <pre>
     *      and
     *     /   \
     *  descr  descr2
     * </pre>
     * 
     * result:<br>
     * descr && descr2
     */
    @Test
    void testAddBasicAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).size()).isEqualTo(2);
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr descr2
     * </pre>
     * 
     * result:<br>
     * descr<br>
     * or<br>
     * descr2
     */
    @Test
    void testAddBasicOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).size()).isEqualTo(1);
        assertThat(list.get(1).size()).isEqualTo(1);
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr  and
     *         / \
     *    descr2 descr3
     * </pre>
     * 
     * result:<br>
     * descr <br>
     * or<br>
     * descr2 && descr3
     */
    @Test
    void testAddOrAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction);
        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction2);
        solver.add(literalRestriction3);
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(2);

        assertThat(list.get(0).size()).isEqualTo(1);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(2);
        assertThat(list.get(1).contains(literalRestriction2)).isTrue();
        assertThat(list.get(1).contains(literalRestriction3)).isTrue();
    }

    /**
     * <pre>
     *       and
     *      /  \
     *  descr   or
     *         / \
     *    descr2 descr3
     * </pre>
     * 
     * result:<br>
     * descr && descr2 <br>
     * or<br>
     * descr && descr3
     */
    @Test
    void testAddAndOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction);
        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction2);
        solver.add(literalRestriction3);
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(2);

        assertThat(list.get(0).size()).isEqualTo(2);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();
        assertThat(list.get(0).contains(literalRestriction2)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(2);
        assertThat(list.get(1).contains(literalRestriction)).isTrue();
        assertThat(list.get(1).contains(literalRestriction3)).isTrue();
    }

    /**
     * <pre>
     *            and
     *         /        \
     *       or          or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr3<br>
     * or<br>
     * descr && descr4<br>
     * or<br>
     * descr2 && descr3<br>
     * or<br>
     * descr2 && descr4
     */
    @Test
    void testAddAndOrOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.AND);
        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(4);

        assertThat(list.get(0).size()).isEqualTo(2);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();
        assertThat(list.get(0).contains(literalRestriction3)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(2);
        assertThat(list.get(1).contains(literalRestriction)).isTrue();
        assertThat(list.get(1).contains(literalRestriction4)).isTrue();

        assertThat(list.get(2).size()).isEqualTo(2);
        assertThat(list.get(2).contains(literalRestriction2)).isTrue();
        assertThat(list.get(2).contains(literalRestriction3)).isTrue();

        assertThat(list.get(3).size()).isEqualTo(2);
        assertThat(list.get(3).contains(literalRestriction2)).isTrue();
        assertThat(list.get(3).contains(literalRestriction4)).isTrue();
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         and
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3 && descr4
     */
    @Test
    void testAddOrAndAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.OR);
        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(2);

        assertThat(list.get(0).size()).isEqualTo(2);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();
        assertThat(list.get(0).contains(literalRestriction2)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(2);
        assertThat(list.get(1).contains(literalRestriction3)).isTrue();
        assertThat(list.get(1).contains(literalRestriction4)).isTrue();
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3<br>
     * or<br>
     * descr4
     */
    @Test
    void testAddOrAndOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.OR);
        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(3);

        assertThat(list.get(0).size()).isEqualTo(2);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();
        assertThat(list.get(0).contains(literalRestriction2)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(1);
        assertThat(list.get(1).contains(literalRestriction3)).isTrue();

        assertThat(list.get(2).size()).isEqualTo(1);
        assertThat(list.get(2).contains(literalRestriction4)).isTrue();
    }

    /**
     * <pre>
     *                   and
     *          /         |      \
     *       and         or       descr5
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2 && descr3 && descr5<br>
     * or<br>
     * descr && descr2 && descr4 && descr5<br>
     */
    @Test
    void testAddOrAndOrDescr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern,
                "");
        LiteralRestriction literalRestriction5 = LiteralRestriction.createRestriction(pattern,
                "");

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator(OperatorDescrType.AND);
        solver.addOperator(OperatorDescrType.AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OperatorDescrType.OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.add(literalRestriction5);
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertThat(list.size()).isEqualTo(2);

        assertThat(list.get(0).size()).isEqualTo(4);
        assertThat(list.get(0).contains(literalRestriction)).isTrue();
        assertThat(list.get(0).contains(literalRestriction2)).isTrue();
        assertThat(list.get(0).contains(literalRestriction3)).isTrue();
        assertThat(list.get(0).contains(literalRestriction5)).isTrue();

        assertThat(list.get(1).size()).isEqualTo(4);
        assertThat(list.get(1).contains(literalRestriction)).isTrue();
        assertThat(list.get(1).contains(literalRestriction2)).isTrue();
        assertThat(list.get(1).contains(literalRestriction4)).isTrue();
        assertThat(list.get(1).contains(literalRestriction4)).isTrue();
    }
}
