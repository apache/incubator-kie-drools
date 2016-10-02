/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntime;
import org.kie.dmn.core.api.*;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.internal.io.ResourceTypePackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DMNRuntimeImpl
        implements DMNRuntime {

    private KieRuntime runtime;

    public DMNRuntimeImpl(KieRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) runtime.getKieBase().getKiePackage( namespace );
        Map<ResourceType, ResourceTypePackage> map = kpkg.getResourceTypePackages();
        DMNPackage dmnpkg = (DMNPackage) map.get( ResourceType.DMN );
        return dmnpkg != null ? dmnpkg.getModel( modelName ) : null;
    }

    @Override
    public DMNResult evaluateAll(DMNModel model, DMNContext context) {
        List<DRGElement> drgElements = ((DMNModelImpl)model).getDefinitions().getDrgElement();
        DMNResultImpl result = new DMNResultImpl();
        result.setContext( context.clone() );
        for( DRGElement e : drgElements ) {
            if( e instanceof Decision ) {
                Decision decision = (Decision) e;
                List<InformationRequirement> missingInput = new ArrayList<>(  );
                for( InformationRequirement ir : decision.getInformationRequirement() ) {
                    if( ir.getRequiredInput() != null ) {
                        InputData input = findElementById( model, ir.getRequiredInput().getHref() );
                        String name = input.getName();
                        if( ! context.isDefined( name ) ) {
                            missingInput.add( ir );
                        }
                    }
                }
                if( ! missingInput.isEmpty() ) {
                    System.out.println("Missing inputs: "+missingInput );
                    return null;
                }
                Object val = FEEL.newInstance().evaluate( ((LiteralExpression) decision.getExpression()).getText(), result.getContext().getAll() );
                result.getContext().set( decision.getVariable().getName(), val );
            }
        }
        return result;
    }

    private InputData findElementById(DMNModel model, String href) {
        String id = href.contains( "#" ) ? href.substring( href.indexOf( '#' ) + 1 ) : href;
        for( DRGElement e : ((DMNModelImpl)model).getDefinitions().getDrgElement() ) {
            if( e instanceof InputData && id.equals( e.getId() ) ) {
                return (InputData) e;
            }
        }
        return null;
    }

    @Override
    public DMNResult evaluateDecision(DMNModel model, String decisionName, DMNContext context) {
        return null;
    }

}
