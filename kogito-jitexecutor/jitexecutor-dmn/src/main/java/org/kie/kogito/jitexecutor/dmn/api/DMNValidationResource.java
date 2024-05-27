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

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNMessage;
import org.kie.kogito.jitexecutor.dmn.utils.ResolveByKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.kie.kogito.jitexecutor.common.Constants.LINEBREAK;

@Path("jitdmn/validate")
public class DMNValidationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DMNValidationResource.class);

    // trick for resolver/implementation for NI
    static final DMNValidator validator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(String payload) {
        LOGGER.debug(LINEBREAK);
        LOGGER.debug("jitdmn/validate");
        LOGGER.debug(payload);
        LOGGER.debug(LINEBREAK);
        List<DMNMessage> validate =
                validator.validate(new StringReader(payload), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION, Validation.ANALYZE_DECISION_TABLE);
        List<JITDMNMessage> result = validate.stream().map(JITDMNMessage::of).collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(MultipleResourcesPayload payload) {
        LOGGER.debug(LINEBREAK);
        LOGGER.debug("jitdmn/validate");
        LOGGER.debug(payload.toString());
        LOGGER.debug(LINEBREAK);
        Map<String, Resource> resources = new LinkedHashMap<>();
        for (ResourceWithURI r : payload.getResources()) {
            Resource readerResource = ResourceFactory.newReaderResource(new StringReader(r.getContent()), "UTF-8");
            readerResource.setSourcePath(r.getURI());
            resources.put(r.getURI(), readerResource);
        }
        ResolveByKey rbk = new ResolveByKey(resources);
        List<DMNMessage> validate = validator
                .validateUsing(Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION, Validation.ANALYZE_DECISION_TABLE)
                .usingImports((x, y, locationURI) -> rbk.readerByKey(locationURI))
                .theseModels(resources.values().toArray(new Resource[] {}));
        List<JITDMNMessage> result = validate.stream().map(JITDMNMessage::of).collect(Collectors.toList());
        return Response.ok(result).build();
    }
}
