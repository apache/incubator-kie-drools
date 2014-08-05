/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.marshalling.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.Output;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.drools.core.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteAssertAction;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.impl.ProtobufMessages.Header;
import org.drools.core.marshalling.impl.ProtobufMessages.Header.StrategyIndex.Builder;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PropagationQueuingNode.PropagateAction;
import org.drools.core.rule.SlidingTimeWindow.BehaviorExpireWMAction;
import org.drools.core.util.Drools;
import org.drools.core.util.KeyStoreHelper;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategy.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map.Entry;

public class PersisterHelper {
    public static WorkingMemoryAction readWorkingMemoryAction(MarshallerReaderContext context) throws IOException,
                                                                                              ClassNotFoundException {
        int type = context.readShort();
        switch ( type ) {
            case WorkingMemoryAction.WorkingMemoryReteAssertAction : {
                return new WorkingMemoryReteAssertAction( context );
            }
//            case WorkingMemoryAction.DeactivateCallback : {
//                return new DeactivateCallback( context );
//            }
            case WorkingMemoryAction.PropagateAction : {
                return new PropagateAction( context );
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

    public static WorkingMemoryAction deserializeWorkingMemoryAction(MarshallerReaderContext context,
                                                                     ProtobufMessages.ActionQueue.Action _action) throws IOException,
                                                                                                                 ClassNotFoundException {
        switch ( _action.getType() ) {
            case ASSERT : {
                return new WorkingMemoryReteAssertAction( context, 
                                                          _action );
            }
//            case DEACTIVATE_CALLBACK : {
//                return new DeactivateCallback(context,
//                                              _action );
//            }
            case PROPAGATE : {
                return new PropagateAction(context,
                                           _action );
            }
            case LOGICAL_RETRACT : {
                return new BeliefSystemLogicalCallback(context,
                                                       _action );
            }
            case EXPIRE : {
                return new WorkingMemoryReteExpireAction(context,
                                                         _action );
            }
            case BEHAVIOR_EXPIRE : {
                return new BehaviorExpireWMAction( context,
                                                   _action );

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

    public void write(MarshallerWriteContext context) throws IOException {

    }

    public static ProtobufInputMarshaller.ActivationKey createActivationKey(final String pkgName,
                                                                            final String ruleName,
                                                                            final ProtobufMessages.Tuple _tuple) {
        int[] tuple = createTupleArray( _tuple );
        return new ProtobufInputMarshaller.ActivationKey( pkgName, ruleName, tuple );
    }

    public static ProtobufInputMarshaller.ActivationKey createActivationKey(final String pkgName,
                                                                            final String ruleName,
                                                                            final LeftTuple leftTuple) {
        int[] tuple = createTupleArray( leftTuple );
        return new ProtobufInputMarshaller.ActivationKey( pkgName, ruleName, tuple );
    }

    public static ProtobufMessages.Tuple createTuple( final LeftTuple leftTuple ) {
        ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
        for( LeftTuple entry = leftTuple; entry != null ; entry = entry.getParent() ) {
            if ( entry.getLastHandle() != null ) {
                // can be null for eval, not and exists that have no right input
                _tuple.addHandleId( entry.getLastHandle().getId() );
            }
        }
        return _tuple.build();
    }
    
    public static int[] createTupleArray(final ProtobufMessages.Tuple _tuple) {
        int[] tuple = new int[_tuple.getHandleIdCount()];
        for ( int i = 0; i < tuple.length; i++ ) {
            // needs to reverse the tuple elements 
            tuple[i] = _tuple.getHandleId( tuple.length - i - 1 );
        }
        return tuple;
    }

    public static int[] createTupleArray(final LeftTuple leftTuple) {
        if( leftTuple != null ) {
            int[] tuple = new int[leftTuple.size()];
            // tuple iterations happens backwards
            int i = tuple.length;
            for( LeftTuple entry = leftTuple; entry != null && i > 0; entry = entry.getParent() ) {
                if ( entry.getLastHandle() != null ) {
                    // can be null for eval, not and exists that have no right input
                    // have to decrement i before assignment
                    tuple[--i] = entry.getLastHandle().getId();
                }
            }
            return tuple;
        } else {
            return new int[0];
        }
    }

    public static ProtobufInputMarshaller.TupleKey createTupleKey(final ProtobufMessages.Tuple _tuple) {
        return new ProtobufInputMarshaller.TupleKey( createTupleArray( _tuple ) );
    }
    
    public static ProtobufInputMarshaller.TupleKey createTupleKey(final LeftTuple leftTuple) {
        return new ProtobufInputMarshaller.TupleKey( createTupleArray( leftTuple ) );
    }
    
    public static ProtobufMessages.Activation createActivation(final String packageName,
                                                               final String ruleName,
                                                               final LeftTuple tuple) {
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
        
        byte[] buff = payload.toByteArray();
        sign( _header, buff );
        _header.setPayload( ByteString.copyFrom( buff ) );

//        LoggerFactory.getLogger(PersisterHelper.class).trace("=============================================================================================================");
//        LoggerFactory.getLogger(PersisterHelper.class).trace(payload);
        context.stream.write( _header.build().toByteArray() );
    }
    
    private static void writeStrategiesIndex(MarshallerWriteContext context,
                                             ProtobufMessages.Header.Builder _header) throws IOException {
        for( Entry<ObjectMarshallingStrategy,Integer> entry : context.usedStrategies.entrySet() ) {
            Builder _strat = ProtobufMessages.Header.StrategyIndex.newBuilder()
                                     .setId( entry.getValue().intValue() )
                                     .setName( entry.getKey().getClass().getName() );
            Context ctx = context.strategyContext.get( entry.getKey() );
            if( ctx != null ) {
                Output os = ByteString.newOutput();
                ctx.write( new DroolsObjectOutputStream( os ) );
                _strat.setData( os.toByteString() );
                os.close();
            }
            _header.addStrategy( _strat.build() );
        }
    }

    private static void sign(ProtobufMessages.Header.Builder _header,
                             byte[] buff ) {
        KeyStoreHelper helper = new KeyStoreHelper();
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
    
    private static ProtobufMessages.Header loadStrategiesCheckSignature(MarshallerReaderContext context, ProtobufMessages.Header _header) throws ClassNotFoundException, IOException {
        loadStrategiesIndex( context, _header );

        byte[] sessionbuff = _header.getPayload().toByteArray();

        // should we check version as well here?
        checkSignature( _header, sessionbuff );
        
        return _header;
    }

    public static ProtobufMessages.Header readFromStreamWithHeaderPreloaded( MarshallerReaderContext context, ExtensionRegistry registry ) throws IOException, ClassNotFoundException {
        // we preload the stream into a byte[] to overcome a message size limit
        // imposed by protobuf as per https://issues.jboss.org/browse/DROOLS-25
        byte[] preloaded = preload(context.stream);
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

	private static void loadStrategiesIndex(MarshallerReaderContext context,
                                            ProtobufMessages.Header _header) throws IOException, ClassNotFoundException {
        for ( ProtobufMessages.Header.StrategyIndex _entry : _header.getStrategyList() ) {
            ObjectMarshallingStrategy strategyObject = context.resolverStrategyFactory.getStrategyObject( _entry.getName() );
            if ( strategyObject == null ) {
                throw new IllegalStateException( "No strategy of type " + _entry.getName() + " available." );
            }
            context.usedStrategies.put( _entry.getId(), strategyObject );
            Context ctx = strategyObject.createContext();
            context.strategyContexts.put( strategyObject, ctx );
            if( _entry.hasData() && ctx != null ) {
		ClassLoader classLoader = null;
                if (context.classLoader != null ){
                    classLoader = context.classLoader;
                } else if(context.kBase != null){
                    classLoader = context.kBase.getRootClassLoader();
                }
                ctx.read( new DroolsObjectInputStream( _entry.getData().newInput(), classLoader) );
            }
        }
    }

    private static void checkSignature(Header _header,
                                       byte[] sessionbuff) {
        KeyStoreHelper helper = new KeyStoreHelper();
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
    
    public static ExtensionRegistry buildRegistry(MarshallerReaderContext context, ProcessMarshaller processMarshaller ) {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        if( processMarshaller != null ) {
            context.parameterObject = registry;
            processMarshaller.init( context );
        }
        return registry;
    }
    
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value  & 0xFF) };
    }    
    
    public static final int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }
    
    // more efficient than instantiating byte buffers and opening streams
    public static final byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) ((value >>> 56) & 0xFF),
                (byte) ((value >>> 48) & 0xFF),
                (byte) ((value >>> 40) & 0xFF),
                (byte) ((value >>> 32) & 0xFF),
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)};
    }

    public static final long byteArrayToLong(byte[] b) {
        return ((((long)b[0]) & 0xFF) << 56)
               + ((((long)b[1]) & 0xFF) << 48)
               + ((((long)b[2]) & 0xFF) << 40)
               + ((((long)b[3]) & 0xFF) << 32)
               + ((((long)b[4]) & 0xFF) << 24)
               + ((((long)b[5]) & 0xFF) << 16)
               + ((((long)b[6]) & 0xFF) << 8)
               + (((long)b[7]) & 0xFF);
    }    


}
