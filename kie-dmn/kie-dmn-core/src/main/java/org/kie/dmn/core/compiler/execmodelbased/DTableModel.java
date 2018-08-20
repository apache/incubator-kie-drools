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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.kie.dmn.core.compiler.DMNEvaluatorCompiler.inferTypeRef;
import static org.kie.dmn.feel.lang.types.BuiltInType.determineTypeFromName;
import static org.kie.dmn.feel.runtime.decisiontables.HitPolicy.fromString;

public class DTableModel {
    private final DMNFEELHelper feel;
    private final DMNModelImpl model;
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

    public DTableModel( DMNFEELHelper feel, DMNModelImpl model, String dtName, String tableName, DecisionTable dt ) {
        this.feel = feel;
        this.model = model;
        this.dt = dt;
        this.dtName = dtName;
        this.namespace = CodegenStringUtil.escapeIdentifier( model.getNamespace() );
        this.tableName = CodegenStringUtil.escapeIdentifier( tableName );
        this.hitPolicy = fromString(dt.getHitPolicy().value() + (dt.getAggregation() != null ? " " + dt.getAggregation().value() : ""));
        this.columns = dt.getInput().stream()
                .map( DColumnModel::new ).collect( toList() );
        this.outputs = dt.getOutput().stream()
                .map( DOutputModel::new ).collect( toList() );
        this.rows = dt.getRule().stream().map( DRowModel::new ).collect( toList() );

        this.variableTypes = columns.stream().collect( toMap( DColumnModel::getName, DColumnModel::getType ) );
        this.dtable = new DecisionTableImpl( dtName, outputs );
    }

    public DTableModel compileAll( DMNCompilerContext ctx ) {
        CompilerContext feelctx = feel.newCompilerContext();
        feelctx.setDoCompile( true );
        ctx.getVariables().forEach( (k, v) -> feelctx.addInputVariableType( k, ((BaseDMNTypeImpl ) v).getFeelType() ) );

        Map<String, CompiledFEELExpression> compilationCache = new HashMap<>();
        initInputClauses(feelctx, compilationCache);
        initRows(feelctx, compilationCache);
        initOutputClauses(feelctx, compilationCache);
        validate();
        return this;
    }

    private void validate() {
        if ( dt.getHitPolicy().equals( org.kie.dmn.model.api.HitPolicy.PRIORITY) && !hasOutputValues() ) {
            MsgUtil.reportMessage( ExecModelDMNEvaluatorCompiler.logger,
                                   DMNMessage.Severity.ERROR,
                                   dt.getParent(),
                                   model,
                                   null,
                                   null,
                                   Msg.MISSING_OUTPUT_VALUES,
                                   dt.getParent() );
        }
    }

    private boolean hasOutputValues() {
        return outputs.stream().map( o -> o.outputValues ).anyMatch( l -> !l.isEmpty() );
    }

    public boolean hasDefaultValues() {
        return outputs.stream().allMatch( o -> o.compiledDefault != null );
    }

    private void initInputClauses( CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache ) {
        int index = 1;
        for (DColumnModel column : columns) {
            String inputValuesText = getInputValuesText( column.inputClause );
            if (inputValuesText != null) {
                column.inputTests = feel.evaluateUnaryTests( inputValuesText, variableTypes );
            }
            column.compiledInputClause = compileFeelExpression( column.inputClause, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX, compilationCache, column.getName(), index++ );
        }
    }

    private void initRows( CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache ) {
        int index = 1;
        for (DRowModel row : rows) {
            int rowIndex = index;
            row.compiledOutputs = row.outputs.stream().map( expr -> compileFeelExpression( dt, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX, compilationCache, expr, rowIndex ) ).collect( toList() );
            index++;
        }
    }

    private CompiledFEELExpression compileFeelExpression( DMNElement element, DMNFEELHelper feel, CompilerContext feelctx, Msg.Message msg, Map<String, CompiledFEELExpression> compilationCache, String expr, int index ) {
        return compilationCache.computeIfAbsent(expr, e -> e == null || e.isEmpty() ? ctx -> null : (CompiledFEELExpression ) feel.compile( model, element, msg, dtName, e, feelctx, index ) );
    }

    private void initOutputClauses( CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache ) {
        for (DOutputModel output : outputs) {
            output.outputValues = getOutputValuesTests( output );
            String defaultValue = output.outputClause.getDefaultOutputEntry() != null ? output.outputClause.getDefaultOutputEntry().getText() : null;
            if (defaultValue != null) {
                output.compiledDefault = compileFeelExpression( output.outputClause, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX, compilationCache, defaultValue, 0 );
            }
        }
    }

    private List<UnaryTest> getOutputValuesTests( DOutputModel output ) {
        String outputValuesText = Optional.ofNullable( output.outputClause.getOutputValues() ).map( UnaryTests::getText ).orElse( null );
        output.typeRef = inferTypeRef(model, dt, output.outputClause);
        if (outputValuesText != null) {
            return feel.evaluateUnaryTests( outputValuesText, variableTypes );
        }
        if (output.typeRef != model.getTypeRegistry().unknown() && output.typeRef.getAllowedValuesFEEL() != null) {
            return output.typeRef.getAllowedValuesFEEL();
        }
        return Collections.emptyList();
    }

    public Object defaultToOutput( EvaluationContext ctx ) {
        if ( outputs.size() == 1 ) {
            return outputs.get( 0 ).compiledDefault.apply( ctx );
        }

        // zip outputEntries with its name:
        return IntStream.range( 0, outputs.size() ).boxed()
                .collect( toMap( i -> outputs.get( i ).getName(), i -> outputs.get( i ).compiledDefault.apply( ctx ) ) );
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

        DRowModel(DecisionRule dr) {
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

    public static class DOutputModel {
        private final OutputClause outputClause;
        private List<UnaryTest> outputValues;
        private CompiledFEELExpression compiledDefault;
        private BaseDMNTypeImpl typeRef;

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

                @Override
                public Type getType() {
                    return typeRef.getFeelType();
                }

                @Override
                public boolean isCollection() {
                    return typeRef.isCollection();
                }
            };
        }

        public String getName() {
            return outputClause.getName();
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
