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

package org.drools.planner.examples.nqueens.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.drools.planner.examples.common.app.LoggingMain;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.persistence.XstreamSolutionDaoImpl;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nqueens.domain.Queen;

public class NQueensGenerator extends LoggingMain {

    private static final File outputDir = new File("data/nqueens/unsolved/");

    public static void main(String[] args) {
        new NQueensGenerator().generate();
    }

    public void generate() {
        String nString = JOptionPane.showInputDialog("For what n?");
        int n = Integer.parseInt(nString.trim());
        SolutionDao solutionDao = new NQueensDaoImpl();
        String outputFileName = "unsolvedNQueens" + n + ".xml";
        File outputFile = new File(outputDir, outputFileName);
        NQueens nQueens = createNQueens(n);
        solutionDao.writeSolution(nQueens, outputFile);
    }

    private NQueens createNQueens(int n) {
        NQueens nQueens = new NQueens();
        nQueens.setId(0L);
        List<Queen> queenList = new ArrayList<Queen>(n);
        for (int i = 0; i < n; i++) {
            Queen queen = new Queen();
            queen.setId((long) i);
            queen.setX(i);
            queen.setY(0);
            queenList.add(queen);
        }
        nQueens.setQueenList(queenList);
        return nQueens;
    }

}
