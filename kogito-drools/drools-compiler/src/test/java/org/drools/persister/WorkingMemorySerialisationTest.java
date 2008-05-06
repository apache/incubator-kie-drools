package org.drools.persister;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Package;

public class WorkingMemorySerialisationTest extends TestCase {

    public void test1() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ));
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2, nodes.size() );
        assertEquals( "Person", ((ClassObjectType)((ObjectTypeNode)nodes.get(3)).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1", ((RuleTerminalNode)nodes.get(5)).getRule().getName() );
        
        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        
        session.insert( p );
        
        PlaceholderResolverStrategyFactory factory = new PlaceholderResolverStrategyFactory();
        factory.addPlaceholderResolverStrategy( new SerializablePlaceholderResolverStrategy() );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputPersister op = new OutputPersister( (InternalRuleBase) ruleBase, ( InternalWorkingMemory )session, new ObjectOutputStream( baos ), factory);
        op.write();
        
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        InputPersister ip = new InputPersister( (InternalRuleBase) ruleBase, new ObjectInputStream( bais ), factory);        
        
        WorkingMemory wm2 = ip.read();
        wm2.setGlobal( "list", list );
        

        wm2.fireAllRules();

        assertEquals( 1, ((List)wm2.getGlobal("list")).size());
        assertEquals( p, ((List)wm2.getGlobal("list")).get(0));
    }

}