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

package org.optaplanner.examples.common.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractTxtSolutionImporter<Solution_> extends AbstractSolutionImporter<Solution_> {

    private static final String DEFAULT_INPUT_FILE_SUFFIX = "txt";

    @Override
    public String getInputFileSuffix() {
        return DEFAULT_INPUT_FILE_SUFFIX;
    }

    public abstract TxtInputBuilder<Solution_> createTxtInputBuilder();

    @Override
    public Solution_ readSolution(File inputFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"))) {
            TxtInputBuilder<Solution_> txtInputBuilder = createTxtInputBuilder();
            txtInputBuilder.setInputFile(inputFile);
            txtInputBuilder.setBufferedReader(reader);
            try {
                Solution_ solution = txtInputBuilder.readSolution();
                logger.info("Imported: {}", inputFile);
                return solution;
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new IllegalArgumentException("Exception in inputFile (" + inputFile + ")", e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the file (" + inputFile.getName() + ").", e);
        }
    }

    public Solution_ readSolution(URL inputURL) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputURL.openStream(), "UTF-8"))) {
            TxtInputBuilder<Solution_> txtInputBuilder = createTxtInputBuilder();
            txtInputBuilder.setInputFile(new File(inputURL.getFile()));
            txtInputBuilder.setBufferedReader(bufferedReader);
            try {
                return txtInputBuilder.readSolution();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Exception in inputURL (" + inputURL + ")", e);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Exception in inputURL (" + inputURL + ")", e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the inputURL (" + inputURL + ").", e);
        }
    }

    public static abstract class TxtInputBuilder<Solution_> extends InputBuilder {

        protected File inputFile;
        protected BufferedReader bufferedReader;

        public void setInputFile(File inputFile) {
            this.inputFile = inputFile;
        }

        public void setBufferedReader(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        public abstract Solution_ readSolution() throws IOException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

        public String getInputId() {
            return FilenameUtils.getBaseName(inputFile.getPath());
        }

        // ************************************************************************
        // Read methods
        // ************************************************************************

        public void readEmptyLine() throws IOException {
            readConstantLine("");
        }

        public void readConstantLine(String constantRegex) throws IOException {
            readConstantLine(bufferedReader, constantRegex);
        }

        public void readConstantLine(BufferedReader subBufferedReader, String constantRegex) throws IOException {
            String line = subBufferedReader.readLine();
            if (line == null) {
                throw new IllegalArgumentException("File ends before a line is expected to be a constant regex ("
                        + constantRegex + ").");
            }
            String value = line.trim();
            if (!value.matches(constantRegex)) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to be a constant regex ("
                        + constantRegex + ").");
            }
        }

        public boolean readOptionalConstantLine(String constantRegex) throws IOException {
            bufferedReader.mark(1024);
            boolean valid = true;
            String line = bufferedReader.readLine();
            if (line == null) {
                valid = false;
            } else {
                String value = line.trim();
                if (!value.matches(constantRegex)) {
                    valid = false;
                }
            }
            if (!valid) {
                bufferedReader.reset();
            }
            return valid;
        }

        public void skipOptionalConstantLines(String constantRegex) throws IOException {
            boolean valid = true;
            while (valid) {
                valid = readOptionalConstantLine(constantRegex);
            }
        }

        public void readUntilConstantLine(String constantRegex) throws IOException {
            String line;
            String value;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    throw new IllegalArgumentException("File ends before a line is expected to be a constant regex ("
                            + constantRegex + ").");
                }
                value = line.trim();
            } while (!value.matches(constantRegex));
        }

        public int readIntegerValue() throws IOException {
            return readIntegerValue("");
        }

        public int readIntegerValue(String prefixRegex) throws IOException {
            return readIntegerValue(prefixRegex, "");
        }

        public int readIntegerValue(String prefixRegex, String suffixRegex) throws IOException {
            String line = bufferedReader.readLine();
            if (line == null) {
                throw new IllegalArgumentException("File ends before a line is expected to contain an integer value ("
                        + prefixRegex + "<value>" + suffixRegex + ").");
            }
            String value = removePrefixSuffixFromLine(line, prefixRegex, suffixRegex);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to contain an integer value ("
                        + value + ").", e);
            }
        }

        public long readLongValue() throws IOException {
            return readLongValue("");
        }

        public long readLongValue(String prefixRegex) throws IOException {
            return readLongValue(prefixRegex, "");
        }

        public long readLongValue(String prefixRegex, String suffixRegex) throws IOException {
            String line = bufferedReader.readLine();
            if (line == null) {
                throw new IllegalArgumentException("File ends before a line is expected to contain an integer value ("
                        + prefixRegex + "<value>" + suffixRegex + ").");
            }
            String value = removePrefixSuffixFromLine(line, prefixRegex, suffixRegex);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to contain an integer value ("
                        + value + ").", e);
            }
        }

        public String readStringValue() throws IOException {
            return readStringValue("");
        }

        public String readStringValue(String prefixRegex) throws IOException {
            return readStringValue(prefixRegex, "");
        }

        public String readStringValue(String prefixRegex, String suffixRegex) throws IOException {
            String line = bufferedReader.readLine();
            if (line == null) {
                throw new IllegalArgumentException("File ends before a line is expected to contain an string value ("
                        + prefixRegex + "<value>" + suffixRegex + ").");
            }
            return removePrefixSuffixFromLine(line, prefixRegex, suffixRegex);
        }

        public String readOptionalStringValue(String defaultValue) throws IOException {
            return readOptionalStringValue("", defaultValue);
        }

        public String readOptionalStringValue(String prefixRegex, String defaultValue) throws IOException {
            return readOptionalStringValue(prefixRegex, "", defaultValue);
        }

        public String readOptionalStringValue(String prefixRegex, String suffixRegex, String defaultValue) throws IOException {
            bufferedReader.mark(1024);
            boolean valid = true;
            String value = bufferedReader.readLine();
            if (value == null) {
                valid = false;
            } else {
                value = value.trim();
                if (value.matches("^" + prefixRegex + ".*")) {
                    value = value.replaceAll("^" + prefixRegex + "(.*)", "$1");
                } else {
                    valid = false;
                }
                if (value.matches(".*" + suffixRegex + "$")) {
                    value = value.replaceAll("(.*)" + suffixRegex + "$", "$1");
                } else {
                    valid = false;
                }
            }
            if (!valid) {
                bufferedReader.reset();
                return defaultValue;
            }
            value = value.trim();
            return value;
        }

        public String removePrefixSuffixFromLine(String line, String prefixRegex, String suffixRegex) {
            String value = line.trim();
            if (!value.matches("^" + prefixRegex + ".*")) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to start with prefixRegex ("
                        + prefixRegex + ").");
            }
            value = value.replaceAll("^" + prefixRegex + "(.*)", "$1");
            if (!value.matches(".*" + suffixRegex + "$")) {
                throw new IllegalArgumentException("Read line (" + line + ") is expected to end with suffixRegex ("
                        + suffixRegex + ").");
            }
            value = value.replaceAll("(.*)" + suffixRegex + "$", "$1");
            value = value.trim();
            return value;
        }

        // ************************************************************************
        // Split methods
        // ************************************************************************

        public String[] splitBySpace(String line) {
            return splitBySpace(line, null);
        }

        public String[] splitBySpace(String line, Integer numberOfTokens) {
            return splitBy(line, "\\ ", "a space ( )", numberOfTokens, false, false);
        }

        public String[] splitBySpace(String line, Integer minimumNumberOfTokens, Integer maximumNumberOfTokens) {
            return splitBy(line, "\\ ", "a space ( )", minimumNumberOfTokens, maximumNumberOfTokens, false, false);
        }

        public String[] splitBySpace(String line, Integer minimumNumberOfTokens, Integer maximumNumberOfTokens,
                boolean trim, boolean removeQuotes) {
            return splitBy(line, "\\ ", "a space ( )", minimumNumberOfTokens, maximumNumberOfTokens, trim, removeQuotes);
        }

        public String[] splitBySpacesOrTabs(String line) {
            return splitBySpacesOrTabs(line, null);
        }

        public String[] splitBySpacesOrTabs(String line, Integer numberOfTokens) {
            return splitBy(line, "[\\ \\t]+", "spaces or tabs", numberOfTokens, false, false);
        }

        public String[] splitBySpacesOrTabs(String line, Integer minimumNumberOfTokens, Integer maximumNumberOfTokens) {
            return splitBy(line, "[\\ \\t]+", "spaces or tabs", minimumNumberOfTokens, maximumNumberOfTokens,
                    false, false);
        }

        public String[] splitByPipelineAndTrim(String line, int numberOfTokens) {
            return splitBy(line, "\\|", "a pipeline (|)", numberOfTokens, true, false);
        }

        public String[] splitBySemicolonSeparatedValue(String line) {
            return splitBy(line, ";", "a semicolon (;)", null, false, true);
        }

        public String[] splitBySemicolonSeparatedValue(String line, int numberOfTokens) {
            return splitBy(line, ";", "a semicolon (;)", numberOfTokens, false, true);
        }

        public String[] splitByCommaAndTrim(String line, int numberOfTokens) {
            return splitBy(line, "\\,", "a comma (,)", numberOfTokens, true, false);
        }

        public String[] splitByCommaAndTrim(String line, Integer minimumNumberOfTokens, Integer maximumNumberOfTokens) {
            return splitBy(line, "\\,", "a comma (,)", minimumNumberOfTokens, maximumNumberOfTokens, true, false);
        }

        public String[] splitBy(String line, String delimiterRegex, String delimiterName,
                Integer numberOfTokens, boolean trim, boolean removeQuotes) {
            return splitBy(line, delimiterRegex, delimiterName, numberOfTokens, numberOfTokens, trim, removeQuotes);
        }

        public String[] splitBy(String line, String delimiterRegex, String delimiterName,
                Integer minimumNumberOfTokens, Integer maximumNumberOfTokens, boolean trim, boolean removeQuotes) {
            String[] lineTokens = line.split(delimiterRegex);
            if (removeQuotes) {
                List<String> lineTokenList = new ArrayList<>(lineTokens.length);
                for (int i = 0; i < lineTokens.length; i++) {
                    String token = lineTokens[i];
                    while ((trim ? token.trim() : token).startsWith("\"") && !(trim ? token.trim() : token).endsWith("\"")) {
                        i++;
                        if (i >= lineTokens.length) {
                            throw new IllegalArgumentException("The line (" + line
                                    + ") has an invalid use of quotes (\").");
                        }
                        String delimiter;
                        switch (delimiterRegex) {
                            case "\\ ":
                                delimiter = " ";
                                break;
                            case "\\,":
                                delimiter = ",";
                                break;
                            default:
                                throw new IllegalArgumentException("Not supported delimiterRegex (" + delimiterRegex + ")");
                        }
                        token += delimiter + lineTokens[i];
                    }
                    if (trim) {
                        token = token.trim();
                    }
                    if (token.startsWith("\"") && token.endsWith("\"")) {
                        token = token.substring(1, token.length() - 1);
                        token = token.replaceAll("\"\"", "\"");
                    }
                    lineTokenList.add(token);
                }
                lineTokens = lineTokenList.toArray(new String[0]);
            }
            if (minimumNumberOfTokens != null && lineTokens.length < minimumNumberOfTokens) {
                throw new IllegalArgumentException("Read line (" + line + ") has " + lineTokens.length
                        + " tokens but is expected to contain at least " + minimumNumberOfTokens
                        + " tokens separated by " + delimiterName + ".");
            }
            if (maximumNumberOfTokens != null && lineTokens.length > maximumNumberOfTokens) {
                throw new IllegalArgumentException("Read line (" + line + ") has " + lineTokens.length
                        + " tokens but is expected to contain at most " + maximumNumberOfTokens
                        + " tokens separated by " + delimiterName + ".");
            }
            if (trim) {
                for (int i = 0; i < lineTokens.length; i++) {
                    lineTokens[i] = lineTokens[i].trim();
                }
            }
            return lineTokens;
        }

        public boolean parseBooleanFromNumber(String token) {
            switch (token) {
                case "0":
                    return false;
                case "1":
                    return true;
                default:
                    throw new IllegalArgumentException("The token (" + token
                            + ") is expected to be 0 or 1 representing a boolean.");
            }
        }

    }

}
