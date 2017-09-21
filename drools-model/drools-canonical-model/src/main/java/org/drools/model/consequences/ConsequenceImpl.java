package org.drools.model.consequences;

import java.util.stream.Stream;

import org.drools.model.Consequence;
import org.drools.model.Variable;
import org.drools.model.functions.BlockN;
import org.drools.model.functions.FunctionN;

public class ConsequenceImpl implements Consequence {
    private final Variable[] variables;
    private final Variable[] declarations;
    private final BlockN block;

    private final FunctionN[] inserts;
    private final Update[] updates;
    private final Variable[] deletes;

    private final boolean usingDrools;

    private final boolean breaking;

    ConsequenceImpl( BlockN block, Variable[] variables, FunctionN[] inserts, Update[] updates, Variable[] deletes, boolean usingDrools, boolean breaking ) {
        this.variables = variables;
        this.declarations = Stream.of(variables).filter( Variable::isFact ).toArray(Variable[]::new);
        this.block = block;
        this.inserts = inserts == null ? new FunctionN[0] : inserts;
        this.updates = updates == null ? new Update[0] : updates;
        this.deletes = deletes == null ? new Variable[0] : deletes;
        this.usingDrools = usingDrools;
        this.breaking = breaking;
    }

    @Override
    public Variable[] getVariables() {
        return variables;
    }

    @Override
    public Variable[] getDeclarations() {
        return declarations;
    }

    @Override
    public BlockN getBlock() {
        return block;
    }

    @Override
    public FunctionN[] getInserts() {
        return inserts;
    }

    @Override
    public Update[] getUpdates() {
        return updates;
    }

    @Override
    public Variable[] getDeletes() {
        return deletes;
    }

    @Override
    public boolean isUsingDrools() {
        return usingDrools;
    }

    @Override
    public boolean isBreaking() {
        return breaking;
    }

    @Override
    public boolean isChangingWorkingMemory() {
        return inserts.length > 0 || updates.length > 0 || deletes.length > 0;
    }

    public static class UpdateImpl implements Update {
        private final Variable updatedVariable;
        private final String[] updatedFields;

        public UpdateImpl(Variable updatedVariable, String... updatedFields) {
            this.updatedVariable = updatedVariable;
            this.updatedFields = updatedFields;
        }

        @Override
        public Variable getUpdatedVariable() {
            return updatedVariable;
        }

        @Override
        public String[] getUpdatedFields() {
            return updatedFields;
        }
    }
}
