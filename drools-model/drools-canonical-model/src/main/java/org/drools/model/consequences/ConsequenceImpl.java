package org.drools.model.consequences;

import java.util.Arrays;
import java.util.stream.Stream;

import org.drools.model.Consequence;
import org.drools.model.Variable;
import org.drools.model.functions.BlockN;
import org.drools.model.impl.ModelComponent;

public class ConsequenceImpl implements Consequence, ModelComponent {
    private final Variable[] variables;
    private final Variable[] declarations;
    private final BlockN block;

    private final boolean usingDrools;

    private final boolean breaking;

    private final String language;

    public ConsequenceImpl(BlockN block, Variable[] variables, boolean usingDrools, boolean breaking, String language) {
        this.variables = variables;
        this.declarations = Stream.of(variables).filter( Variable::isFact ).toArray(Variable[]::new);
        this.block = block;
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
    public boolean isUsingDrools() {
        return usingDrools;
    }

    @Override
    public boolean isBreaking() {
        return breaking;
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
        return language != null ? language.equals( that.language ) : that.language == null;
    }

    @Override
    public String toString() {
        return "ConsequenceImpl (" +
                "variables: " + Arrays.toString(variables) + ", " +
                "language: " + language + ", " +
                "breaking: " + breaking + ")";
    }
}
