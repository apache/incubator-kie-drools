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

package org.optaplanner.examples.common.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonApp extends LoggingMain {

    protected static final Logger logger = LoggerFactory.getLogger(CommonApp.class);

    /**
     * Some examples are not compatible with every native LookAndFeel.
     * For example, NurseRosteringPanel is incompatible with Mac.
     */
    public static void fixateLookAndFeel() {
        CommonUncaughtExceptionHandler uncaughtExceptionHandler = new CommonUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        System.setProperty("sun.awt.exception.handler", CommonUncaughtExceptionHandler.class.getName());
        String lookAndFeelName = "Metal"; // "Nimbus" is nicer but incompatible
        Exception lookAndFeelException;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            lookAndFeelException = null;
        } catch (UnsupportedLookAndFeelException e) {
            lookAndFeelException = e;
        } catch (ClassNotFoundException e) {
            lookAndFeelException = e;
        } catch (InstantiationException e) {
            lookAndFeelException = e;
        } catch (IllegalAccessException e) {
            lookAndFeelException = e;
        }
        logger.warn("Could not switch to lookAndFeel (" + lookAndFeelName + "). Layout might be incorrect.",
                lookAndFeelException);

    }

    private static class CommonUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // Not logger.error() because it needs to show up red (and linked) in the IDE console
            System.err.append("Exception in thread \"").append(t.getName()).append("\" ");
            e.printStackTrace();
            displayException(t, e);
        }

        private void displayException(Thread t, Throwable e) {
            final JFrame exceptionFrame = new JFrame("Uncaught exception: " + e.getMessage());
            exceptionFrame.setIconImage(SolverAndPersistenceFrame.OPTA_PLANNER_ICON.getImage());
            exceptionFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contentPanel.add(new JLabel("An uncaught exception has occurred: "), BorderLayout.NORTH);
            JTextArea stackTraceTextArea = new JTextArea(10, 80);
            stackTraceTextArea.setEditable(false);
            stackTraceTextArea.append("Exception in thread \"" + t.getName() + "\" " + e.getClass().getName()
                    + ": " + e.getMessage() + "\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                stackTraceTextArea.append("    at " + stackTraceElement.toString() + "\n");
            }
            JScrollPane stackTraceScrollPane = new JScrollPane(stackTraceTextArea,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            contentPanel.add(stackTraceScrollPane, BorderLayout.CENTER);
            stackTraceTextArea.setCaretPosition(0); // Scroll to top
            JButton closeButton = new JButton(new AbstractAction("Close") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exceptionFrame.setVisible(false);
                    exceptionFrame.dispose();
                }
            });
            contentPanel.add(closeButton, BorderLayout.SOUTH);
            exceptionFrame.setContentPane(contentPanel);
            exceptionFrame.pack();
            exceptionFrame.setLocationRelativeTo(null);
            exceptionFrame.setVisible(true);
        }

    }

    protected final String name;
    protected final String description;
    protected final String iconResource;

    protected SolverAndPersistenceFrame solverAndPersistenceFrame;
    protected SolutionBusiness solutionBusiness;

    protected CommonApp(String name, String description, String iconResource) {
        this.name = name;
        this.description = description;
        this.iconResource = iconResource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconResource() {
        return iconResource;
    }

    public void init() {
        init(null, true);
    }

    public void init(Component centerForComponent, boolean exitOnClose) {
        solutionBusiness = createSolutionBusiness();
        solverAndPersistenceFrame = new SolverAndPersistenceFrame(
                solutionBusiness, createSolutionPanel(), name + " OptaPlanner example");
        solverAndPersistenceFrame.setDefaultCloseOperation(exitOnClose ? WindowConstants.EXIT_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
        solverAndPersistenceFrame.init(centerForComponent);
        solverAndPersistenceFrame.setVisible(true);
    }

    public SolutionBusiness createSolutionBusiness() {
        SolutionBusiness solutionBusiness = new SolutionBusiness();
        solutionBusiness.setSolutionDao(createSolutionDao());
        solutionBusiness.setImporter(createSolutionImporter());
        solutionBusiness.setExporter(createSolutionExporter());
        solutionBusiness.updateDataDirs();
        solutionBusiness.setSolver(createSolver());
        return solutionBusiness;
    }

    protected abstract Solver createSolver();

    protected abstract SolutionPanel createSolutionPanel();

    protected abstract SolutionDao createSolutionDao();

    protected AbstractSolutionImporter createSolutionImporter() {
        return null;
    }

    protected AbstractSolutionExporter createSolutionExporter() {
        return null;
    }

}
