package org.kie.kogito.codegen.process.persistence;

import java.io.IOException;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerException;

public class ProtostreamObjectMarshaller implements ObjectMarshallerStrategy {

    private static final String NAMESPACE = "org.kie.kogito/";
    private SerializationContext context;


    public ProtostreamObjectMarshaller() {
        context = new SerializationContextImpl(Configuration.builder().build());

    }

    @Override
    public Integer order() {
        return 2;
    }

    @Override
    public boolean acceptForMarshalling(Object value) {
        return context.canMarshall(value.getClass());
    }

    @Override
    public Object marshall(Object unmarshalled) {
        try {
            String fullTypeName = context.getMarshaller(unmarshalled.getClass()).getTypeName();
            return Any.newBuilder()
                    .setTypeUrl(NAMESPACE + fullTypeName)
                    .setValue(ByteString.copyFrom(ProtobufUtil.toByteArray(context, unmarshalled))).build();
        } catch (IOException e) {
            throw new ProcessInstanceMarshallerException("cannot marshall protobuf stream", e);
        }
    }

    @Override
    public boolean acceptForUnmarshalling(Object value) {
        Any data = (Any) value;
        return data.getTypeUrl().startsWith(NAMESPACE);
    }

    @Override
    public Object unmarshall(Object marshalled) {
        try {
            Any data = (Any) marshalled;
            String fqn = context.getMarshaller(removeNamespace(data.getTypeUrl())).getJavaClass().getCanonicalName();
            byte[] bytes = data.getValue().toByteArray();
            return ProtobufUtil.fromByteArray(context, bytes, 0, bytes.length, Class.forName(fqn));
        } catch(IOException | ClassNotFoundException e) {
            throw new ProcessInstanceMarshallerException("cannot unmarshall protobuf stream", e);
        }
        
    }

    private String removeNamespace(String dataTypeURL) {
        return dataTypeURL.substring(NAMESPACE.length());
    }

}
