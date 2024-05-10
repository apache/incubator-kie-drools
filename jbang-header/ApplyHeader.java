//usr/bin/env jbang "$0" "$@" ; exit $?

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributes;

public class ApplyHeader {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: jbang ApplyHeader.java <root-folder> '<glob-pattern>'");
            System.exit(1);
        }

        Path rootFolder = Paths.get(args[0]);
        String globPattern = args[1];

        try {
            // Read header content
            final String header =
                    """
                    /**
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
                    """;

            // Create a matcher for the glob pattern
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

            // Find files matching the glob pattern recursively
            Files.walkFileTree(rootFolder, new ModifyHeader(matcher, header));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ModifyHeader extends SimpleFileVisitor<Path> {
        private final PathMatcher matcher;
        private final String header;

        ModifyHeader(PathMatcher matcher, String header) {
            this.matcher = matcher;
            this.header = header;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Check to see if the file name matches
            if (matcher.matches(file.getFileName())) {
                // The file matches, and there is no ASF header, then add it
                if (Files.lines(file, StandardCharsets.UTF_8).noneMatch(line -> line.contains("Licensed to the Apache Software Foundation"))) {
                    System.out.println("Executing header update on: " + file.toString());
                    String content = Files.readString(file, StandardCharsets.UTF_8);
                    content = header + "\n" + content;
                    Files.writeString(file, content, StandardCharsets.UTF_8);
                    return FileVisitResult.CONTINUE;
                }
            }
            return super.visitFile(file, attrs);
        }
    }
}