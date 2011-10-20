package org.drools.integrationtests.marshalling;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Date;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.NamedEntryPoint;
import org.drools.common.RuleBasePartitionId;
import org.drools.conf.EventProcessingOption;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.integrationtests.marshalling.util.OldOutputMarshallerMethods;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ObjectMarshallingStrategyStore;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.Rete;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.EntryPoint;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.junit.Assert;
import org.junit.Test;

public class FactHandleMarshallingTest {

    private RuleBase createRuleBase() { 
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( config );
        RuleBase ruleBase = ((KnowledgeBaseImpl) kbase).getRuleBase();
        return ruleBase;
    }
    
    private InternalFactHandle createEventFactHandle(AbstractWorkingMemory wm, RuleBase ruleBase) { 
        // EntryPointNode
        Rete rete = ((ReteooRuleBase) ruleBase).getRete();
        RuleBasePartitionId partionId = new RuleBasePartitionId("P-MAIN");
        EntryPointNode entryPointNode = new EntryPointNode(1, partionId, false, (ObjectSource) rete , EntryPoint.DEFAULT);
        WorkingMemoryEntryPoint wmEntryPoint = new NamedEntryPoint(EntryPoint.DEFAULT, entryPointNode, wm);
        EventFactHandle factHandle = new EventFactHandle(1, (Object) new Person(),0, (new Date()).getTime(), 0, wmEntryPoint);
        
        return factHandle;
    }
       
    private AbstractWorkingMemory createWorkingMemory(RuleBase ruleBase) { 
        // WorkingMemoryEntryPoint
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        SessionConfiguration sessionConf = ((SessionConfiguration) ksconf);
        AbstractWorkingMemory wm = new ReteooWorkingMemory(1, (InternalRuleBase) ruleBase, 
                sessionConf, EnvironmentFactory.newEnvironment());
        
        return wm;
    }
    
    @Test
    public void backwardsCompatibleEventFactHandleTest() throws IOException, ClassNotFoundException { 
        RuleBase ruleBase = createRuleBase();
        AbstractWorkingMemory wm = createWorkingMemory(ruleBase);
        InternalFactHandle factHandle = createEventFactHandle(wm, ruleBase);
        
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStore(strats), true, true, null);
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
                new ObjectMarshallingStrategyStore(newStrats), true, true, null);
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
