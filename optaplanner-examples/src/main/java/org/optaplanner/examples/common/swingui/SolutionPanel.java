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

package org.optaplanner.examples.common.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolutionPanel<Solution_> extends JPanel implements Scrollable {

    protected static final String USAGE_EXPLANATION_PATH = "/org/optaplanner/examples/common/swingui/exampleUsageExplanation.png";
    // Size fits into screen resolution 1024*768
    public static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 600);

    protected static final Color[][] INDICTMENT_COLORS = {
            {TangoColorFactory.SCARLET_3, TangoColorFactory.SCARLET_1},
            {TangoColorFactory.ORANGE_3, TangoColorFactory.ORANGE_1},
            {TangoColorFactory.BUTTER_3, TangoColorFactory.BUTTER_1},
            {TangoColorFactory.CHAMELEON_3, TangoColorFactory.CHAMELEON_1},
            {TangoColorFactory.SKY_BLUE_3, TangoColorFactory.SKY_BLUE_1},
            {TangoColorFactory.PLUM_3, TangoColorFactory.PLUM_1}
    };

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverAndPersistenceFrame solverAndPersistenceFrame;
    protected SolutionBusiness<Solution_> solutionBusiness;

    protected boolean useIndictmentColor = false;
    protected TangoColorFactory normalColorFactory;
    protected double[] indictmentMinimumLevelNumbers;

    public SolverAndPersistenceFrame getSolverAndPersistenceFrame() {
        return solverAndPersistenceFrame;
    }

    public void setSolverAndPersistenceFrame(SolverAndPersistenceFrame solverAndPersistenceFrame) {
        this.solverAndPersistenceFrame = solverAndPersistenceFrame;
    }

    public SolutionBusiness<Solution_> getSolutionBusiness() {
        return solutionBusiness;
    }

    public void setSolutionBusiness(SolutionBusiness<Solution_> solutionBusiness) {
        this.solutionBusiness = solutionBusiness;
    }

    public boolean isUseIndictmentColor() {
        return useIndictmentColor;
    }

    public void setUseIndictmentColor(boolean useIndictmentColor) {
        this.useIndictmentColor = useIndictmentColor;
    }

    public String getUsageExplanationPath() {
        return USAGE_EXPLANATION_PATH;
    }

    public boolean isWrapInScrollPane() {
        return true;
    }

    public abstract void resetPanel(Solution_ solution);

    public void updatePanel(Solution_ solution) {
        resetPanel(solution);
    }

    public Solution_ getSolution() {
        return (Solution_) solutionBusiness.getSolution();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

    public boolean isIndictmentHeatMapEnabled() {
        return false;
    }

    protected void preparePlanningEntityColors(List<?> planningEntityList) {
        if (useIndictmentColor) {
            indictmentMinimumLevelNumbers = null;
            for (Object planningEntity : planningEntityList) {
                Indictment indictment = solutionBusiness.getIndictmentMap().get(planningEntity);
                if (indictment != null) {
                    Number[] levelNumbers = indictment.getScore().toLevelNumbers();
                    if (indictmentMinimumLevelNumbers == null) {
                        indictmentMinimumLevelNumbers = new double[levelNumbers.length];
                        for (int i = 0; i < levelNumbers.length; i++) {
                            indictmentMinimumLevelNumbers[i] = levelNumbers[i].doubleValue();
                        }
                    } else {
                        for (int i = 0; i < levelNumbers.length; i++) {
                            double levelNumber = levelNumbers[i].doubleValue();
                            if (levelNumber < indictmentMinimumLevelNumbers[i]) {
                                indictmentMinimumLevelNumbers[i] = levelNumber;
                            }
                        }
                    }
                }
            }
        } else {
            normalColorFactory = new TangoColorFactory();
        }
    }

    public Color determinePlanningEntityColor(Object planningEntity, Object normalColorObject) {
        if (useIndictmentColor) {
            Indictment indictment = solutionBusiness.getIndictmentMap().get(planningEntity);
            if (indictment != null) {
                Number[] levelNumbers = indictment.getScore().toLevelNumbers();
                for (int i = 0; i < levelNumbers.length; i++) {
                    if (i > INDICTMENT_COLORS.length) {
                        return TangoColorFactory.ALUMINIUM_3;
                    }
                    double levelNumber = levelNumbers[i].doubleValue();
                    if (levelNumber < 0.0) {
                        return TangoColorFactory.buildPercentageColor(
                                INDICTMENT_COLORS[i][0], INDICTMENT_COLORS[i][1],
                                1.0 - (levelNumber / indictmentMinimumLevelNumbers[i]));
                    }
                }
            }
            return Color.WHITE;
        } else {
            return normalColorFactory.pickColor(normalColorObject);
        }
    }

    public String determinePlanningEntityTooltip(Object planningEntity) {
        Indictment indictment = solutionBusiness.getIndictmentMap().get(planningEntity);
        if (indictment == null) {
            return "<html>No indictment</html>";
        }
        StringBuilder s = new StringBuilder("<html>Indictment: ").append(indictment.getScore().toShortString());
        for (ConstraintMatch constraintMatch : indictment.getConstraintMatchSet()) {
            s.append("<br/>&nbsp;&nbsp;").append(constraintMatch.getConstraintName())
                    .append(" = ").append(constraintMatch.getScore().toShortString());
        }
        s.append("</html>");
        return s.toString();
    }

    public void doProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
        doProblemFactChange(problemFactChange, false);
    }

    public void doProblemFactChange(ProblemFactChange<Solution_> problemFactChange, boolean reset) {
        solutionBusiness.doProblemFactChange(problemFactChange);
        Solution_ solution = getSolution();
        Score score = solutionBusiness.getScore();
        if (reset) {
            resetPanel(solution);
        } else {
            updatePanel(solution);
        }
        validate();
        solverAndPersistenceFrame.refreshScoreField(score);
    }

}
