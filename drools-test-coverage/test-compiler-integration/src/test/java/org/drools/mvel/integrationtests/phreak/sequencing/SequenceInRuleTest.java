package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.DynamicFilterProto;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.drools.mvel.integrationtests.phreak.E;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.AlphaConstraint;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.Predicate1;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.CompositeBaseConfiguration;

import java.util.Collections;

public class SequenceInRuleTest {
    SessionsAwareKnowledgeBase kbase;
    @Before
    public void setup() {
        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID", conf );

        kbase = new SessionsAwareKnowledgeBase(rbase);

        RuleImpl                 rule = new RuleImpl("rule1").setPackage("org.pkg1");
        InternalKnowledgePackage pkg  = CoreComponentFactory.get().createKnowledgePackage("org.pkg1");
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData());
        pkg.addRule( rule );

        //rule.addPattern();

//        buildContext = createContext();
//        buildContext.getRuleBase().getRuleBaseConfiguration().setOption(EventProcessingOption.STREAM);
//
//        MultiInputNodeBuilder builder = MultiInputNodeBuilder.create(buildContext);
//
//        mnode = builder.buildNode(A.class, new Class[]{B.class, C.class, D.class});
//
        final ObjectType aObjectType = new ClassObjectType(A.class);
        final ObjectType bObjectType = new ClassObjectType(B.class);
        final ObjectType cObjectType = new ClassObjectType(C.class);

        Pattern aPattern = new Pattern(0, aObjectType);
        rule.addPattern(aPattern);

//        final ObjectType dObjectType = new ClassObjectType(D.class);
//        final ObjectType eObjectType = new ClassObjectType(E.class);
//
        final Pattern bpattern = new Pattern(0,
                                             bObjectType,
                                             "b" );
        bpattern.addConstraint(new AlphaConstraint((Predicate1<B>) b -> b.getText().equals("b")));

        final Pattern cpattern = new Pattern(0,
                                             cObjectType,
                                             "c" );
        cpattern.addConstraint(new AlphaConstraint( (Predicate1<C>) c -> c.getText().equals("c")));
//
//        final Pattern dpattern = new Pattern(0,
//                                             dObjectType,
//                                             "d" );
//        dpattern.addConstraint(new AlphaConstraint( (Predicate1<D>) d -> d.getText().equals("d")));
//
//        final Pattern epattern = new Pattern(0,
//                                             eObjectType,
//                                             "e" );
//        epattern.addConstraint(new AlphaConstraint( (Predicate1<E>) e -> e.getText().equals("e")));
//
//        DynamicFilterProto bfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) bpattern.getConstraints().get(0), 0);
//        DynamicFilterProto cfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) cpattern.getConstraints().get(0), 1);

        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask), 0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit1 = new LogicCircuit(gate1);
        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);

        rbase.addPackage(pkg);

//        buildContext.getRule().addSequence(seq);

//        KnowledgePackageImpl pkg = new KnowledgePackageImpl("org.default");
//        pkg.addRule(rule);
//
//        buildContext.getRuleBase()

    }

    @Test
    public void tes1() {
        KieSession session = kbase.newKieSession();
        session.insert(new A(0));
        session.fireAllRules();
        session.insert(new B(0, "b"));
        session.insert(new C(0, "c"));
        session.fireAllRules();
    }
}
