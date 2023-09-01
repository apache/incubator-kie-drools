package org.kie.dmn.feel.runtime.decisiontables;

import java.util.List;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;

public interface DecisionTable {

    String getName();

    List<? extends OutputClause> getOutputs();

    interface OutputClause {

        String getName();

        List<UnaryTest> getOutputValues();

        Type getType();

        boolean isCollection();
    }
}
