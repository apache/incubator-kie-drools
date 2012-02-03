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

package org.drools.planner.examples.common.swingui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.business.SolutionBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SolutionPanel extends JPanel implements Scrollable {

    protected static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 500);

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverAndPersistenceFrame solverAndPersistenceFrame;
    protected SolutionBusiness solutionBusiness;

    public void setSolverAndPersistenceFrame(SolverAndPersistenceFrame solverAndPersistenceFrame) {
        this.solverAndPersistenceFrame = solverAndPersistenceFrame;
    }

    public void setSolutionBusiness(SolutionBusiness solutionBusiness) {
        this.solutionBusiness = solutionBusiness;
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

    public void setSolvingState(boolean solving) {
        // Hook method
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

}
