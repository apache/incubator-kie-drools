/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.scrabble.domain.ScrabbleCell;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.examples.scrabble.domain.ScrabbleWordAssignment;

public class ScrabbleImporter extends AbstractTxtSolutionImporter<ScrabbleSolution> {

    public static void main(String[] args) {
        ScrabbleImporter importer = new ScrabbleImporter();
        importer.convert("jbossProjects.txt", "jbossProjects.xml");
    }

    public ScrabbleImporter() {
        super(new ScrabbleDao());
    }

    public ScrabbleImporter(boolean withoutDao) {
        super(withoutDao);
    }

    @Override
    public TxtInputBuilder<ScrabbleSolution> createTxtInputBuilder() {
        return new ScrabbleInputBuilder();
    }

    public static class ScrabbleInputBuilder extends TxtInputBuilder<ScrabbleSolution> {

        private ScrabbleSolution solution;
        private Locale locale;

        @Override
        public ScrabbleSolution readSolution() throws IOException {
            solution = new ScrabbleSolution();
            solution.setId(0L);
            locale = Locale.forLanguageTag(readStringValue("locale:"));
            readCellList();
            readWordList();
            BigInteger possibleSolutionSize = BigInteger.valueOf(solution.getCellList().size()).pow(
                    solution.getWordList().size());
            logger.info("ScrabbleSolution {} has {} words and {} cells with a search space of {}.",
                    getInputId(),
                    solution.getWordList().size(),
                    solution.getCellList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        private void readCellList() throws IOException {
            int gridWidth = readIntegerValue("gridWidth:");
            solution.setGridWidth(gridWidth);
            int gridHeight = readIntegerValue("gridHeight:");
            solution.setGridHeight(gridHeight);
            List<ScrabbleCell> cellList = new ArrayList<>(gridWidth * gridHeight);
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    ScrabbleCell cell = new ScrabbleCell();
                    cell.setId((long) (y * gridWidth + x));
                    cell.setX(x);
                    cell.setY(y);
                    cell.setWordSet(new LinkedHashSet<>());
                    cell.setCharacterCountMap(new LinkedHashMap<>());
                    cellList.add(cell);
                }
            }
            solution.setCellList(cellList);
        }

        private void readWordList() throws IOException {
            readConstantLine("# Words");
            List<ScrabbleWordAssignment> wordList = new ArrayList<>();
            String line = bufferedReader.readLine();
            long wordId = 0L;
            while (line != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    ScrabbleWordAssignment wordAssignment = new ScrabbleWordAssignment();
                    wordAssignment.setId(wordId);
                    wordId++;
                    wordAssignment.setSolution(solution);
                    // Add spaces to enforce an empty cell before and after each word
                    wordAssignment.setWord(" " + word.toUpperCase(locale) + " ");
                    wordList.add(wordAssignment);
                }
                line = bufferedReader.readLine();
            }
            solution.setWordList(wordList);
        }

    }

}
