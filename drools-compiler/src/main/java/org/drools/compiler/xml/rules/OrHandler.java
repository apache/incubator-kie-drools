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

package org.drools.compiler.xml.rules;

import java.util.HashSet;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.MultiPatternDestinationDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OrHandler extends BaseAbstractHandler
    implements
    Handler {
    public OrHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        final OrDescr orDescr = new OrDescr();

        return orDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final OrDescr orDescr = (OrDescr) parser.getCurrent();

        final Object parent = parser.getParent();

        if ( !orDescr.getDescrs().isEmpty() ) {
            if ( parent instanceof RuleDescr || parent instanceof QueryDescr ) {
                final RuleDescr ruleDescr = (RuleDescr) parent;
                ruleDescr.getLhs().addDescr( orDescr );
            } else if ( parent instanceof MultiPatternDestinationDescr ) {
                final MultiPatternDestinationDescr mpDescr = (MultiPatternDestinationDescr) parent;
                mpDescr.setInput( orDescr );
            } else if ( parent instanceof ConditionalElementDescr ) {
                final ConditionalElementDescr ceDescr = (ConditionalElementDescr) parent;
                ceDescr.addDescr( orDescr );
            }
        }

        return orDescr;
    }

    public Class generateNodeFor() {
        return OrDescr.class;
    }
}
