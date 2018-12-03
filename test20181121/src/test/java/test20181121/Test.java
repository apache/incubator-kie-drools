package test20181121;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drools.javaparser.ast.CompilationUnit;
import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.feel.codegen.feel11.CompilerBytecodeLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class Test {

//    @org.junit.Test
//    public void test2() {
//        KieServices kieServices = KieServices.Factory.get();
//
//        KieContainer kieContainer = kieServices.newKieContainer(kieServices.newReleaseId("dmn", "car-damage-responsibility", "1.2"));
//
//        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
//
//        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_17396034-163a-48aa-9a7f-c6eb17f9cc6c", "Car Damage Responsibility");
//    }

    @org.junit.Test
    public void testSolutionCase1() {
        DMNRuntime runtime = createRuntime("META-INF/decision-1.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_17396034-163a-48aa-9a7f-c6eb17f9cc6c", "Car Damage Responsibility");
        assertThat(dmnModel, notNullValue());

    }

    static class MockEventListener implements DMNRuntimeEventListener {

            private List<Integer> selected;
            private List<Integer> matches;

            @Override
            public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {

            }

            @Override
            public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {

            }

            @Override
            public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {

            }

            @Override
            public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {

            }

            @Override
            public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {

            }

            @Override
            public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {

            }

            @Override
            public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {

            }

            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                matches = event.getMatches();
                selected = event.getSelected();
            }

            @Override
            public void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {

            }

            @Override
            public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {

            }

        public List<Integer> getSelected() {
            return selected;
        }

        public List<Integer> getMatches() {
            return matches;
        }
    }

    @org.junit.Test
    public void testDecisionTableDefaultValue() {
        List<CompilationUnit> generatedClasses = new ArrayList<>();
        CompilerBytecodeLoader.generateClassListener = new CompilerBytecodeLoader.GenerateClassListener() {
            @Override
            public void generatedClass(CompilationUnit cu) {
                generatedClasses.add(cu);
            }
        };

        final DMNRuntime runtime = createRuntime( "decisiontable-default-value.dmn", this.getClass() );
        final MockEventListener listener = new MockEventListener();
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "decisiontable-default-value" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.getMessages().toString(), dmnModel.hasErrors(), is( false ) );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 16 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.getMessages().toString(), dmnResult.hasErrors(), is( false ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Approval Status" ), is( "Declined" ) );

        assertThat(listener.matches, is(empty()));
        assertThat(listener.selected, is(empty()));
        assertThat(generatedClasses, is(empty()));
    }

    public static DMNRuntime createRuntime(final String resourceName, final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test-"+ UUID.randomUUID(), "1.0"),
                ks.getResources().newClassPathResource(resourceName, testClass));

        final DMNRuntime runtime = typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);
        return runtime;
    }

    public static DMNRuntime typeSafeGetKieRuntime(final KieContainer kieContainer) {
        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) dmnRuntime).setOption(new RuntimeTypeCheckOption(true));
        return dmnRuntime;
    }

}
