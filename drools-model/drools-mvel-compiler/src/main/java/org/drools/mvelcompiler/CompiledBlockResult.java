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
package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.drools.mvel.parser.printer.PrintUtil.printNode;

public class CompiledBlockResult implements CompiledResult{

    private List<Statement> statements;
    private Set<String> usedBindings = new HashSet<>();

    public CompiledBlockResult(List<Statement> statements) {
        this.statements = statements;
    }

    public String resultAsString() {
        return printNode(statementResults());
    }

    @Override
    public BlockStmt statementResults() {
        return new BlockStmt(NodeList.nodeList(statements));
    }

    public CompiledBlockResult setUsedBindings(Set<String> usedBindings) {
        this.usedBindings = usedBindings;
        return this;
    }

    @Override
    public Set<String> getUsedBindings() {
        return usedBindings;
    }

    @Override
    public String toString() {
        return "ParsingResult{" +
                "statements='" + resultAsString() + '\'' +
                '}';
    }
}
