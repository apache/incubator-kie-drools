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

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SolutionPanel extends JPanel implements Scrollable {

    protected static final String USAGE_EXPLANATION_PATH = "/org/optaplanner/examples/common/swingui/exampleUsageExplanation.png";
    // Size fits into screen resolution 1024*768
    public static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 600);

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverAndPersistenceFrame solverAndPersistenceFrame;
    protected SolutionBusiness solutionBusiness;

    public SolverAndPersistenceFrame getSolverAndPersistenceFrame() {
        return solverAndPersistenceFrame;
    }

    public void setSolverAndPersistenceFrame(SolverAndPersistenceFrame solverAndPersistenceFrame) {
        this.solverAndPersistenceFrame = solverAndPersistenceFrame;
    }

    public SolutionBusiness getSolutionBusiness() {
        return solutionBusiness;
    }

    public void setSolutionBusiness(SolutionBusiness solutionBusiness) {
        this.solutionBusiness = solutionBusiness;
    }

    public String getUsageExplanationPath() {
        return USAGE_EXPLANATION_PATH;
    }

    public boolean isWrapInScrollPane() {
        return true;
    }

    public boolean isRefreshScreenDuringSolving() {
        return false;
    }

    public abstract void resetPanel(Solution solution);

    public void updatePanel(Solution solution) {
        resetPanel(solution);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

    public void doProblemFactChange(ProblemFactChange problemFactChange) {
        doProblemFactChange(problemFactChange, false);
    }

    public void doProblemFactChange(ProblemFactChange problemFactChange, boolean reset) {
        solutionBusiness.doProblemFactChange(problemFactChange);
        Solution solution = solutionBusiness.getSolution();
        if (reset) {
            resetPanel(solution);
        } else {
            updatePanel(solution);
        }
        validate();
        solverAndPersistenceFrame.refreshScoreField(solution);
    }

}
