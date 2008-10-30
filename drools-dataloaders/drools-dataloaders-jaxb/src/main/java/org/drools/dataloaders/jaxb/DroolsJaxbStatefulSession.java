package org.drools.dataloaders.jaxb;

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

import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drools.StatefulSession;
import org.drools.runtime.rule.FactHandle;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.ExpressionCompiler;

public class DroolsJaxbStatefulSession {
    private Unmarshaller            unmarshaller;
    private StatefulSession         session;
    private DroolsJaxbConfiguration configuration;
    private Serializable            getterExpr;

    public DroolsJaxbStatefulSession(StatefulSession session,
                                     Unmarshaller unmarshaller) {
        this( session,
              unmarshaller,
              new DroolsJaxbConfiguration() );
    }

    public DroolsJaxbStatefulSession(StatefulSession session,
                                     Unmarshaller unmarshaller,
                                     DroolsJaxbConfiguration configuration) {
        this.session = session;
        this.unmarshaller = unmarshaller;
        this.configuration = configuration;
        
        if ( this.configuration.getIterableGetter() != null ) {
            final ParserContext parserContext = new ParserContext();
            parserContext.setStrictTypeEnforcement( false );

            ExpressionCompiler compiler = new ExpressionCompiler( this.configuration.getIterableGetter() );
            this.getterExpr = compiler.compile( parserContext );
        }
    }

    public Map insertUnmarshalled(Reader reader) throws JAXBException {

        Object object = this.unmarshaller.unmarshal( reader );
        if ( object instanceof JAXBElement ) {
            object = ((JAXBElement)object).getValue().getClass().getName();
        }
        Map handles = new HashMap<FactHandle, Object>();
        if ( object == null ) {
            return handles;
        }

        if ( this.getterExpr != null ) {
            Iterable it = (Iterable) MVEL.executeExpression( this.getterExpr,
                                                             object );
            if ( it != null ) {
                for ( Object item : it ) {
                    FactHandle handle = this.session.insert( item );
                    handles.put( handle,
                                 object );
                }
            }
        } else {
            FactHandle handle = this.session.insert( object );
            handles.put( handle,
                         object );

        }

        return handles;
    }

}
