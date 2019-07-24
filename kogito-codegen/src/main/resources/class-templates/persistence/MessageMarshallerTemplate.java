import java.io.IOException;


import org.infinispan.protostream.MessageMarshaller;

public class CustomMessageMarshaller implements MessageMarshaller {

	public Class<?> getJavaClass() {

	}

	public String getTypeName() {

	}

	public Type readFrom(ProtoStreamReader reader) throws IOException {

	}

	public void writeTo(ProtoStreamWriter writer, Type t) throws IOException {

	}

}