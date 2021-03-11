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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
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

@Deprecated
public class DTableModel {
    private final DMNFEELHelper feel;
    private final DMNModelImpl model;
    private final DecisionTable dt;
    private final String namespace;
    private final String dtName;
    private final String tableName;
    private final HitPolicy hitPolicy;

    protected final List<DColumnModel> columns;
    protected final List<DRowModel> rows;
    protected final List<DOutputModel> outputs;

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
                         .map(c -> new DColumnModel(c, model)).collect(toList());
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
                                   dtName );
        }
    }

    private boolean hasOutputValues() {
        return outputs.stream().map( o -> o.outputValues ).anyMatch( l -> !l.isEmpty() );
    }

    public boolean hasDefaultValues() {
        return outputs.stream().allMatch( o -> o.compiledDefault != null );
    }

    protected void iterateOverRows(BiConsumer<DRowModel, Integer> rowsFeelExpressionGeneration) {
        int rowIndex = 1;
        for (DRowModel row : rows) {
            rowsFeelExpressionGeneration.accept(row, rowIndex);
            rowIndex++;
        }
    }

    protected void initRows(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        iterateOverRows((row, rowIndex) -> row.compiledOutputs = row.outputs.stream().map(expr -> compileFeelExpression(dt, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX, compilationCache, expr, rowIndex)).collect(toList()));
    }

    protected ClassOrInterfaceDeclaration[][] generateRows(CompilerContext feelctx) {
        List<ClassOrInterfaceDeclaration[]> allRows = new ArrayList<>();
        iterateOverRows((row, integer) -> {
            ClassOrInterfaceDeclaration[] rowCompiledOutputs = row.outputs.stream().map(expr -> feel.generateFeelExpressionSource(expr, feelctx)).toArray(ClassOrInterfaceDeclaration[]::new);
            allRows.add(rowCompiledOutputs);
        });
        return allRows.toArray(new ClassOrInterfaceDeclaration[0][0]);
    }

    protected void iterateOverInputClauses(BiConsumer<DColumnModel, Integer> inputFeelExpressionGeneration) {
        int index = 0;
        for (DColumnModel column : columns) {
            String inputValuesText = getInputValuesText( column.inputClause );
            if (inputValuesText != null && !inputValuesText.isEmpty()) {
                column.inputTests = feel.evaluateUnaryTests( inputValuesText, variableTypes );
            }
            inputFeelExpressionGeneration.accept(column, index);
            index++;
        }
    }

    protected void initInputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        iterateOverInputClauses((column, index) -> column.compiledInputClause = compileFeelExpression(column.inputClause, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX, compilationCache, column.getName(), index));
    }

    protected List<ClassOrInterfaceDeclaration> generateInputClauses(CompilerContext feelctx) {
        List<ClassOrInterfaceDeclaration> inputClauses = new ArrayList<>();
        iterateOverInputClauses((column, index) -> inputClauses.add(feel.generateFeelExpressionSource(column.getName(), feelctx)));
        return inputClauses;
    }

    protected void iterateOverOutputClauses(BiConsumer<DOutputModel, String> ouputFeelExpressionGeneration) {
        for (DOutputModel output : outputs) {
            output.outputValues = getOutputValuesTests( output );
            String defaultValue = output.outputClause.getDefaultOutputEntry() != null ? output.outputClause.getDefaultOutputEntry().getText() : null;
            if (defaultValue != null && !defaultValue.isEmpty()) {
                ouputFeelExpressionGeneration.accept(output, defaultValue);
            }
        }
    }

    protected void initOutputClauses( CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache ) {
        iterateOverOutputClauses((output, defaultValue) -> output.compiledDefault = compileFeelExpression(output.outputClause, feel, feelctx, Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX, compilationCache, defaultValue,  0));
    }

    public  Map<String, ClassOrInterfaceDeclaration> generateOutputClauses(CompilerContext feelctx) {
        Map<String, ClassOrInterfaceDeclaration> outputClauses = new HashMap<>();
        iterateOverOutputClauses((output, defaultValue) -> outputClauses.put(defaultValue, feel.generateFeelExpressionSource(defaultValue, feelctx)));
        return outputClauses;
    }

    protected CompiledFEELExpression compileFeelExpression( DMNElement element, DMNFEELHelper feel, CompilerContext feelctx, Msg.Message msg, Map<String, CompiledFEELExpression> compilationCache, String expr, int index ) {
        return compilationCache.computeIfAbsent(expr, e -> {
            if (e == null || e.isEmpty()) {
                return ctx -> null;
            } else {
                return (CompiledFEELExpression) feel.compile(model, element, msg, dtName, e, feelctx, index);
            }
        });
    }

    protected List<UnaryTest> getOutputValuesTests( DOutputModel output ) {
        String outputValuesText = Optional.ofNullable( output.outputClause.getOutputValues() ).map( UnaryTests::getText ).orElse( null );
        output.typeRef = inferTypeRef(model, dt, output.outputClause);
        if (outputValuesText != null && !outputValuesText.isEmpty()) {
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
        protected final List<String> outputs;

        protected List<CompiledFEELExpression> compiledOutputs;

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

        public List<String> getOutputs() {
            return outputs;
        }
    }

    public static class DColumnModel {
        private final InputClause inputClause;
        private final String name;
        private final Type type;
        private final DMNType dmnType;

        private List<UnaryTest> inputTests;
        protected CompiledFEELExpression compiledInputClause;

        DColumnModel(InputClause inputClause, DMNModelImpl model) {
            this.inputClause = inputClause;
            LiteralExpression expr = inputClause.getInputExpression();
            this.name = expr.getText();
            this.type = determineTypeFromName( expr.getTypeRef() != null ? expr.getTypeRef().getLocalPart() : null );
            if (expr.getTypeRef() != null) {
                String exprTypeRefNS = expr.getTypeRef().getNamespaceURI();
                if (exprTypeRefNS == null || exprTypeRefNS.isEmpty()) {
                    exprTypeRefNS = model.getNamespace();
                }
                this.dmnType = model.getTypeRegistry().resolveType(exprTypeRefNS, expr.getTypeRef().getLocalPart());
            } else {
                this.dmnType = null;
            }
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public FEELEvent validate( EvaluationContext ctx, Object parameter ) {
            if (inputTests != null) {
                boolean satisfies = true;
                if (dmnType != null && dmnType.isCollection() && parameter instanceof Collection) {
                    for (Object parameterItem : (Collection<?>) parameter) {
                        satisfies &= inputTests.stream().map(ut -> ut.apply(ctx, parameterItem)).filter(x -> x != null && x).findAny().orElse(false);
                    }
                } else {
                    satisfies = inputTests.stream().map(ut -> ut.apply(ctx, parameter)).filter(x -> x != null && x).findAny().orElse(false);
                }
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

    public String getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum generator) {
        String pkgName = getNamespace();
        String tableName = getTableName();
        return pkgName + "." + tableName + generator.type;
    }

    public static class DOutputModel {
        protected final OutputClause outputClause;
        protected List<UnaryTest> outputValues;
        protected CompiledFEELExpression compiledDefault;
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
