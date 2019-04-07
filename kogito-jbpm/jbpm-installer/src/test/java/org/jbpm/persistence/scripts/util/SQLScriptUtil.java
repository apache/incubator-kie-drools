/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.persistence.scripts.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jbpm.persistence.scripts.DatabaseType;

/**
 * Contains util methods for working with SQL script.
 */
public final class SQLScriptUtil {

    /**
     * Standard SQL command delimiter.
     */
    private static final String DELIMITER_STANDARD = ";";
    /**
     * Delimiter used in MS SQL Server database systems.
     */
    private static final String DELIMITER_MSSQL = "GO";

    /**
     * Extracts SQL commands from SQL script. It parses SQL script and divides it to single commands.
     * @param script Script from which SQL commands are extracted.
     * @param databaseType Database system type for which is the script written.
     * @return A list of SQL commands that are in specified script.
     *         If there are no commands in the script, returns empty list. Never returns null.
     * @throws IOException
     */
    public static List<String> getCommandsFromScript(final File script, final DatabaseType databaseType)
            throws IOException {
        final List<String> scriptLines = FileUtils.readLines(script);
        final List<String> foundCommands = new ArrayList<String>();
        final StringBuilder command = new StringBuilder();
        for (String line : scriptLines) {
            // Ignore comments.
            final String trimmedLine = line.trim();
            if ("".equals(trimmedLine) || trimmedLine.startsWith("--") || trimmedLine.startsWith("#")) {
                continue;
            }
            // If the whole line is a delimiter -> add buffered command to found commands.
            if (trimmedLine.equals(DELIMITER_STANDARD)
                    || ((databaseType == DatabaseType.SQLSERVER || databaseType == DatabaseType.SQLSERVER2008)
                            && trimmedLine.equals(DELIMITER_MSSQL))) {
                if (!"".equals(command.toString())) {
                    foundCommands.add(command.toString());
                    command.setLength(0);
                    command.trimToSize();
                }
            }
            // Split line by delimiter.
            if (trimmedLine.contains(DELIMITER_STANDARD)) {
                extractCommandsFromLine(trimmedLine, "\\" + DELIMITER_STANDARD, command, foundCommands);
            } else if ((databaseType == DatabaseType.SQLSERVER || databaseType == DatabaseType.SQLSERVER2008)
                    && trimmedLine.contains(DELIMITER_MSSQL)) {
                extractCommandsFromLine(trimmedLine, DELIMITER_MSSQL, command, foundCommands);
            } else {
                command.append(trimmedLine).append(" ");
            }
        }
        // If there's still some buffered command, add it to found commands.
        if (!"".equals(command.toString())) {
            foundCommands.add(command.toString());
        }
        return foundCommands;
    }

    /**
     * Extracts SQL commands from string. Divides the string (line) by specified delimiter
     * and adds found whole commands to extractedCommands or buffers unfinished commands.
     * @param line Line which is searched for SQL commands.
     * @param delimiter Delimiter that ends SQL command. Used for dividing specified string (line).
     * @param bufferedCommand Already buffered command from previous searches.
     *                        If there's some buffered command, this method appends the first
     *                        found delimited occurence to buffered command.
     *                        If there's no buffered command and the string (line) not ends
     *                        with delimiter, the last part is buffered.
     * @param extractedCommands Results list to which are found SQL commands added.
     */
    private static void extractCommandsFromLine(final String line, final String delimiter,
            final StringBuilder bufferedCommand, final List<String> extractedCommands) {
        final String[] lineParts = line.split(delimiter);
        for (int i = 0; i < lineParts.length; i++) {
            // If there's some buffered command, append first found line part to it.
            if (i == 0) {
                extractedCommands.add(bufferedCommand.toString() + " " + lineParts[i]);
                bufferedCommand.setLength(0);
                bufferedCommand.trimToSize();
            } else {
                // If the line doesn't end with delimiter, buffer the last line part.
                if (i == (lineParts.length - 1) && !line.endsWith(delimiter)) {
                    bufferedCommand.append(lineParts[i]);
                } else {
                    extractedCommands.add(lineParts[i]);
                }
            }
        }
    }

    private SQLScriptUtil() {
        // It makes no sense to create instances of util classes.
    }
}
