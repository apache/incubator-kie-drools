package org.kie.pmml.compiler.api.mocks;

import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MathContext;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.Output;
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;

public class TestModel extends Model {

    private static final Boolean DEFAULT_SCORABLE = true;
    private String modelName;
    private MiningFunction miningFunction;
    private String algorithmName;
    private Boolean scorable;
    private MathContext mathContext;
    private MiningSchema miningSchema;
    private Output output;
    private LocalTransformations localTransformations;

    public TestModel() {
        modelName = getGeneratedClassName("TestModel");
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public Model setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    @Override
    public MiningFunction getMiningFunction() {
        return this.miningFunction;
    }

    @Override
    public TestModel setMiningFunction(MiningFunction miningFunction) {
        this.miningFunction = miningFunction;
        return this;
    }

    public String getAlgorithmName() {
        return this.algorithmName;
    }

    public TestModel setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
        return this;
    }

    public boolean isScorable() {
        return this.scorable == null ? DEFAULT_SCORABLE : this.scorable;
    }

    public TestModel setScorable(Boolean scorable) {
        this.scorable = scorable;
        return this;
    }

    public MathContext getMathContext() {
        return this.mathContext == null ? MathContext.DOUBLE : this.mathContext;
    }

    public TestModel setMathContext(MathContext mathContext) {
        this.mathContext = mathContext;
        return this;
    }

    public MiningSchema getMiningSchema() {
        return this.miningSchema;
    }

    public TestModel setMiningSchema(MiningSchema miningSchema) {
        this.miningSchema = miningSchema;
        return this;
    }

    public Output getOutput() {
        return this.output;
    }

    public TestModel setOutput(Output output) {
        this.output = output;
        return this;
    }

    public LocalTransformations getLocalTransformations() {
        return this.localTransformations;
    }

    public TestModel setLocalTransformations(LocalTransformations localTransformations) {
        this.localTransformations = localTransformations;
        return this;
    }

    @Override
    public VisitorAction accept(Visitor visitor) {
        return null;
    }
}
