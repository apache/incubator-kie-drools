package org.drools.model.consequences;

import java.util.Arrays;
import java.util.stream.Stream;

import org.drools.model.Consequence;
import org.drools.model.Variable;
import org.drools.model.functions.BlockN;
import org.drools.model.functions.FunctionN;
import org.drools.model.impl.ModelComponent;

public class ConsequenceImpl implements Consequence, ModelComponent {
    private final Variable[] variables;
    private final Variable[] declarations;
    private final BlockN block;

    private final FunctionN[] inserts;
    private final Update[] updates;
    private final Variable[] deletes;

    private final boolean usingDrools;

    private final boolean breaking;

    private final String language;

    ConsequenceImpl(BlockN block, Variable[] variables, FunctionN[] inserts, Update[] updates, Variable[] deletes, boolean usingDrools, boolean breaking, String language) {
        this.variables = variables;
        this.declarations = Stream.of(variables).filter( Variable::isFact ).toArray(Variable[]::new);
        this.block = block;
        this.inserts = inserts == null ? new FunctionN[0] : inserts;
        this.updates = updates == null ? new Update[0] : updates;
        this.deletes = deletes == null ? new Variable[0] : deletes;
        this.usingDrools = usingDrools;
        this.breaking = breaking;
        this.language = language;
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

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ConsequenceImpl) ) return false;

        ConsequenceImpl that = ( ConsequenceImpl ) o;

        if ( usingDrools != that.usingDrools ) return false;
        if ( breaking != that.breaking ) return false;
        if ( !ModelComponent.areEqualInModel( variables, that.variables ) ) return false;
        if ( !ModelComponent.areEqualInModel( declarations, that.declarations ) ) return false;
        if ( block != null ? !block.equals( that.block ) : that.block != null ) return false;
        if ( !Arrays.equals( inserts, that.inserts ) ) return false;
        if ( !Arrays.equals( updates, that.updates ) ) return false;
        if ( !ModelComponent.areEqualInModel( deletes, that.deletes ) ) return false;
        return language != null ? language.equals( that.language ) : that.language == null;
    }
}
