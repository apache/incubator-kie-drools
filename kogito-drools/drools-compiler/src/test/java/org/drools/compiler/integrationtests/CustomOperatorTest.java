package org.drools.compiler.integrationtests;

import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

public class CustomOperatorTest extends CommonTestMethodBase {

    @Test
    public void testCustomOperatorCombiningConstraints() {
        // JBRULES-3517
        String str =
                "declare GN\n" +
                "   gNo : Double\n" +
                "end\n" +
                "\n" +
                "declare t547147\n" +
                "   c547148 : String\n" +
                "   c547149 : String\n" +
                "end\n" +
                "\n" +
                "declare Tra48\n" +
                "   gNo : Double\n" +
                "   postCode : String\n" +
                "   name : String\n" +
                "   cnt : String\n" +
                "end\n" +
                "\n" +
                "rule \"r548695.1\"\n" +
                "no-loop true\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "   gnId : GN()\n" +
                "   la : t547147( )\n" +
                "   v1717 : Tra48( gnId.gNo == gNo, name F_str[startsWith] la.c547148 || postCode F_str[contains] la.c547149 )\n" +
                "then\n" +
                "   System.out.println(\"Rule r548695.1 fired\");\n" +
                "end\n";

        KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        builderConf.setOption(EvaluatorOption.get("str", new F_StrEvaluatorDefinition()));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(builderConf, str);
    }

    public static class F_StrEvaluatorDefinition implements EvaluatorDefinition {

        public static final Operator STR_COMPARE = Operator.addOperatorToRegistry("F_str", false);
        public static final Operator NOT_STR_COMPARE = Operator.addOperatorToRegistry("F_str", true);
        private static final String[] SUPPORTED_IDS = {STR_COMPARE.getOperatorString()};

        public enum Operations {

            startsWith, endsWith, length, contains, bidicontains;
        }

        private Evaluator[] evaluator;

        public String[] getEvaluatorIds() {
            return F_StrEvaluatorDefinition.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
            F_StrEvaluator evaluatorLocal = new F_StrEvaluator(type, isNegated);
            evaluatorLocal.setParameterText(parameterText);
            return evaluatorLocal;
        }

        public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText) {
            return getEvaluator(type, operatorId, isNegated, parameterText, Target.FACT, Target.FACT);
        }

        public Evaluator getEvaluator(ValueType type, Operator operator, String parameterText) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), parameterText);
        }

        public Evaluator getEvaluator(ValueType type, Operator operator) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), null);
        }

        public boolean supportsType(ValueType vt) {
            return true;
        }

        public Target getTarget() {
            return Target.FACT;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(evaluator);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            evaluator = (Evaluator[]) in.readObject();
        }
    }

    public static class F_StrEvaluator extends BaseEvaluator {

        private F_StrEvaluatorDefinition.Operations parameter;

        public void setParameterText(String parameterText) {
            this.parameter = F_StrEvaluatorDefinition.Operations.valueOf(parameterText);
        }

        public F_StrEvaluatorDefinition.Operations getParameter() {
            return parameter;
        }

        public F_StrEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? F_StrEvaluatorDefinition.NOT_STR_COMPARE : F_StrEvaluatorDefinition.STR_COMPARE);
        }

        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle);
            String objectValueString = (String) objectValue;
            return evaluateAll((String) value.getValue(), objectValueString);
        }

        public boolean evaluate(InternalWorkingMemory iwm, InternalReadAccessor ira, InternalFactHandle left, InternalReadAccessor ira1, InternalFactHandle right) {
            return evaluateAll((String) left.getObject(), (String) right.getObject());
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {
            final Object valRight = context.extractor.getValue(workingMemory, right);
            return evaluateAll((String) ((ObjectVariableContextEntry) context).left, (String) valRight);
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(workingMemory, left);
            return evaluateAll((String) varLeft, (String) ((ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(String leftString, String rightString) {
            boolean result = ((leftString != null) && (rightString != null));

            if (result) {
                switch (parameter) {
                    case startsWith:
                        result = this.getOperator().isNegated() ^ (leftString.startsWith(rightString));
                        return result;
                    case endsWith:
                        result = this.getOperator().isNegated() ^ (leftString.endsWith(rightString));
                        return result;
                }
            }
            return result;
        }
    }

    @Test
    public void testCustomOperatorUsingCollections() {
        String str =
                "import org.drools.compiler.Person\n" +
                "import org.drools.compiler.Address\n" +
                "rule R when\n" +
                "    $alice : Person(name == \"Alice\")\n" +
                "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n" +
                "then\n" +
                "end\n";

        KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        builderConf.setOption(EvaluatorOption.get("supersetOf", new SupersetOfEvaluatorDefinition()));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(builderConf, str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person alice = new Person("Alice", 30);
        alice.addAddress(new Address("Large Street", "BigTown", "12345"));
        Person bob = new Person("Bob", 30);
        bob.addAddress(new Address("Large Street", "BigTown", "12345"));
        bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

        ksession.insert(alice);
        ksession.insert(bob);

        assertEquals(1, ksession.fireAllRules());
    }

    public static class SupersetOfEvaluatorDefinition implements EvaluatorDefinition {

        public static final Operator SUPERSET_OF = Operator.addOperatorToRegistry("supersetOf", false);
        public static final Operator NOT_SUPERSET_OF = Operator.addOperatorToRegistry("supersetOf", true);
        private static final String[] SUPPORTED_IDS = {SUPERSET_OF.getOperatorString()};

        private Evaluator[] evaluator;

        public String[] getEvaluatorIds() {
            return SupersetOfEvaluatorDefinition.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
            SupersetOfEvaluator evaluatorLocal = new SupersetOfEvaluator(type, isNegated);
            return evaluatorLocal;
        }

        public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText) {
            return getEvaluator(type, operatorId, isNegated, parameterText, Target.FACT, Target.FACT);
        }

        public Evaluator getEvaluator(ValueType type, Operator operator, String parameterText) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), parameterText);
        }

        public Evaluator getEvaluator(ValueType type, Operator operator) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), null);
        }

        public boolean supportsType(ValueType vt) {
            return true;
        }

        public Target getTarget() {
            return Target.FACT;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(evaluator);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            evaluator = (Evaluator[]) in.readObject();
        }
    }

    public static class SupersetOfEvaluator extends BaseEvaluator {

        public SupersetOfEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? SupersetOfEvaluatorDefinition.NOT_SUPERSET_OF : SupersetOfEvaluatorDefinition.SUPERSET_OF);
        }

        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle);
            return evaluateAll((Collection) value.getValue(), (Collection) objectValue);
        }

        public boolean evaluate(InternalWorkingMemory iwm, InternalReadAccessor ira, InternalFactHandle left, InternalReadAccessor ira1, InternalFactHandle right) {
            return evaluateAll((Collection) left.getObject(), (Collection) right.getObject());
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {
            final Object valRight = context.extractor.getValue(workingMemory, right.getObject());
            return evaluateAll((Collection) ((ObjectVariableContextEntry) context).left, (Collection) valRight);
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(workingMemory, left);
            return evaluateAll((Collection) varLeft, (Collection) ((ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(Collection leftCollection, Collection rightCollection) {
            return rightCollection.containsAll(leftCollection);
        }
    }

    @Test
    public void testCustomOperatorOnKieModule() {
        String str =
                "import org.drools.compiler.Person\n" +
                "import org.drools.compiler.Address\n" +
                "rule R when\n" +
                "    $alice : Person(name == \"Alice\")\n" +
                "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieSession ksession = new KieHelper()
                .setKieModuleModel(ks.newKieModuleModel()
                                     .setConfigurationProperty("drools.evaluator.supersetOf",
                                                               SupersetOfEvaluatorDefinition.class.getName()))
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        Person alice = new Person("Alice", 30);
        alice.addAddress(new Address("Large Street", "BigTown", "12345"));
        Person bob = new Person("Bob", 30);
        bob.addAddress(new Address("Large Street", "BigTown", "12345"));
        bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

        ksession.insert(alice);
        ksession.insert(bob);

        assertEquals(1, ksession.fireAllRules());
    }
}
