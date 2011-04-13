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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.drools.KnowledgeBase;
import org.drools.examples.sudoku.rules.DroolsUtil;
import org.drools.examples.sudoku.swing.SudokuGridSamples;
import org.drools.examples.sudoku.swing.SudokuGridView;


/**
 * This example shows how Drools can be used to solve a 9x9 Sudoku Grid.
 * <p
 * This Class hooks together the GUI and the model and allows you to 
 * load different grids.
 * 
 * @version $Revision: 1.1 $
 */
public class SudokuMain
implements ActionListener
{
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
    private JFileChooser fileChooser;

    public static void main(String[] args) throws Exception{
        @SuppressWarnings("unused")
        SudokuMain sudokuMain = new SudokuMain();
    }

    public SudokuMain() throws Exception{
        mainFrame = new JFrame("Drools Sudoku Example");
        for (String sampleName : SudokuGridSamples.getInstance().getSampleNames()){
            JMenuItem menuItem = new JMenuItem(sampleName);
            menuItem.addActionListener(this);
            samplesMenu.add(menuItem);
        }
        fileMenu.add(samplesMenu);
        openMenuItem.addActionListener(this);
        // fileMenu.add(openMenuItem);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        mainFrame.setJMenuBar(menuBar);
        sudokuGridView = new SudokuGridView();

        KnowledgeBase kBase = DroolsUtil.readKnowledgeBase( "/org/drools/examples/sudoku/sudoku.drl" );
        sudoku = new Sudoku( kBase );

        mainFrame.setLayout(borderLayout);
        mainFrame.add(BorderLayout.CENTER, sudokuGridView);
        buttonPanel.add(solveButton);
        solveButton.addActionListener(this);
        buttonPanel.add(stepButton);
        stepButton.addActionListener(this);
        mainFrame.add(BorderLayout.SOUTH, buttonPanel);
        mainFrame.setSize(400,400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        sudokuGridView.setModel( sudoku );

        sudoku.setCellValues( SudokuGridSamples.getInstance().getSample("Simple") );
    }

    public void actionPerformed(ActionEvent ev){
        if( ev.getSource().equals(solveButton) ){
            long startTime = System.currentTimeMillis();
            sudoku.solve();
            
        } else  if( ev.getSource().equals(stepButton) ){
            sudoku.step();
           
        } else if (ev.getSource().equals(openMenuItem)) {
            if( fileChooser == null ){
                fileChooser = new JFileChooser();
            }

            try {
                if( fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION ){
                    System.out.println(fileChooser.getSelectedFile().getCanonicalPath());
                }
            } catch( IOException ex ){
                ex.printStackTrace();
            }
            
        } else if( ev.getSource().equals( exitMenuItem ) ){
            System.exit(0);
            
        } else if( ev.getSource() instanceof JMenuItem ){
            JMenuItem menuItem = (JMenuItem) ev.getSource();
            sudoku.setCellValues( SudokuGridSamples.getInstance().getSample(menuItem.getText()) );
        } else {
            //
        }
    }
}
