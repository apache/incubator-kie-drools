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

package org.drools.compiler.kie.builder.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.drools.compiler.kie.builder.impl.KieModuleCache.Header;
import org.drools.core.util.Drools;
import org.drools.core.util.KeyStoreHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class KieModuleCacheHelper {
    
    public static void writeToStreamWithHeader( OutputStream stream,
                                                Message payload ) throws IOException {
        KieModuleCache.Header.Builder _header = KieModuleCache.Header.newBuilder();
        // need to automate this version numbering somehow
        _header.setVersion( KieModuleCache.Version.newBuilder()
                            .setVersionMajor( Drools.getMajorVersion() )
                            .setVersionMinor( Drools.getMinorVersion() )
                            .setVersionRevision( Drools.getRevisionVersion() )
                            .build() );
        
        byte[] buff = payload.toByteArray();
        sign( _header, buff );
        _header.setPayload( ByteString.copyFrom( buff ) );

        stream.write( _header.build().toByteArray() );
    }
    
    private static void sign(KieModuleCache.Header.Builder _header,
                             byte[] buff ) {
        KeyStoreHelper helper = new KeyStoreHelper();
        if (helper.isSigned()) {
            try {
                _header.setSignature( KieModuleCache.Signature.newBuilder()
                                      .setKeyAlias( helper.getPvtKeyAlias() )
                                      .setSignature( ByteString.copyFrom( helper.signDataWithPrivateKey( buff ) ) )
                                      .build() );
            } catch (Exception e) {
                throw new RuntimeException( "Error signing session: " + e.getMessage(),
                                            e );
            }
        }
    }
    
    public static KieModuleCache.Header readFromStreamWithHeaderPreloaded( InputStream stream, ExtensionRegistry registry ) throws IOException, ClassNotFoundException {
        // we preload the stream into a byte[] to overcome a message size limit
        // imposed by protobuf as per https://issues.jboss.org/browse/DROOLS-25
        byte[] preloaded = preload(stream);
        KieModuleCache.Header _header = KieModuleCache.Header.parseFrom( preloaded, registry );

        // should we check version as well here?
        checkSignature( _header, _header.getPayload().toByteArray() );

        return _header;
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
                    throw new RuntimeException(
                                                      "Signature does not match serialized package. This is a security violation. Deserialisation aborted." );
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
    
    public static ExtensionRegistry buildRegistry() {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
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
