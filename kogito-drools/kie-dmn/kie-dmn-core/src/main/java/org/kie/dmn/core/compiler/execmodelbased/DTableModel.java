/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.model.v1_1.DecisionRule;
import org.kie.dmn.model.v1_1.DecisionTable;
import org.kie.dmn.model.v1_1.InputClause;
import org.kie.dmn.model.v1_1.LiteralExpression;
import org.kie.dmn.model.v1_1.OutputClause;
import org.kie.dmn.model.v1_1.UnaryTests;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import static org.kie.dmn.feel.lang.types.BuiltInType.determineTypeFromName;
import static org.kie.dmn.feel.runtime.decisiontables.HitPolicy.fromString;

public class DTableModel {
    private final DecisionTable dt;
    private final String namespace;
    private final String dtName;
    private final String tableName;
    private final HitPolicy hitPolicy;

    private final List<DColumnModel> columns;
    private final List<DRowModel> rows;
    private final List<DOutputModel> outputs;

    private final Map<String, Type> variableTypes;

    private final org.kie.dmn.feel.runtime.decisiontables.DecisionTable dtable;

    public DTableModel( DMNFEELHelper feel, String namespace, String dtName, DecisionTable dt ) {
        this.dt = dt;
        this.dtName = dtName;
        this.namespace = CodegenStringUtil.escapeIdentifier( namespace );
        this.tableName = CodegenStringUtil.escapeIdentifier( dtName );
        this.hitPolicy = fromString(dt.getHitPolicy().value() + (dt.getAggregation() != null ? " " + dt.getAggregation().value() : ""));
        this.columns = dt.getInput().stream()
                .map( DColumnModel::new ).collect( toList() );
        this.outputs = dt.getOutput().stream()
                .map( DOutputModel::new ).collect( toList() );
        this.rows = dt.getRule().stream().map( dr -> new DRowModel( feel, dr ) ).collect( toList() );

        this.variableTypes = columns.stream().collect( toMap( DColumnModel::getName, DColumnModel::getType ) );
        this.dtable = new DecisionTableImpl( dtName, outputs );
    }

    public DTableModel compileAll( DMNFEELHelper feel, DMNCompilerContext ctx ) {
        CompilerContext feelctx = feel.newCompilerContext();
        feelctx.setDoCompile( true );
        ctx.getVariables().forEach( (k, v) -> feelctx.addInputVariableType( k, ((BaseDMNTypeImpl ) v).getFeelType() ) );

        initInputClauses(feel, feelctx);
        initRows(feel, feelctx);
        initOutputClauses(feel, feelctx);
        return this;
    }

    private void initInputClauses( DMNFEELHelper feel, CompilerContext feelctx ) {
        for (DColumnModel column : columns) {
            String inputValuesText = getInputValuesText( column.inputClause );
            if (inputValuesText != null) {
                column.inputTests = feel.evaluateUnaryTests( inputValuesText, variableTypes );
            }
            column.compiledInputClause = (CompiledFEELExpression) feel.compile( column.getName(), feelctx );
        }
    }

    private void initRows( DMNFEELHelper feel, CompilerContext feelctx ) {
        for (DRowModel row : rows) {
            row.compiledOutputs = row.outputs.stream().map( expr -> (CompiledFEELExpression) feel.compile( expr, feelctx ) ).collect( toList() );
        }
    }

    private void initOutputClauses( DMNFEELHelper feel, CompilerContext feelctx ) {
        for (DOutputModel output : outputs) {
            String outputValuesText = getOutputValuesText( output.outputClause );
            if (outputValuesText != null) {
                output.outputValues = feel.evaluateUnaryTests( outputValuesText, variableTypes );
            }
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDtName() {
        return dtName;
    }

    public Map<String, Type> getVariableTypes() {
        return variableTypes;
    }

    public List<DColumnModel> getColumns() {
        return columns;
    }

    public List<InputClause> getInputs() {
        return dt.getInput();
    }

    public List<DRowModel> getRows() {
        return rows;
    }

    public int getOutputSize() {
        return outputs.size();
    }

    public int getInputSize() {
        return columns.size();
    }

    public HitPolicy getHitPolicy() {
        return hitPolicy;
    }

    public org.kie.dmn.feel.runtime.decisiontables.DecisionTable asDecisionTable() {
        return dtable;
    }

    public static class DRowModel {

        private final List<String> inputs;
        private final List<String> outputs;

        private List<CompiledFEELExpression> compiledOutputs;

        DRowModel(DMNFEELHelper feel, DecisionRule dr) {
            this.inputs = dr.getInputEntry().stream()
                    .map( UnaryTests::getText ).collect( toList() );
            this.outputs = dr.getOutputEntry().stream()
                    .map( LiteralExpression::getText ).collect( toList() );
        }

        public List<String> getInputs() {
            return inputs;
        }

        public Object evaluate(EvaluationContext ctx, int pos) {
            return compiledOutputs.get( pos ).apply( ctx );
        }
    }

    public static class DColumnModel {
        private final InputClause inputClause;
        private final String name;
        private final Type type;

        private List<UnaryTest> inputTests;
        private CompiledFEELExpression compiledInputClause;

        DColumnModel(InputClause inputClause) {
            this.inputClause = inputClause;
            LiteralExpression expr = inputClause.getInputExpression();
            this.name = expr.getText();
            this.type = determineTypeFromName( expr.getTypeRef() != null ? expr.getTypeRef().getLocalPart() : null );
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public FEELEvent validate( EvaluationContext ctx, Object parameter ) {
            if (inputTests != null) {
                boolean satisfies = inputTests.stream().map( ut -> ut.apply( ctx, parameter ) ).filter( Boolean::booleanValue ).findAny().orElse( false );
                if ( !satisfies ) {
                    String values = getInputValuesText( inputClause );
                    return new InvalidInputEvent( FEELEvent.Severity.ERROR,
                            inputClause.getInputExpression() + "='" + parameter + "' does not match any of the valid values " + values + " for decision table '" + getName() + "'.",
                            getName(),
                            null,
                            values );
                }
            }
            return null;
        }

        public Object evaluate(EvaluationContext ctx) {
            return compiledInputClause.apply( ctx );
        }
    }

    private static String getInputValuesText( InputClause inputClause ) {
        return Optional.ofNullable( inputClause.getInputValues() ).map( UnaryTests::getText ).orElse(null);
    }

    private static String getOutputValuesText( OutputClause outputClause ) {
        return  Optional.ofNullable( outputClause.getOutputValues() ).map( UnaryTests::getText ).orElse( null );
    }

    public static class DOutputModel {
        private final OutputClause outputClause;
        private List<UnaryTest> outputValues;

        DOutputModel( OutputClause outputClause ) {
            this.outputClause = outputClause;
        }

        org.kie.dmn.feel.runtime.decisiontables.DecisionTable.OutputClause asOutputClause() {
            return new org.kie.dmn.feel.runtime.decisiontables.DecisionTable.OutputClause() {
                @Override
                public String getName() {
                    return outputClause.getName();
                }

                @Override
                public List<UnaryTest> getOutputValues() {
                    return outputValues;
                }
            };
        }
    }

    private static class DecisionTableImpl implements org.kie.dmn.feel.runtime.decisiontables.DecisionTable {
        private final String name;
        private final List<OutputClause> outputs;

        private DecisionTableImpl( String name, List<DOutputModel> outputs ) {
            this.name = name;
            this.outputs = outputs.stream().map( DOutputModel::asOutputClause ).collect( toList() );
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<? extends OutputClause> getOutputs() {
            return outputs;
        }
    }
}
