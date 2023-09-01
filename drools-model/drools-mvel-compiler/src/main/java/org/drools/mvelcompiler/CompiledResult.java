package org.drools.mvelcompiler;

import java.util.Set;

import com.github.javaparser.ast.stmt.BlockStmt;

public interface CompiledResult {

    BlockStmt statementResults();

    Set<String> getUsedBindings();
}
