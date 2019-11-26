package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.stmt.Statement;

public class PostProcessedExecModel {
    private Statement convertedStatement;

    public PostProcessedExecModel(Statement convertedStatement) {
        this.convertedStatement = convertedStatement;
    }

    public String getConvertedBlockAsString() {
        return convertedStatement.toString();
    }

    public Statement getConvertedStatement() {
        return convertedStatement;
    }
}
