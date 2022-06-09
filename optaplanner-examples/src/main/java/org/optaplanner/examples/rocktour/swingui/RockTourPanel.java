package org.optaplanner.examples.rocktour.swingui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.examples.rocktour.persistence.RockTourXlsxFileIO;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class RockTourPanel extends SolutionPanel<RockTourSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/rocktour/swingui/rockTourLogo.png";

    private RockTourWorldPanel rockTourWorldPanel;

    public RockTourPanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton button = new JButton("Show in LibreOffice or Excel");
        button.addActionListener(event -> {
            SolutionFileIO<RockTourSolution> solutionFileIO = new RockTourXlsxFileIO();
            File tempFile;
            try {
                tempFile = File.createTempFile(solutionBusiness.getSolutionFileName(),
                        "." + solutionFileIO.getOutputFileExtension());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create temp file.", e);
            }
            solutionFileIO.write(solutionBusiness.getSolution(), tempFile);
            try {
                Desktop.getDesktop().open(tempFile);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to show temp file (" + tempFile + ") in LibreOffice or Excel.", e);
            }
        });
        buttonPanel.add(button);
        buttonPanel.add(new JLabel("Changes to that file are ignored unless you explicitly save it there and open it here."));
        add(buttonPanel, BorderLayout.NORTH);
        rockTourWorldPanel = new RockTourWorldPanel(this);
        add(rockTourWorldPanel, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(RockTourSolution solution) {
        rockTourWorldPanel.resetPanel(solution);
    }

    @Override
    public void updatePanel(RockTourSolution solution) {
        rockTourWorldPanel.updatePanel(solution);
    }

}
