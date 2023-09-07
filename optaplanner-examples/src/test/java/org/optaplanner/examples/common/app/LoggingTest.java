/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggingTest {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected static List<File> getAllFilesRecursivelyAndSorted(File dir, Predicate<File> fileFilter) {
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            return paths.map(Path::toFile)
                    .filter(fileFilter)
                    .sorted(new ProblemFileComparator())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading directory (" + dir + ").", e);
        }
    }

}
