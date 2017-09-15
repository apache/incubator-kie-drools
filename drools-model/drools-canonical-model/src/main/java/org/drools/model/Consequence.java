package org.drools.model;

import org.drools.model.functions.BlockN;
import org.drools.model.functions.FunctionN;

public interface Consequence extends RuleItem {

    Variable[] getVariables();
    Variable[] getDeclarations();

    BlockN getBlock();

    FunctionN[] getInserts();

    Update[] getUpdates();

    Variable[] getDeletes();

    boolean isChangingWorkingMemory();

    boolean isUsingDrools();

    interface Update<T> {
        Variable<T> getUpdatedVariable();

        String[] getUpdatedFields();
    }
}
