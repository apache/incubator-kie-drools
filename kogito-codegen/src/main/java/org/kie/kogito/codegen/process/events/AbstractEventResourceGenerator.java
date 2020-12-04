/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.events;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.TemplatedGenerator;

import static com.github.javaparser.StaticJavaParser.parse;

abstract class AbstractEventResourceGenerator {

    protected TemplatedGenerator generator;

    public AbstractEventResourceGenerator(TemplatedGenerator generator){
        this.generator = generator;
    }

    protected final String getResourceTemplate(){
        return generator.templatePath();
    }

    protected final String getClassName(){
        return generator.typeName();
    }

    public final String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public abstract String generate();

    protected final List<String> extractRepeatLinesFromMethod(final BlockStmt block) {
        // first we take the comment block and then filter the content to use only the lines we are interested
        final List<String> linesSetup = Stream.of(block.getAllContainedComments().stream()
                                                          .filter(c -> c.isBlockComment() && c.getContent().contains("$repeat$"))
                                                          .findFirst().orElseThrow(() -> new IllegalArgumentException("Repeat block not found!"))
                                                          .getContent().split("\n"))
                .filter(l -> !l.trim().isEmpty() && !l.contains("repeat"))
                .map(l -> l.replace("*", ""))
                .collect(Collectors.toList());
        // clean up the comments
        block.getAllContainedComments().forEach(Comment::remove);
        return linesSetup;
    }
}
