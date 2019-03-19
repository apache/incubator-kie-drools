package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class ParsingResult {

    private List<Statement> statements;
    private Map<String, Set<String>> modifyProperties = new HashMap<>();

    public ParsingResult(List<Statement> statements) {
        this.statements = statements;
    }

    public String resultAsString() {
        return printConstraint(statementResults());
    }

    public BlockStmt statementResults() {
        return new BlockStmt(NodeList.nodeList(statements));
    }

    public ParsingResult setModifyProperties(Map<String, Set<String>> modifyProperties) {
        this.modifyProperties = modifyProperties;
        return this;
    }

    public Map<String, Set<String>> getModifyProperties() {
        return modifyProperties;
    }

    @Override
    public String toString() {
        return "ParsingResult{" +
                "statements='" + resultAsString() + '\'' +
                '}';
    }
}
