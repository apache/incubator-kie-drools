/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.serialization.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.Output;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.drools.tms.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.base.factmodel.traits.TraitFactory;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.serialization.protobuf.marshalling.ActivationKey;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.serialization.protobuf.marshalling.MarshallingHelper;
import org.drools.serialization.protobuf.marshalling.ProcessMarshaller;
import org.drools.core.marshalling.TupleKey;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.rule.SlidingTimeWindow.BehaviorExpireWMAction;
import org.drools.core.reteoo.Tuple;
import org.drools.base.util.Drools;
import org.drools.core.util.KeyStoreHelper;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.serialization.protobuf.ProtobufMessages.Header;
import org.drools.serialization.protobuf.ProtobufMessages.Header.StrategyIndex.Builder;
import org.drools.serialization.protobuf.actions.ProtobufBehaviorExpireWMAction;
import org.drools.serialization.protobuf.actions.ProtobufBeliefSystemLogicalCallback;
import org.drools.serialization.protobuf.actions.ProtobufWorkingMemoryReteAssertAction;
import org.drools.serialization.protobuf.actions.ProtobufWorkingMemoryReteExpireAction;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategy.Context;

public class PersisterHelper extends MarshallingHelper {

    public static WorkingMemoryAction readWorkingMemoryAction( MarshallerReaderContext context) throws IOException {
        int type = context.readShort();
        switch ( type ) {
            case WorkingMemoryAction.WorkingMemoryReteAssertAction : {
                return new WorkingMemoryReteAssertAction( context );
            }
            case WorkingMemoryAction.LogicalRetractCallback : {
                return new BeliefSystemLogicalCallback( context );
            }
            case WorkingMemoryAction.WorkingMemoryReteExpireAction : {
                return new WorkingMemoryReteExpireAction( context );
            }
            case WorkingMemoryAction.WorkingMemoryBehahviourRetract : {
                return new BehaviorExpireWMAction( context );

            }
        }
        return null;
    }

    public static WorkingMemoryAction deserializeWorkingMemoryAction( MarshallerReaderContext context,
                                                                      ProtobufMessages.ActionQueue.Action _action) throws IOException {
        switch ( _action.getType() ) {
            case ASSERT : {
                return new ProtobufWorkingMemoryReteAssertAction( context, _action );
            }
            case LOGICAL_RETRACT : {
                return new ProtobufBeliefSystemLogicalCallback(context, _action );
            }
            case EXPIRE : {
                return new ProtobufWorkingMemoryReteExpireAction(context, _action );
            }
            case BEHAVIOR_EXPIRE : {
                return new ProtobufBehaviorExpireWMAction( context, _action );

            }         
            case SIGNAL : {
                // need to fix this
            }
            case SIGNAL_PROCESS_INSTANCE : {
                // need to fix this
            }
        }
        return null;
    }

    public void write( ProtobufMarshallerWriteContext context) throws IOException {

    }

    public static ActivationKey createActivationKey( String pkgName, String ruleName, ProtobufMessages.Tuple _tuple) {
        return createActivationKey( pkgName, ruleName, toArrayOfObject(createTupleArray( _tuple )) );
    }

    public static ProtobufMessages.Tuple createTuple( final Tuple leftTuple ) {
        ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
        for( Tuple entry = leftTuple.skipEmptyHandles(); entry != null ; entry = entry.getParent() ) {
            _tuple.addHandleId( entry.getFactHandle().getId() );
        }
        return _tuple.build();
    }
    
    public static long[] createTupleArray(final ProtobufMessages.Tuple _tuple) {
        long[] tuple = new long[_tuple.getHandleIdCount()];
        for ( int i = 0; i < tuple.length; i++ ) {
            // needs to reverse the tuple elements 
            tuple[i] = _tuple.getHandleId( tuple.length - i - 1 );
        }
        return tuple;
    }

    public static TupleKey createTupleKey( final ProtobufMessages.Tuple _tuple) {
        return new TupleKey( createTupleArray( _tuple ));
    }
    
    public static ProtobufMessages.Activation createActivation(final String packageName,
                                                               final String ruleName,
                                                               final Tuple tuple) {
        return ProtobufMessages.Activation.newBuilder()
                        .setPackageName( packageName )
                        .setRuleName( ruleName )
                        .setTuple( createTuple( tuple ) )
                        .build();
    }
    
    public static void writeToStreamWithHeader( MarshallerWriteContext context,
                                                Message payload ) throws IOException {
        ProtobufMessages.Header.Builder _header = ProtobufMessages.Header.newBuilder();
        _header.setVersion( ProtobufMessages.Version.newBuilder()
                                            .setVersionMajor( Drools.getMajorVersion() )
                                            .setVersionMinor( Drools.getMinorVersion() )
                                            .setVersionRevision( Drools.getRevisionVersion() )
                            .build() );
        
        writeStrategiesIndex( context, _header );

        InternalRuleBase kBase = context.getKnowledgeBase();
        if (kBase != null) {
            TraitFactory traitFactory = RuntimeComponentFactory.get().getTraitFactory(kBase);
            if (traitFactory != null) {
                writeRuntimeDefinedClasses(traitFactory, context, _header);
            }
        }

        byte[] buff = payload.toByteArray();
        sign( _header, buff );
        _header.setPayload( ByteString.copyFrom( buff ) );

        context.write( _header.build().toByteArray() );
    }

    private static void writeRuntimeDefinedClasses( TraitFactory traitFactory, MarshallerWriteContext context, ProtobufMessages.Header.Builder _header) {
        if (context.getKnowledgeBase() == null) {
            return;
        }

        ProjectClassLoader pcl = (ProjectClassLoader) (context.getKnowledgeBase()).getRootClassLoader();
        if (pcl.getStore() == null || pcl.getStore().isEmpty()) {
            return;
        }

        List<String> runtimeClassNames = new ArrayList(pcl.getStore().keySet());
        Collections.sort(runtimeClassNames);
        ProtobufMessages.RuntimeClassDef.Builder _classDef = ProtobufMessages.RuntimeClassDef.newBuilder();
        for (String resourceName : runtimeClassNames) {
            if (traitFactory.isRuntimeClass(resourceName)) {
                _classDef.clear();
                _classDef.setClassFqName(resourceName);
                _classDef.setClassDef(ByteString.copyFrom(pcl.getStore().get(resourceName)));
                _header.addRuntimeClassDefinitions(_classDef.build());
            }
        }
    }

    private static void writeStrategiesIndex( MarshallerWriteContext context,
                                              ProtobufMessages.Header.Builder _header) throws IOException {
        for( Entry<ObjectMarshallingStrategy,Integer> entry : context.getUsedStrategies().entrySet() ) {
			Builder _strat = ProtobufMessages.Header.StrategyIndex.newBuilder()
                                     .setId( entry.getValue().intValue() )
                                     .setName( entry.getKey().getName()  );
			
            Context ctx = context.getStrategyContext().get( entry.getKey() );
            if( ctx != null ) {
                try (Output os = ByteString.newOutput()) {
                    ctx.write( new DroolsObjectOutputStream( os ) );
                    _strat.setData( os.toByteString() );
                }
            }
            _header.addStrategy( _strat.build() );
        }
    }

    private static void sign(ProtobufMessages.Header.Builder _header,
                             byte[] buff ) {
        KeyStoreHelper helper = KeyStoreHelper.get();
        if (helper.isSigned()) {
            try {
                _header.setSignature( ProtobufMessages.Signature.newBuilder()
                                      .setKeyAlias( helper.getPvtKeyAlias() )
                                      .setSignature( ByteString.copyFrom( helper.signDataWithPrivateKey( buff ) ) )
                                      .build() );
            } catch (Exception e) {
                throw new RuntimeException( "Error signing session: " + e.getMessage(),
                                            e );
            }
        }
    }
    
    private static ProtobufMessages.Header loadStrategiesCheckSignature( MarshallerReaderContext context, ProtobufMessages.Header _header) throws ClassNotFoundException, IOException {
        loadStrategiesIndex( context, _header );

        byte[] sessionbuff = _header.getPayload().toByteArray();

        // should we check version as well here?
        checkSignature( _header, sessionbuff );
        
        return _header;
    }

    public static ProtobufMessages.Header readFromStreamWithHeaderPreloaded( MarshallerReaderContext context, ExtensionRegistry registry ) throws IOException, ClassNotFoundException {
        // we preload the stream into a byte[] to overcome a message size limit
        // imposed by protobuf as per https://issues.jboss.org/browse/DROOLS-25
        byte[] preloaded = preload((InputStream) context);
        ProtobufMessages.Header _header = ProtobufMessages.Header.parseFrom( preloaded, registry );

        return loadStrategiesCheckSignature(context, _header);
    }
    
    /* Method that preloads the source stream into a byte array to bypass the message size limitations in Protobuf unmarshalling.
       (Protobuf does not enforce a message size limit when unmarshalling from a byte array)
    */
    private static byte[] preload(InputStream stream) throws IOException {
        byte[] buf = new byte[4096];
        ByteArrayOutputStream preloaded = new ByteArrayOutputStream();

        int read;
        while((read = stream.read(buf)) != -1) {
            preloaded.write(buf, 0, read);
        }

        return preloaded.toByteArray();
    }

	private static void loadStrategiesIndex( MarshallerReaderContext context, ProtobufMessages.Header _header) throws IOException, ClassNotFoundException {
        for ( ProtobufMessages.Header.StrategyIndex _entry : _header.getStrategyList() ) {
            ObjectMarshallingStrategy strategyObject = context.getResolverStrategyFactory().getStrategyObject( _entry.getName() );
            if ( strategyObject == null ) {
                throw new IllegalStateException( "No strategy of type " + _entry.getName() + " available." );
            }
            context.getUsedStrategies().put( _entry.getId(), strategyObject );
            Context ctx = strategyObject.createContext();
            context.getStrategyContexts().put( strategyObject, ctx );
            if( _entry.hasData() && ctx != null ) {
		        ClassLoader classLoader = null;
                if (context.getClassLoader() != null ){
                    classLoader = context.getClassLoader();
                } else if(context.getKnowledgeBase() != null){
                    classLoader = context.getKnowledgeBase().getRootClassLoader();
                }
                if ( classLoader instanceof ProjectClassLoader ) {
                   readRuntimeDefinedClasses( _header, (ProjectClassLoader) classLoader );
                }
                ctx.read( new DroolsObjectInputStream( _entry.getData().newInput(), classLoader) );
            }
        }
    }

    public static void readRuntimeDefinedClasses( Header _header,
                                                  ProjectClassLoader pcl ) throws IOException, ClassNotFoundException {
        if ( _header.getRuntimeClassDefinitionsCount() > 0 ) {
            for ( ProtobufMessages.RuntimeClassDef def : _header.getRuntimeClassDefinitionsList() ) {
                String resourceName = def.getClassFqName();
                byte[] byteCode = def.getClassDef().toByteArray();
                if ( ! pcl.getStore().containsKey( resourceName ) ) {
                    pcl.getStore().put(resourceName, byteCode);
                }
            }
        }
    }

    private static void checkSignature(Header _header,
                                       byte[] sessionbuff) {
        KeyStoreHelper helper = KeyStoreHelper.get();
        boolean signed = _header.hasSignature();
        if ( helper.isSigned() != signed ) {
            throw new RuntimeException( "This environment is configured to work with " +
                                        (helper.isSigned() ? "signed" : "unsigned") +
                                        " serialized objects, but the given object is " +
                                        (signed ? "signed" : "unsigned") + ". Deserialization aborted." );
        }
        if ( signed ) {
            if ( helper.getPubKeyStore() == null ) {
                throw new RuntimeException( "The session was serialized with a signature. Please configure a public keystore with the public key to check the signature. Deserialization aborted." );
            }
            try {
                if ( !helper.checkDataWithPublicKey( _header.getSignature().getKeyAlias(),
                                                     sessionbuff,
                                                     _header.getSignature().getSignature().toByteArray() ) ) {
                    throw new RuntimeException( "Signature does not match serialized package. This is a security violation. Deserialisation aborted." );
                }
            } catch ( InvalidKeyException e ) {
                throw new RuntimeException( "Invalid key checking signature: " + e.getMessage(),
                                            e );
            } catch ( KeyStoreException e ) {
                throw new RuntimeException( "Error accessing Key Store: " + e.getMessage(),
                                            e );
            } catch ( NoSuchAlgorithmException e ) {
                throw new RuntimeException( "No algorithm available: " + e.getMessage(),
                                            e );
            } catch ( SignatureException e ) {
                throw new RuntimeException( "Signature Exception: " + e.getMessage(),
                                            e );
            }
        }
    }
    
    public static ExtensionRegistry buildRegistry( MarshallerReaderContext context, ProcessMarshaller processMarshaller ) {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        if( processMarshaller != null ) {
            context.setParameterObject( registry );
            processMarshaller.init( context );
        }
        return registry;
    }
}
