/*
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
package org.kie.kogito.jitexecutor.dmn.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.spi.OASFactoryResolver;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.openapi.DMNOASGeneratorFactory;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.dmn.DMNEvaluator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.runtime.io.schema.SchemaWriter;

@Path("jitdmn/schema")
public class SchemaResource {

    // trick for resolver/implementation for NI
    static final OpenAPI x;
    static Schema resourceWithURI;
    static {
        OASFactoryResolver.instance();
        x = OASFactory.createObject(OpenAPI.class);
        resourceWithURI = OASFactory.createObject(Schema.class).type(SchemaType.OBJECT)
                .addProperty("URI", OASFactory.createObject(Schema.class).type(SchemaType.STRING))
                .addProperty("content", OASFactory.createObject(Schema.class).type(SchemaType.STRING))
                .required(List.of("URI", "content"));
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(String payload) {
        DMNModel dmnModel = DMNEvaluator.fromXML(payload).getDmnModel();
        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singletonList(dmnModel)).build();
        return fullSchema(dmnModel, oasResult, true);
    }

    private Response fullSchema(DMNModel dmnModel, DMNOASResult oasResult, final boolean singleModel) {
        ObjectNode jsNode = oasResult.getJsonSchemaNode();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        Schema schema = OASFactory.createObject(Schema.class).type(SchemaType.OBJECT);
        schema.addProperty("context", OASFactory.createObject(Schema.class).type(SchemaType.OBJECT).ref(isRef));
        if (singleModel) {
            schema.addProperty("model", OASFactory.createObject(Schema.class).type(SchemaType.STRING));
        } else {
            schema.addProperty("mainURI", OASFactory.createObject(Schema.class).type(SchemaType.STRING));
            schema.addProperty("resources", OASFactory.createObject(Schema.class).type(SchemaType.ARRAY).items(resourceWithURI));
        }
        ObjectNode schemasNode = jsNode.putObject("properties");
        for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
            SchemaWriter.writeSchema(schemasNode, entry.getValue(), entry.getKey());
        }
        jsNode.put("type", "object");
        ArrayNode requiredArray = jsNode.putArray("required").add("context");
        if (singleModel) {
            requiredArray.add("model");
        } else {
            requiredArray.add("mainURI").add("resources");
        }
        return Response.ok(jsNode).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(MultipleResourcesPayload payload) {
        DMNEvaluator dmnEvaluator = DMNEvaluator.fromMultiple(payload);
        DMNModel dmnModel = dmnEvaluator.getDmnModel();
        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(dmnEvaluator.getAllDMNModels()).build();
        return fullSchema(dmnModel, oasResult, false);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("form")
    public Response form(String payload) {
        DMNModel dmnModel = DMNEvaluator.fromXML(payload).getDmnModel();
        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singletonList(dmnModel)).build();
        return formSchema(dmnModel, oasResult);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("form")
    public Response form(MultipleResourcesPayload payload) {
        DMNEvaluator dmnEvaluator = DMNEvaluator.fromMultiple(payload);
        DMNModel dmnModel = dmnEvaluator.getDmnModel();
        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(dmnEvaluator.getAllDMNModels()).build();
        return formSchema(dmnModel, oasResult);
    }

    private Response formSchema(DMNModel dmnModel, DMNOASResult oasResult) {
        ObjectNode jsNode = oasResult.getJsonSchemaNode();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        jsNode.put("$ref", isRef);

        return Response.ok(jsNode).build();
    }
}
