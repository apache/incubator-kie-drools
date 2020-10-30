package org.drools.core.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ByteArrayResourceSerializationTest {

	//FIX https://issues.redhat.com/browse/DROOLS-5681
	@Test
	public void bytesAttributesIsStillSerializedDeserializedCorrectly() throws IOException, ClassNotFoundException {

		final byte[] content = "some content".getBytes(StandardCharsets.UTF_8);

		ByteArrayResource bar = new ByteArrayResource(content, StandardCharsets.UTF_8.toString());
		byte[] serializedBar;
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)){
			oos.writeObject(bar);
			serializedBar = baos.toByteArray();
		}
		ByteArrayResource desBar;
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedBar))){
			desBar = (ByteArrayResource) ois.readObject();
		}
		Assertions.assertThat(desBar.getBytes()).isEqualTo(content);
	}

}
