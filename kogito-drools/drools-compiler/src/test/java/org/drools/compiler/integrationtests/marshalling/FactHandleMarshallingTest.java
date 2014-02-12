package org.drools.compiler.integrationtests.marshalling;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.compiler.integrationtests.marshalling.util.OldOutputMarshallerMethods;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.InputMarshaller;
import org.drools.core.marshalling.impl.MarshallerProviderImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ObjectMarshallingStrategyStoreImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.Rete;
import org.drools.core.rule.EntryPointId;
import org.drools.core.reteoo.builder.NodeFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;

public class FactHandleMarshallingTest {

    private InternalKnowledgeBase createKnowledgeBase() {
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        return (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase( config );
    }
    
    private InternalFactHandle createEventFactHandle(StatefulKnowledgeSessionImpl wm, InternalKnowledgeBase kBase) {
        // EntryPointNode
        Rete rete = kBase.getRete();

        NodeFactory nFacotry = kBase.getConfiguration().getComponentFactory().getNodeFactoryService();

        RuleBasePartitionId partionId = new RuleBasePartitionId("P-MAIN");
        EntryPointNode entryPointNode = nFacotry.buildEntryPointNode(1, partionId, false, (ObjectSource) rete , EntryPointId.DEFAULT);
        EntryPoint wmEntryPoint = new NamedEntryPoint(EntryPointId.DEFAULT, entryPointNode, wm);

        EventFactHandle factHandle = new EventFactHandle(1, (Object) new Person(),0, (new Date()).getTime(), 0, wmEntryPoint);
        
        return factHandle;
    }
       
    private StatefulKnowledgeSessionImpl createWorkingMemory(InternalKnowledgeBase kBase) {
        // WorkingMemoryEntryPoint
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        SessionConfiguration sessionConf = ((SessionConfiguration) ksconf);
        StatefulKnowledgeSessionImpl wm = new StatefulKnowledgeSessionImpl(1, kBase, true, sessionConf, EnvironmentFactory.newEnvironment());
        
        return wm;
    }
    
    @Test
    public void backwardsCompatibleEventFactHandleTest() throws IOException, ClassNotFoundException {
        InternalKnowledgeBase kBase = createKnowledgeBase();
        StatefulKnowledgeSessionImpl wm = createWorkingMemory(kBase);
        InternalFactHandle factHandle = createEventFactHandle(wm, kBase);
        
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStoreImpl(strats), true, true, null);
            OldOutputMarshallerMethods.writeFactHandle_v1(outContext, (ObjectOutputStream) outContext, 
                    outContext.objectMarshallingStrategyStore, 2, factHandle);
            outContext.close();
            byteArray = baos.toByteArray();
        }
        
        // unmarshall/deserialize workItem
        InternalFactHandle newFactHandle;
        {
            // Only put serialization strategy in 
            ObjectMarshallingStrategy[] newStrats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy()  };
    
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStoreImpl(newStrats), Collections.EMPTY_MAP,
                true, true, null);
            inContext.wm = wm;
            newFactHandle = InputMarshaller.readFactHandle(inContext);
            inContext.close();
        }

        assertTrue( "Serialized FactHandle not the same as the original.", compareInstances(factHandle, newFactHandle) );
    }

    private boolean compareInstances(Object objA, Object objB) { 
        boolean same = true;
        if( objA != null && objB != null ) { 
            if( ! objA.getClass().equals(objB.getClass()) ) { 
                return false;
            }
            String className = objA.getClass().getName();
            if( className.startsWith("java") ) { 
                return objA.equals(objB);
            }
            
            try { 
                Field [] fields = objA.getClass().getDeclaredFields();
                if( fields.length == 0 ) { 
                    same = true;
                }
                else { 
                    for( int i = 0; same && i < fields.length; ++i ) { 
                        fields[i].setAccessible(true);
                        Object subObjA = fields[i].get(objA);
                        Object subObjB = fields[i].get(objB);
                        if( ! compareInstances(subObjA, subObjB) ) { 
                           return false; 
                        }
                    }
                }
            }
            catch( Exception e ) { 
                same = false;
                Assert.fail(e.getClass().getSimpleName() + ":" + e.getMessage() );
            }
        }
        else if( objA != objB ) { 
            return false;
        }
        
        return same;
    }
}
