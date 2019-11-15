package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.javaparser.ast.stmt.Statement;

public class PostProcessedExecModel {
    private final List<CreatedClass> createdClasses = new ArrayList<>();
    private Statement convertedStatement;

    public PostProcessedExecModel(Statement convertedStatement) {
        this.convertedStatement = convertedStatement;
    }

    public String getConvertedBlockAsString() {
        return convertedStatement.toString();
    }

    public PostProcessedExecModel addAllLambdaClasses(Collection<CreatedClass> createdClasses) {
        this.createdClasses.addAll(createdClasses);
        return this;
    }
}
