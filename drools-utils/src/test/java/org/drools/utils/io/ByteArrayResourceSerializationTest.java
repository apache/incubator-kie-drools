/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.utils.io;

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
