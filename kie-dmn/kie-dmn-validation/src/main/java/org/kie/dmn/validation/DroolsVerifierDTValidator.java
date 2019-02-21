package org.kie.dmn.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.index.IndexImpl;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.DataType;
import org.drools.verifier.core.index.model.DataType.DataTypes;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.RangeNode.IntervalBoundary;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;

public class DroolsVerifierDTValidator {


    private static final String OBJECT_TYPE_NAME = "mock.Type";
    private static AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfigurationMock(CheckConfiguration.newDefault());

    public static void validateDT(DMNModel model, DecisionNode dn, DecisionTable dt) {
        IndexImpl index1 = new IndexImpl();
        ObjectType objectType = new ObjectType(OBJECT_TYPE_NAME, analyzerConfiguration);
        int jHeadIdx = 0;
        List<Column> columns = new ArrayList<>();
        List<ObjectField> objectFields = new ArrayList<>();
        for (InputClause ic : dt.getInput()) {
            Column c = new Column(jHeadIdx + 1, analyzerConfiguration);
            columns.add(c);
            String typeRef = Optional.ofNullable(ic.getInputExpression().getTypeRef()).map(QName::getLocalPart).orElse("any");
            ObjectField of = new ObjectField(OBJECT_TYPE_NAME, typeRef, typeRef, analyzerConfiguration);
            objectFields.add(of);
            objectType.getFields().add(of);
            jHeadIdx++;
        }
        for (OutputClause oc : dt.getOutput()) {
            Column c = new Column(jHeadIdx + 1, analyzerConfiguration);
            columns.add(c);
            String typeRef = Optional.ofNullable(oc.getTypeRef()).map(QName::getLocalPart).orElse("any");
            ObjectField of = new ObjectField(OBJECT_TYPE_NAME, typeRef, typeRef, analyzerConfiguration);
            objectFields.add(of);
            objectType.getFields().add(of);
            jHeadIdx++;
        }

        for (int jRowIdx = 0; jRowIdx < dt.getRule().size(); jRowIdx++) {
            DecisionRule r = dt.getRule().get(jRowIdx);

            final Pattern pattern = new Pattern(OBJECT_TYPE_NAME, objectType, analyzerConfiguration);
            final Rule row = new Rule(jRowIdx + 1, analyzerConfiguration);

            int jColIdx = 0;
            List<Field> fields = new ArrayList<>();
            for (UnaryTests ie : r.getInputEntry()) {
                ObjectField objectField = objectFields.get(jColIdx);
                final Field field = new Field(objectField, objectField.getFactType(), objectField.getFieldType(), objectField.getName(), analyzerConfiguration);
                fields.add(field);
                pattern.getFields().add(field);
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) DMNDTValidator.FEEL.compileUnaryTests(ie.getText(), DMNDTValidator.FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getExpr().getExpression();

                if (utln.getElements().size() > 1) {
                    throw new UnsupportedOperationException("TODO: verify drools-verifier for multiple UT in the same cell.");
                }
                for (BaseNode n : utln.getElements()) {
                    if (n instanceof DashNode) {
                        continue;
                    }
                    UnaryTestNode ut = (UnaryTestNode) n;
                    if (ut.getOperator() == UnaryOperator.EQ || ut.getOperator() == UnaryOperator.GT || ut.getOperator() == UnaryOperator.GTE || ut.getOperator() == UnaryOperator.LT || ut
                                                                                                                                                                                           .getOperator() == UnaryOperator.LTE) {
                        FieldCondition<?> condition1 = new FieldCondition<>(field,
                                                                            columns.get(jColIdx),
                                                                            validatorStringOperatorFromUTOperator(ut.getOperator()),
                                                                            valuesFromNode(ut.getValue()),
                                                                            analyzerConfiguration);
                        field.getConditions().add(condition1);
                        row.getConditions().add(condition1);
                    } else if (ut.getValue() instanceof RangeNode) {
                        RangeNode rangeNode = (RangeNode) ut.getValue();
                        FieldCondition<?> condition1 = new FieldCondition<>(field,
                                                                            columns.get(jColIdx),
                                                                            rangeNode.getLowerBound() == IntervalBoundary.OPEN ? ">" : ">=",
                                                                            valuesFromNode(rangeNode.getStart()),
                                                                            analyzerConfiguration);
                        field.getConditions().add(condition1);
                        row.getConditions().add(condition1);
                        FieldCondition<?> condition2 = new FieldCondition<>(field,
                                                                            columns.get(jColIdx),
                                                                            rangeNode.getUpperBound() == IntervalBoundary.OPEN ? "<" : "<=",
                                                                            valuesFromNode(rangeNode.getEnd()),
                                                                            analyzerConfiguration);
                        field.getConditions().add(condition2);
                        row.getConditions().add(condition2);
                    } else {
                        throw new UnsupportedOperationException("TODO");
                    }
                }
                jColIdx++;
            }
            for (LiteralExpression oe : r.getOutputEntry()) {
                ObjectField objectField = objectFields.get(jColIdx);
                final Field field = new Field(objectField, objectField.getFactType(), objectField.getFieldType(), objectField.getName(), analyzerConfiguration);
                fields.add(field);
                pattern.getFields().add(field);

                ProcessedExpression compileUnaryTests = (ProcessedExpression) DMNDTValidator.FEEL.compile(oe.getText(), DMNDTValidator.FEEL.newCompilerContext());
                InterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                BaseNode baseNode = (BaseNode) interpreted.getExpr().getExpression();

                final FieldAction action = new FieldAction(field, columns.get(jColIdx), dataTypeFromNode(baseNode), valuesFromNode(baseNode), analyzerConfiguration);
                field.getActions().add(action);
                row.getActions().add(action);
                jColIdx++;
            }

            row.getPatterns().add(pattern);
            index1.getRules().add(row);
        }

        final Analyzer analyzer = new Analyzer(new Reporter() {

            @Override
            public void sendReport(Set<Issue> issues) {
                for (Issue issue : issues) {
                    System.out.println("issue.getRowNumbers   " + issue.getRowNumbers());
                    System.out.println("issue.getCheckType    " + issue.getCheckType());
                    System.out.println("issue.getDebugMessage " + issue.getDebugMessage());
                }
            }

            @Override
            public void sendStatus(Status status) {
                System.out.println("status.getWebWorkerUUID   " + status.getWebWorkerUUID());
                System.out.println("status.getStart           " + status.getStart());
                System.out.println("status.getEnd             " + status.getEnd());
                System.out.println("status.getTotalCheckCount " + status.getTotalCheckCount());
            }
        },
                                               index1,
                                               analyzerConfiguration);

        analyzer.start();
        analyzer.analyze();
    }

    private static String validatorStringOperatorFromUTOperator(UnaryOperator operator) {
        switch (operator) {
            case EQ:
                return "==";
            case GT:
                return ">";
            case GTE:
                return ">=";
            case LT:
                return "<";
            case LTE:
                return "<=";
            case IN:
            case NE:
            case NOT:
            case TEST:
            default:
                return null;
        }
    }

    private static DataTypes dataTypeFromNode(BaseNode baseNode) {
        if (baseNode instanceof NumberNode) {
            return DataType.DataTypes.NUMERIC_BIGDECIMAL;
        } else if (baseNode instanceof StringNode) {
            return DataType.DataTypes.STRING;
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    private static Values<?> valuesFromNode(BaseNode start) {
        if (start instanceof NumberNode) {
            NumberNode numberNode = (NumberNode) start;
            return new Values<>(numberNode.getValue());
        } else if (start instanceof StringNode) {
            StringNode stringNode = (StringNode) start;
            return new Values<>(stringNode.getText());
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }
}
