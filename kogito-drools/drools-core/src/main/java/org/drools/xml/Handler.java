/*
 * Copyright 2005 JBoss Inc
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

package org.drools.xml;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface Handler {

    Object start(String uri,
                 String localName,
                 Attributes attrs,
                 ExtensibleXmlParser xmlPackageReader) throws SAXException;

    Object end(String uri,
               String localName,
               ExtensibleXmlParser xmlPackageReader) throws SAXException;

    Set<Class<?>> getValidParents();

    Set<Class<?>> getValidPeers();

    boolean allowNesting();

    Class<?> generateNodeFor();
}
