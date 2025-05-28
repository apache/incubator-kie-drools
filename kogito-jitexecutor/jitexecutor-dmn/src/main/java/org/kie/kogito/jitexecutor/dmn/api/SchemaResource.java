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
import java.util.function.Supplier;

import org.eclipse.microprofile.config.ConfigProvider;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.api.OpenApiConfig;
import io.smallrye.openapi.runtime.io.IOContext;
import io.smallrye.openapi.runtime.io.JsonIO;
import io.smallrye.openapi.runtime.io.media.SchemaIO;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.kie.kogito.jitexecutor.common.Constants.LINEBREAK;

@Path("jitdmn/schema")
public class SchemaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaResource.class);

    // trick for resolver/implementation for NI
    static final OpenAPI x;
    static Schema resourceWithURI;
    static {
        OASFactoryResolver.instance();
        x = OASFactory.createObject(OpenAPI.class);
        resourceWithURI = OASFactory.createObject(Schema.class).type(List.of(SchemaType.OBJECT))
                .addProperty("URI", OASFactory.createObject(Schema.class).type(List.of(SchemaType.STRING)))
                .addProperty("content", OASFactory.createObject(Schema.class).type(List.of(SchemaType.STRING)))
                .required(List.of("URI", "content"));
    }

    private JsonIO<JsonNode, ArrayNode, ObjectNode, ArrayNode, ObjectNode> jsonIO;
    private SchemaIO<JsonNode, ArrayNode, ObjectNode, ArrayNode, ObjectNode> schemaIO;

    public SchemaResource() {
        this.jsonIO = JsonIO.newInstance(OpenApiConfig.fromConfig(ConfigProvider.getConfig()));
        this.schemaIO = new SchemaIO<>(IOContext.forJson(jsonIO));
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(String payload) {
        LOGGER.debug(LINEBREAK);
        LOGGER.debug("jitdmn/validate");
        LOGGER.debug(payload);
        LOGGER.debug(LINEBREAK);
        Supplier<Response> supplier = () -> {
            DMNModel dmnModel = DMNEvaluator.fromXML(payload).getDmnModel();
            DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singletonList(dmnModel)).build();
            return fullSchema(dmnModel, oasResult, true);
        };
        return DMNResourceHelper.manageResponse(supplier);
    }

    private Response fullSchema(DMNModel dmnModel, DMNOASResult oasResult, final boolean singleModel) {
        ObjectNode jsNode = oasResult.getJsonSchemaNode();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        Schema schema = OASFactory.createObject(Schema.class).type(List.of(SchemaType.OBJECT));
        schema.addProperty("context", OASFactory.createObject(Schema.class).type(List.of(SchemaType.OBJECT)).ref(isRef));
        if (singleModel) {
            schema.addProperty("model", OASFactory.createObject(Schema.class).type(List.of(SchemaType.STRING)));
        } else {
            schema.addProperty("mainURI", OASFactory.createObject(Schema.class).type(List.of(SchemaType.STRING)));
            schema.addProperty("resources", OASFactory.createObject(Schema.class).type(List.of(SchemaType.ARRAY)).items(resourceWithURI));
        }
        ObjectNode schemasNode = jsNode.putObject("properties");
        for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
            schemasNode.set(entry.getKey(), schemaIO.write(entry.getValue()).get());
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
        Supplier<Response> supplier = () -> {
            DMNEvaluator dmnEvaluator = DMNEvaluator.fromMultiple(payload);
            DMNModel dmnModel = dmnEvaluator.getDmnModel();
            DMNOASResult oasResult = DMNOASGeneratorFactory.generator(dmnEvaluator.getAllDMNModels()).build();
            return fullSchema(dmnModel, oasResult, false);
        };
        return DMNResourceHelper.manageResponse(supplier);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("form")
    public Response form(String payload) {
        Supplier<Response> supplier = () -> {
            DMNModel dmnModel = DMNEvaluator.fromXML(payload).getDmnModel();
            DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singletonList(dmnModel)).build();
            return formSchema(dmnModel, oasResult);
        };
        return DMNResourceHelper.manageResponse(supplier);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("form")
    public Response form(MultipleResourcesPayload payload) {
        Supplier<Response> supplier = () -> {
            DMNEvaluator dmnEvaluator = DMNEvaluator.fromMultiple(payload);
            DMNModel dmnModel = dmnEvaluator.getDmnModel();
            DMNOASResult oasResult = DMNOASGeneratorFactory.generator(dmnEvaluator.getAllDMNModels()).build();
            return formSchema(dmnModel, oasResult);
        };
        return DMNResourceHelper.manageResponse(supplier);
    }

    private Response formSchema(DMNModel dmnModel, DMNOASResult oasResult) {
        ObjectNode jsNode = oasResult.getJsonSchemaNode();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        jsNode.put("$ref", isRef);

        return Response.ok(jsNode).build();
    }
}
