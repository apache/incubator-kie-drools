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
