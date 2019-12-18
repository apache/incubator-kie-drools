/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.workbench.models.datamodel.rule;

import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ActionConstraintMatchTest {

    @Test
    public void extractInterpolationVariablesActionHardConstraintMatch() {
        ActionHardConstraintMatch constraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void extractInterpolationVariablesActionSoftConstraintMatch() {
        ActionSoftConstraintMatch constraintMatch = new ActionSoftConstraintMatch("foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void extractInterpolationVariablesActionMediumConstraintMatch() {
        ActionMediumConstraintMatch constraintMatch = new ActionMediumConstraintMatch("foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void extractInterpolationVariablesActionSimpleConstraintMatch() {
        ActionSimpleConstraintMatch constraintMatch = new ActionSimpleConstraintMatch("foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void extractInterpolationVariablesActionBendableHardConstraintMatch() {
        ActionBendableHardConstraintMatch constraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                  "foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void extractInterpolationVariablesActionBendableSoftConstraintMatch() {
        ActionBendableSoftConstraintMatch constraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                  "foo bar @{var1}");

        extractInterpolationVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    private void extractInterpolationVariablesAbstractActionConstraintMatch(final AbstractActionConstraintMatch constraintMatch) {
        Collection<InterpolationVariable> interpolationVariables = constraintMatch.extractInterpolationVariables();

        assertEquals(1,
                     interpolationVariables.size());
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var1",
                                                                             DataType.TYPE_OBJECT)));
    }

    @Test
    public void extractInterpolationVariablesActionMultiConstraintHardSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("foo bar @{var2}");

        ActionMultiConstraintHardSoftMatch multiConstraintHardSoftMatch = new ActionMultiConstraintHardSoftMatch(hardConstraintMatch,
                                                                                                                 softConstraintMatch);

        Collection<InterpolationVariable> interpolationVariables = multiConstraintHardSoftMatch.extractInterpolationVariables();

        assertEquals(2,
                     interpolationVariables.size());
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var1",
                                                                             DataType.TYPE_OBJECT)));
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var2",
                                                                             DataType.TYPE_OBJECT)));
    }

    @Test
    public void extractInterpolationVariablesActionMultiConstraintHardMediumSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");
        ActionMediumConstraintMatch mediumConstraintMatch = new ActionMediumConstraintMatch("foo bar @{var2}");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("foo bar @{var3}");

        ActionMultiConstraintHardMediumSoftMatch multiConstraintHardSoftMatch = new ActionMultiConstraintHardMediumSoftMatch(hardConstraintMatch,
                                                                                                                             mediumConstraintMatch,
                                                                                                                             softConstraintMatch);

        Collection<InterpolationVariable> interpolationVariables = multiConstraintHardSoftMatch.extractInterpolationVariables();

        assertEquals(3,
                     interpolationVariables.size());
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var1",
                                                                             DataType.TYPE_OBJECT)));
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var2",
                                                                             DataType.TYPE_OBJECT)));
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var3",
                                                                             DataType.TYPE_OBJECT)));
    }

    @Test
    public void extractInterpolationVariablesActionMultiConstraintBendableMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                 Arrays.asList(bendableSoftConstraintMatch));

        extractInterpolationVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    @Test
    public void extractInterpolationVariablesActionMultiConstraintBendableLongMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableLongMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableLongMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                         Arrays.asList(bendableSoftConstraintMatch));

        extractInterpolationVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    @Test
    public void extractInterpolationVariablesActionMultiConstraintBendableBigDecimalMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableBigDecimalMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableBigDecimalMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                                     Arrays.asList(bendableSoftConstraintMatch));

        extractInterpolationVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    private void extractInterpolationVariablesAbstractActionMultiConstraintBendableMatch(final AbstractActionMultiConstraintBendableMatch constraintMatch) {
        Collection<InterpolationVariable> interpolationVariables = constraintMatch.extractInterpolationVariables();

        assertEquals(2,
                     interpolationVariables.size());
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var1",
                                                                             DataType.TYPE_OBJECT)));
        assertTrue(interpolationVariables.contains(new InterpolationVariable("var2",
                                                                             DataType.TYPE_OBJECT)));
    }

    @Test
    public void substituteTemplateVariablesActionHardConstraintMatch() {
        ActionHardConstraintMatch constraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void substituteTemplateVariablesActionSoftConstraintMatch() {
        ActionSoftConstraintMatch constraintMatch = new ActionSoftConstraintMatch("foo bar @{var1}");

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void substituteTemplateVariablesActionMediumConstraintMatch() {
        ActionMediumConstraintMatch constraintMatch = new ActionMediumConstraintMatch("foo bar @{var1}");

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void substituteTemplateVariablesActionSimpleConstraintMatch() {
        ActionSimpleConstraintMatch constraintMatch = new ActionSimpleConstraintMatch("foo bar @{var1}");

        constraintMatch.substituteTemplateVariables(getKeyToValueFunction());

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void substituteTemplateVariablesActionBendableHardConstraintMatch() {
        ActionBendableHardConstraintMatch constraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                  "foo bar @{var1}");

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void substituteTemplateVariablesActionBendableSoftConstraintMatch() {
        ActionBendableSoftConstraintMatch constraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                  "foo bar @{var1}");

        substituteTemplateVariablesAbstractActionConstraintMatch(constraintMatch);
    }

    private void substituteTemplateVariablesAbstractActionConstraintMatch(final AbstractActionConstraintMatch constraintMatch) {
        constraintMatch.substituteTemplateVariables(getKeyToValueFunction());

        assertEquals("foo bar val1",
                     constraintMatch.getConstraintMatch());
    }

    @Test
    public void substituteTemplateVariablesActionMultiConstraintHardSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("foo bar @{var2}");

        ActionMultiConstraintHardSoftMatch multiConstraintHardSoftMatch = new ActionMultiConstraintHardSoftMatch(hardConstraintMatch,
                                                                                                                 softConstraintMatch);

        multiConstraintHardSoftMatch.substituteTemplateVariables(getKeyToValueFunction());

        assertEquals("foo bar val1",
                     multiConstraintHardSoftMatch.getActionHardConstraintMatch().getConstraintMatch());
        assertEquals("foo bar val2",
                     multiConstraintHardSoftMatch.getActionSoftConstraintMatch().getConstraintMatch());
    }

    @Test
    public void substituteTemplateVariablesActionMultiConstraintHardMediumSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("foo bar @{var1}");
        ActionMediumConstraintMatch mediumConstraintMatch = new ActionMediumConstraintMatch("foo bar @{var2}");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("foo bar @{var3}");

        ActionMultiConstraintHardMediumSoftMatch multiConstraintHardSoftMatch = new ActionMultiConstraintHardMediumSoftMatch(hardConstraintMatch,
                                                                                                                             mediumConstraintMatch,
                                                                                                                             softConstraintMatch);

        multiConstraintHardSoftMatch.substituteTemplateVariables(getKeyToValueFunction());

        assertEquals("foo bar val1",
                     multiConstraintHardSoftMatch.getActionHardConstraintMatch().getConstraintMatch());
        assertEquals("foo bar val2",
                     multiConstraintHardSoftMatch.getActionMediumConstraintMatch().getConstraintMatch());
        assertEquals("foo bar val3",
                     multiConstraintHardSoftMatch.getActionSoftConstraintMatch().getConstraintMatch());
    }

    @Test
    public void substituteTemplateVariablesActionMultiConstraintBendableMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                 Arrays.asList(bendableSoftConstraintMatch));

        substituteTemplateVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    @Test
    public void substituteTemplateVariablesActionMultiConstraintBendableLongMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableLongMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableLongMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                         Arrays.asList(bendableSoftConstraintMatch));

        substituteTemplateVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    @Test
    public void substituteTemplateVariablesActionMultiConstraintBendableBigDecimalMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "foo bar @{var1}");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "foo bar @{var2}");

        ActionMultiConstraintBendableBigDecimalMatch multiConstraintBendableMatch = new ActionMultiConstraintBendableBigDecimalMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                                     Arrays.asList(bendableSoftConstraintMatch));

        substituteTemplateVariablesAbstractActionMultiConstraintBendableMatch(multiConstraintBendableMatch);
    }

    private void substituteTemplateVariablesAbstractActionMultiConstraintBendableMatch(final AbstractActionMultiConstraintBendableMatch constraintMatch) {
        constraintMatch.substituteTemplateVariables(getKeyToValueFunction());

        assertNotNull(constraintMatch.getActionBendableHardConstraintMatches());
        assertEquals("foo bar val1",
                     constraintMatch.getActionBendableHardConstraintMatches().get(0).getConstraintMatch());

        assertNotNull(constraintMatch.getActionBendableSoftConstraintMatches());
        assertEquals("foo bar val2",
                     constraintMatch.getActionBendableSoftConstraintMatches().get(0).getConstraintMatch());
    }

    private Function<String, String> getKeyToValueFunction() {
        return s -> {
            switch (s) {
                case "var1":
                    return "val1";
                case "var2":
                    return "val2";
                case "var3":
                    return "val3";
                default:
                    throw new IllegalArgumentException("Undefined variable " + s);
            }
        };
    }

    @Test
    public void cloneIActionBendableHardConstraintMatchPlugin() {
        ActionBendableHardConstraintMatch constraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                  "test");

        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void cloneIActionBendableSoftConstraintMatchPlugin() {
        ActionBendableSoftConstraintMatch constraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                  "test");
        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void cloneIActionHardConstraintMatchPlugin() {
        ActionHardConstraintMatch constraintMatch = new ActionHardConstraintMatch("test");

        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void cloneIActionSoftConstraintMatchPlugin() {
        ActionSoftConstraintMatch constraintMatch = new ActionSoftConstraintMatch("test");

        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void cloneIActionMediumConstraintMatchPlugin() {
        ActionMediumConstraintMatch constraintMatch = new ActionMediumConstraintMatch("test");

        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    @Test
    public void cloneIActionSimpleConstraintMatchPlugin() {
        ActionSimpleConstraintMatch constraintMatch = new ActionSimpleConstraintMatch("test");

        cloneIActionAbstractActionConstraintMatch(constraintMatch);
    }

    private void cloneIActionAbstractActionConstraintMatch(final AbstractActionConstraintMatch constraintMatch) {
        IAction constraintMatchClone = (IAction) constraintMatch.cloneTemplateAware();

        assertNotNull(constraintMatchClone);
        assertNotSame(constraintMatchClone,
                      constraintMatch);

        assertEquals("test",
                     constraintMatch.getConstraintMatch());
    }

    @Test
    public void cloneIActionMultiConstraintBendableBigDecimalMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "hard");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "soft");

        ActionMultiConstraintBendableBigDecimalMatch constraintMatch = new ActionMultiConstraintBendableBigDecimalMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                                        Arrays.asList(bendableSoftConstraintMatch));

        cloneIActionAbstractActionMultiConstraintBendableMatch(constraintMatch);
    }

    @Test
    public void cloneIActionMultiConstraintBendableLongMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "hard");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "soft");

        ActionMultiConstraintBendableLongMatch constraintMatch = new ActionMultiConstraintBendableLongMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                            Arrays.asList(bendableSoftConstraintMatch));

        cloneIActionAbstractActionMultiConstraintBendableMatch(constraintMatch);
    }

    @Test
    public void cloneIActionMultiConstraintBendableMatch() {
        ActionBendableHardConstraintMatch bendableHardConstraintMatch = new ActionBendableHardConstraintMatch(0,
                                                                                                              "hard");
        ActionBendableSoftConstraintMatch bendableSoftConstraintMatch = new ActionBendableSoftConstraintMatch(0,
                                                                                                              "soft");

        ActionMultiConstraintBendableMatch constraintMatch = new ActionMultiConstraintBendableMatch(Arrays.asList(bendableHardConstraintMatch),
                                                                                                    Arrays.asList(bendableSoftConstraintMatch));

        cloneIActionAbstractActionMultiConstraintBendableMatch(constraintMatch);
    }

    private void cloneIActionAbstractActionMultiConstraintBendableMatch(final AbstractActionMultiConstraintBendableMatch constraintMatch) {
        IAction constraintMatchClone = (IAction) constraintMatch.cloneTemplateAware();

        assertNotNull(constraintMatchClone);
        assertNotSame(constraintMatchClone,
                      constraintMatch);

        assertNotNull(constraintMatch.getActionBendableHardConstraintMatches());
        assertNotNull(constraintMatch.getActionBendableSoftConstraintMatches());

        assertEquals("hard",
                     constraintMatch.getActionBendableHardConstraintMatches().get(0).getConstraintMatch());
        assertEquals("soft",
                     constraintMatch.getActionBendableSoftConstraintMatches().get(0).getConstraintMatch());
    }

    @Test
    public void cloneIActionMultiConstraintHardMediumSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("hard");
        ActionMediumConstraintMatch mediumConstraintMatch = new ActionMediumConstraintMatch("medium");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("soft");

        ActionMultiConstraintHardMediumSoftMatch constraintMatch = new ActionMultiConstraintHardMediumSoftMatch(hardConstraintMatch,
                                                                                                                mediumConstraintMatch,
                                                                                                                softConstraintMatch);

        IAction constraintMatchClone = (IAction) constraintMatch.cloneTemplateAware();

        assertNotNull(constraintMatchClone);
        assertNotSame(constraintMatchClone,
                      constraintMatch);

        assertNotNull(constraintMatch.getActionHardConstraintMatch());
        assertNotNull(constraintMatch.getActionMediumConstraintMatch());
        assertNotNull(constraintMatch.getActionSoftConstraintMatch());

        assertEquals("hard",
                     constraintMatch.getActionHardConstraintMatch().getConstraintMatch());
        assertEquals("medium",
                     constraintMatch.getActionMediumConstraintMatch().getConstraintMatch());
        assertEquals("soft",
                     constraintMatch.getActionSoftConstraintMatch().getConstraintMatch());
    }

    @Test
    public void cloneIActionMultiConstraintHardSoftMatch() {
        ActionHardConstraintMatch hardConstraintMatch = new ActionHardConstraintMatch("hard");
        ActionSoftConstraintMatch softConstraintMatch = new ActionSoftConstraintMatch("soft");

        ActionMultiConstraintHardSoftMatch constraintMatch = new ActionMultiConstraintHardSoftMatch(hardConstraintMatch,
                                                                                                    softConstraintMatch);

        IAction constraintMatchClone = (IAction) constraintMatch.cloneTemplateAware();

        assertNotNull(constraintMatchClone);
        assertNotSame(constraintMatchClone,
                      constraintMatch);

        assertNotNull(constraintMatch.getActionHardConstraintMatch());
        assertNotNull(constraintMatch.getActionSoftConstraintMatch());

        assertEquals("hard",
                     constraintMatch.getActionHardConstraintMatch().getConstraintMatch());
        assertEquals("soft",
                     constraintMatch.getActionSoftConstraintMatch().getConstraintMatch());
    }

    @Test
    public void marshalActionSoftConstraintMatch() {
        ActionSoftConstraintMatch action = new ActionSoftConstraintMatch("-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addSoftConstraintMatch(kcontext, -1)",
                     marshaledAction);
    }

    @Test
    public void marshalActionBendableSoftConstraintMatch() {
        ActionBendableSoftConstraintMatch action = new ActionBendableSoftConstraintMatch(1,
                                                                                         "-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addSoftConstraintMatch(kcontext, 1, -1)",
                     marshaledAction);
    }

    @Test
    public void marshalActionMediumConstraintMatch() {
        ActionMediumConstraintMatch action = new ActionMediumConstraintMatch("-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMediumConstraintMatch(kcontext, -1)",
                     marshaledAction);
    }

    @Test
    public void marshalActionMultiConstraintHardSoftMatch() {
        ActionMultiConstraintHardSoftMatch action = new ActionMultiConstraintHardSoftMatch(new ActionHardConstraintMatch("-1"),
                                                                                           new ActionSoftConstraintMatch("-2"));

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMultiConstraintMatch(kcontext, -1, -2)",
                     marshaledAction);
    }

    @Test
    public void marshalActionMultiConstraintHardMediumSoftMatch() {
        ActionMultiConstraintHardMediumSoftMatch action = new ActionMultiConstraintHardMediumSoftMatch(new ActionHardConstraintMatch("-1"),
                                                                                                       new ActionMediumConstraintMatch("-2"),
                                                                                                       new ActionSoftConstraintMatch("-3"));

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMultiConstraintMatch(kcontext, -1, -2, -3)",
                     marshaledAction);
    }

    @Test
    public void marshalActionMultiConstraintBendableMatch() {
        ActionMultiConstraintBendableMatch action = new ActionMultiConstraintBendableMatch(Arrays.asList(new ActionBendableHardConstraintMatch(0,
                                                                                                                                               "-1"),
                                                                                                         new ActionBendableHardConstraintMatch(1,
                                                                                                                                               "-2")),
                                                                                           Arrays.asList(new ActionBendableSoftConstraintMatch(0,
                                                                                                                                               "-3"),
                                                                                                         new ActionBendableSoftConstraintMatch(1,
                                                                                                                                               "-4")));

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMultiConstraintMatch(kcontext, new int[] {-1, -2}, new int[] {-3, -4})",
                     marshaledAction);
    }

    @Test
    public void marshalActionMultiConstraintBendableLongMatch() {
        ActionMultiConstraintBendableLongMatch action = new ActionMultiConstraintBendableLongMatch(Arrays.asList(new ActionBendableHardConstraintMatch(0,
                                                                                                                                                       "-1l"),
                                                                                                                 new ActionBendableHardConstraintMatch(1,
                                                                                                                                                       "-2l")),
                                                                                                   Arrays.asList(new ActionBendableSoftConstraintMatch(0,
                                                                                                                                                       "-3l"),
                                                                                                                 new ActionBendableSoftConstraintMatch(1,
                                                                                                                                                       "-4l")));

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMultiConstraintMatch(kcontext, new long[] {-1l, -2l}, new long[] {-3l, -4l})",
                     marshaledAction);
    }

    @Test
    public void marshalActionMultiConstraintBendableBigDecimalMatch() {
        ActionMultiConstraintBendableBigDecimalMatch action = new ActionMultiConstraintBendableBigDecimalMatch(Arrays.asList(new ActionBendableHardConstraintMatch(0,
                                                                                                                                                                   "new java.math.BigDecimal(-1)"),
                                                                                                                             new ActionBendableHardConstraintMatch(1,
                                                                                                                                                                   "new java.math.BigDecimal(-2)")),
                                                                                                               Arrays.asList(new ActionBendableSoftConstraintMatch(0,
                                                                                                                                                                   "new java.math.BigDecimal(-3)"),
                                                                                                                             new ActionBendableSoftConstraintMatch(1,
                                                                                                                                                                   "new java.math.BigDecimal(-4)")));

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addMultiConstraintMatch(kcontext, new java.math.BigDecimal[] {new java.math.BigDecimal(-1), new java.math.BigDecimal(-2)}, new java.math.BigDecimal[] {new java.math.BigDecimal(-3), new java.math.BigDecimal(-4)})",
                     marshaledAction);
    }

    @Test
    public void marshalActionSimpleConstraintMatch() {
        ActionSimpleConstraintMatch action = new ActionSimpleConstraintMatch("-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addConstraintMatch(kcontext, -1)",
                     marshaledAction);
    }

    @Test
    public void marshalActionHardConstraintMatch() {
        ActionHardConstraintMatch action = new ActionHardConstraintMatch("-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addHardConstraintMatch(kcontext, -1)",
                     marshaledAction);
    }

    @Test
    public void marshalActionBendableHardConstraintMatch() {
        ActionBendableHardConstraintMatch action = new ActionBendableHardConstraintMatch(1,
                                                                                         "-1");

        String marshaledAction = action.getStringRepresentation();

        assertEquals("scoreHolder.addHardConstraintMatch(kcontext, 1, -1)",
                     marshaledAction);
    }
}
