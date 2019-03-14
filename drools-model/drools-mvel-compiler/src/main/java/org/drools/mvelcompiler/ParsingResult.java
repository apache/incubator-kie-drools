package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class ParsingResult {

    private List<Statement> statements;
    private List<String> modifyProperties = new ArrayList<>();

    public ParsingResult(List<Statement> statements) {
        this.statements = statements;
    }

    public String resultAsString() {

        BlockStmt blockStmt = new BlockStmt(NodeList.nodeList(statements));

        return printConstraint(blockStmt);
    }

    public ParsingResult addModifyProperties(Collection<? extends String> properties) {
        modifyProperties.addAll(properties);
        return this;
    }

    public List<String> getModifyProperties() {
        return modifyProperties;
    }

    @Override
    public String toString() {
        return "ParsingResult{" +
                "statements='" + resultAsString() + '\'' +
                '}';
    }
}
