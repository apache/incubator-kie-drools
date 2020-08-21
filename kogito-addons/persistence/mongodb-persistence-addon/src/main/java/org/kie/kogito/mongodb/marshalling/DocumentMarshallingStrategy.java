/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.mongodb.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.kogito.mongodb.utils.DocumentUtils;

public class DocumentMarshallingStrategy implements ObjectMarshallingStrategy {

    @Override
    public boolean accept(Object object) {
        return object != null;
    }

    @Override
    public Context createContext() {
        return null;
    }

    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] marshal(Context context, ObjectOutputStream os, Object object) throws IOException {

        return DocumentUtils.toByteArray(object);
    }

    @Override
    public Object unmarshal(String dataType,
                            Context context,
                            ObjectInputStream is,
                            byte[] object,
                            ClassLoader classloader) throws IOException, ClassNotFoundException {

        return DocumentUtils.fromByteArray(dataType, object);
    }
}
