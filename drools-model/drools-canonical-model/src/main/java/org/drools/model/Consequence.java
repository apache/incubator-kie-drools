package org.drools.model;

import org.drools.model.functions.BlockN;

public interface Consequence extends RuleItem {

    Variable[] getVariables();
    Variable[] getDeclarations();

    BlockN getBlock();

    boolean isUsingDrools();

    boolean isBreaking();

    interface Update<T> {
        Variable<T> getUpdatedVariable();

        String[] getUpdatedFields();
    }

    String getLanguage();
}
