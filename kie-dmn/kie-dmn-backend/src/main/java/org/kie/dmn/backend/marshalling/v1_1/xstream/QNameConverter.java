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
package org.kie.dmn.backend.marshalling.v1_1.xstream;

import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Please note this does not extend the DMNBaseConverter as it just need access to the node value itself.
 */
public class QNameConverter implements Converter {

    private static final Logger LOG = LoggerFactory.getLogger(QNameConverter.class);

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( QName.class );
    }

    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
        QName qname = (QName) object;
        if ( !XMLConstants.NULL_NS_URI.equals(qname.getNamespaceURI()) && !XMLConstants.DEFAULT_NS_PREFIX.equals(qname.getPrefix()) ) {
            CustomStaxWriter staxWriter = ((CustomStaxWriter) writer.underlyingWriter());
            try {
                staxWriter.writeNamespace(qname.getPrefix(), qname.getNamespaceURI());
            } catch (XMLStreamException e) {
                // TODO what to do?
                LOG.error("Exception", e);
            }
        }
        writer.setValue(MarshallingUtils.formatQName(qname));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        QName qname = MarshallingUtils.parseQNameString( reader.getValue() );
        Map<String, String> currentNSCtx = ((CustomStaxReader) reader.underlyingReader()).getNsContext();
        String qnameURI = currentNSCtx.get(qname.getPrefix());
        if (qnameURI != null) {
            return new QName(qnameURI, qname.getLocalPart(), qname.getPrefix());
        }
        return qname;
    }

}