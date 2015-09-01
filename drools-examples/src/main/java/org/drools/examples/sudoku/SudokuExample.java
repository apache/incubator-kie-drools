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
package org.drools.examples.sudoku;

import org.drools.core.util.IoUtils;
import org.drools.examples.sudoku.swing.SudokuGridSamples;
import org.drools.examples.sudoku.swing.SudokuGridView;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        new SudokuExample().init(kc, true);
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
        Reader fileIsReader = null;
        BufferedReader rdr = null;
        try {
            fileIsReader = new InputStreamReader(new FileInputStream(path), IoUtils.UTF8_CHARSET);
            rdr = new BufferedReader( fileIsReader );
            String line = rdr.readLine();
            for( int iRow = 0; iRow < 9;  iRow++ ){
                values[iRow] = new Integer[9];
                for( int iCol = 0; iCol < 9; iCol++ ){
                    if( line != null && line.length() > iCol ){
                        char c = line.charAt( iCol );
                        if( '1' <= c && c <= '9' ){
                            values[iRow][iCol] = Integer.valueOf( c - '0' );
                        }
                    }
                }
                line = rdr.readLine();
            }
            sudoku.setCellValues( values );
            sudoku.validate();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if( rdr != null ) {
                try {
                    rdr.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
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
                ex.printStackTrace();
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
        } else {
            //
        }
    }
}
