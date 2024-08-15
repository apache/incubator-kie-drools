package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.Pattern;
import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.SequenceNode;
import org.drools.base.reteoo.DynamicFilterProto;
import org.drools.core.reteoo.SequenceNode.SequenceNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.SequencerMemory;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.drools.mvel.integrationtests.phreak.E;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ThreadSafeOption;
import org.kie.internal.conf.CompositeBaseConfiguration;

import java.util.Collections;

public class AbstractPhreakSequencerSubsequenceTest {
    StatefulKnowledgeSessionImpl session;
    SequenceNodeMemory nodeMemory;
    SequencerMemory    sequencerMemory;
    BuildContext       buildContext;
    Sequence       seq0;
    Sequence       seq1;
    Sequence                   seq2;
    SequenceNode               snode;

    Pattern bpattern;
    Pattern cpattern;
    Pattern dpattern;
    Pattern epattern;

    RuleImpl                 rule;
    InternalKnowledgePackage pkg;
    SessionsAwareKnowledgeBase kbase;

    DynamicFilterProto bfilter;
    DynamicFilterProto cfilter;
    DynamicFilterProto dfilter;
    DynamicFilterProto efilter;

    public void initKBaseWithEmptyRule() {
        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID", conf );

        kbase = new SessionsAwareKnowledgeBase(rbase);

        rule = new RuleImpl("rule1").setPackage("org.pkg1");
        pkg  = CoreComponentFactory.get().createKnowledgePackage("org.pkg1");
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData());
        pkg.addRule( rule );

        final ObjectType aObjectType = new ClassObjectType(A.class);
        final ObjectType bObjectType = new ClassObjectType(B.class);
        final ObjectType cObjectType = new ClassObjectType(C.class);
        final ObjectType dObjectType = new ClassObjectType(D.class);
        final ObjectType eObjectType = new ClassObjectType(E.class);

        Pattern aPattern = new Pattern(0, aObjectType);
        rule.addPattern(aPattern);

        bpattern = new Pattern(0, bObjectType, "b" );
        bpattern.addConstraint(new AlphaConstraint((Predicate1<B>) b -> b.getText().equals("b")));

        cpattern = new Pattern(0, cObjectType, "c" );
        cpattern.addConstraint(new AlphaConstraint( (Predicate1<C>) c -> c.getText().equals("c")));

        dpattern = new Pattern(0, dObjectType, "d" );
        dpattern.addConstraint(new AlphaConstraint( (Predicate1<D>) d -> d.getText().equals("d")));

        epattern = new Pattern(0, eObjectType, "e" );
        epattern.addConstraint(new AlphaConstraint( (Predicate1<E>) e -> e.getText().equals("e")));
    }

    public static BuildContext createContext() {

        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID",
                                                        conf );
        BuildContext buildContext = new BuildContext(rbase, Collections.emptyList() );

        RuleImpl                 rule = new RuleImpl("rule1").setPackage("org.pkg1");
        InternalKnowledgePackage pkg  = CoreComponentFactory.get().createKnowledgePackage("org.pkg1");
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData());

        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

    void createSession() {
        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.NO);
        sessionConf.setClockType(ClockType.PSEUDO_CLOCK);

        if (session != null) {
            nodeMemory      = null;
            sequencerMemory = null;
            session.dispose();
            session = null;
        }

        session = (StatefulKnowledgeSessionImpl) kbase.newKieSession(sessionConf, null);

        InternalFactHandle fhA0 = (InternalFactHandle) session.insert(new A(0));
        nodeMemory = session.getNodeMemory(snode);
        LeftTuple lt = new LeftTuple(fhA0, snode, true);
        lt.setContextObject(snode.createSequencerMemory(lt, new MockLeftTupleSink(buildContext.getNextNodeId(), buildContext), nodeMemory));
        nodeMemory.getLeftTupleMemory().add(lt);
        sequencerMemory = (SequencerMemory) lt.getContextObject();
    }

    void createSession2() {
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.NO);
        sessionConf.setClockType(ClockType.PSEUDO_CLOCK);

        if (snode == null) {
            ObjectTypeNode aNode = kbase.getRete().getEntryPointNode(EntryPointId.DEFAULT).getObjectTypeNodes().get(new ClassObjectType(A.class));
            snode = (SequenceNode) aNode.getSinks()[0].getSinks()[0];
        }

        if (session != null) {
            nodeMemory      = null;
            sequencerMemory = null;
            session.dispose();
            session = null;
        }

        session = (StatefulKnowledgeSessionImpl) kbase.newKieSession(sessionConf, null);

        InternalFactHandle fhA0 = (InternalFactHandle) session.insert(new A(0));
        session.fireAllRules();
        nodeMemory = session.getNodeMemory(snode);
        sequencerMemory = (SequencerMemory) fhA0.getFirstLeftTuple().getContextObject();
    }
}
