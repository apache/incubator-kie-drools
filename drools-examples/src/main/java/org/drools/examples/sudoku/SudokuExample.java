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
package org.drools.examples.sudoku;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.drools.examples.sudoku.swing.SudokuGridSamples;
import org.drools.examples.sudoku.swing.SudokuGridView;
import org.drools.util.IoUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example shows how Drools can be used to solve a 9x9 Sudoku Grid.
 * This Class hooks together the GUI and the model and allows you to 
 * load different grids.
 * &lt;pgt;
 * Several grids are provided via File -> Samples.
 * &lt;pgt;
 * For loading a grid from a file, prepare a text file containing nine
 * text lines as shown below and select File -> Open...
 * &lt;pgt;
 * &lt;pre&gt;
 *        95
 *         1
 *  3  752 8
 *  7 3  9 4
 *  8  5   2
 * 6  814  7
 * 5  1     
 * 49 5  8 6
 *   8 4 7 3
 * &lt;/pre&gt;
 */
public class SudokuExample implements ActionListener {
    private static final Logger LOG = LoggerFactory.getLogger(SudokuExample.class);
    private JFrame mainFrame;
    private SudokuGridView sudokuGridView;
    private Sudoku sudoku;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu samplesMenu = new JMenu("Samples");
    private JMenuItem openMenuItem = new JMenuItem("Open...");
    private JMenuItem exitMenuItem = new JMenuItem("Exit");
    private BorderLayout borderLayout = new BorderLayout();
    private FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
    private JPanel buttonPanel = new JPanel(flowLayout);
    private JButton solveButton = new JButton("Solve");
    private JButton stepButton  = new JButton("Step");
    private JButton dumpButton  = new JButton("Dump");
    private JFileChooser fileChooser;

    public static void main(String[] args) {
        KieContainer kc = createSudokuKieContainer();
        new SudokuExample().init(kc, true);
    }

    public static KieContainer createSudokuKieContainer() {
        // Create a KieContainer separately from other examples because Sudoku rules were written without property reactivity
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/org/drools/examples/sudoku/sudoku.drl",
                  ks.getResources().newInputStreamResource(SudokuExample.class.getResourceAsStream("/org/drools/examples/sudoku/sudoku.drl")));
        kfs.write("src/main/resources/org/drools/examples/sudoku/validate.drl",
                  ks.getResources().newInputStreamResource(SudokuExample.class.getResourceAsStream("/org/drools/examples/sudoku/validate.drl")));
        ReleaseId releaseId = ks.newReleaseId("org.drools.examples.sudoku", "sudoku", "1.0.0");
        kfs.generateAndWritePomXML(releaseId);
        KieModuleModel kieModuleModel = ks.newKieModuleModel();
        kieModuleModel.setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALLOWED.name());
        kfs.writeKModuleXML(kieModuleModel.toXML());
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
            LOG.error("Build error : {}", kieBuilder.getResults().getMessages());
        }
        return ks.newKieContainer(releaseId);
    }

    public SudokuExample() {
    }

    public void init(KieContainer kc, boolean exitOnClose) {
        mainFrame = new JFrame("Sudoku Example");
        for (String sampleName : SudokuGridSamples.getInstance().getSampleNames()){
            JMenuItem menuItem = new JMenuItem(sampleName);
            menuItem.addActionListener(this);
            samplesMenu.add(menuItem);
        }
        fileMenu.add(samplesMenu);
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        mainFrame.setJMenuBar(menuBar);
        sudokuGridView = new SudokuGridView();

        sudoku = new Sudoku( kc );

        mainFrame.setLayout(borderLayout);
        mainFrame.add(BorderLayout.CENTER, sudokuGridView);

        buttonPanel.add(solveButton);
        solveButton.addActionListener(this);
        buttonPanel.add(stepButton);
        stepButton.addActionListener(this);  
        buttonPanel.add(dumpButton);
        buttonsActive( false );
        dumpButton.addActionListener(this);
        mainFrame.add(BorderLayout.SOUTH, buttonPanel);
        mainFrame.setSize(400,400);
        mainFrame.setLocationRelativeTo(null); // Center in screen
        mainFrame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setResizable( false );
        mainFrame.setVisible(true);
        sudokuGridView.setModel(sudoku);
    }

    private void buttonsActive(boolean active) {
        solveButton.setEnabled(active);
        stepButton.setEnabled(active);
        dumpButton.setEnabled(active);
    }


    private void runFile(String path){
        Integer[][] values = new Integer[9][];
        try (Reader fileIsReader = new InputStreamReader(new FileInputStream(path), IoUtils.UTF8_CHARSET);
             BufferedReader rdr = new BufferedReader(fileIsReader)) {
            String line = rdr.readLine();
            for( int iRow = 0; iRow < 9;  iRow++ ){
                values[iRow] = new Integer[9];
                for( int iCol = 0; iCol < 9; iCol++ ){
                    if( line != null && line.length() > iCol ){
                        char c = line.charAt( iCol );
                        if( '1' <= c && c <= '9' ){
                            values[iRow][iCol] = c - '0';
                        }
                    }
                }
                line = rdr.readLine();
            }
            sudoku.setCellValues( values );
            sudoku.validate();
        } catch ( IOException e ) {
            LOG.error("Exception", e);
        }
    }

    public void actionPerformed(ActionEvent ev){
        if (ev.getSource().equals(solveButton) ) {
            sudoku.solve();
            buttonsActive(false);
            if (!sudoku.isSolved()) {
                sudoku.dumpGrid();
                System.out.println( "Sorry - can't solve this grid." );
            }

        } else if (ev.getSource().equals(stepButton)) {
            sudoku.step();
            if (sudoku.isSolved() || sudoku.isUnsolvable()) buttonsActive(false);
            if (sudoku.isUnsolvable()) {
                sudoku.dumpGrid();
                System.out.println( "Sorry - can't solve this grid." );
            }

        } else if (ev.getSource().equals(dumpButton)) {
            sudoku.dumpGrid();

        } else if (ev.getSource().equals(openMenuItem)) {
            if( fileChooser == null ){
                fileChooser = new JFileChooser();
            }

            try {
                if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getCanonicalPath();
                    System.out.println(path);
                    runFile(path);
                    buttonsActive(true);
                }
            } catch (IOException ex) {
                LOG.error("Exception", ex);
            }

        } else if (ev.getSource().equals(exitMenuItem)) {
            if (mainFrame.getDefaultCloseOperation() == WindowConstants.EXIT_ON_CLOSE) {
                System.exit(0);
            } else {
                mainFrame.dispose();
            }

        } else if (ev.getSource() instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) ev.getSource();
            Integer[][] sample = SudokuGridSamples.getInstance().getSample(menuItem.getText());
            sudoku.setCellValues(sample);
            sudoku.validate();
            buttonsActive(true);
        }
    }
}
