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
package org.kie.dmn.kogito.quarkus.example;

import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.kogito.Application;
import org.kie.kogito.dmn.rest.DMNEvaluationErrorException;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.dmn.util.StronglyTypedUtils;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/$nameURL$")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Decision - $modelName$", description = "$modelDescription$")
public class DMNRestResourceTemplate {

    Application application;

    private static final String KOGITO_DECISION_INFOWARN_HEADER = "X-Kogito-decision-messages";
    private static final String KOGITO_EXECUTION_ID_HEADER = "X-Kogito-execution-id";

    private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .registerModule(new com.fasterxml.jackson.databind.module.SimpleModule()
                                    .addSerializer(org.kie.dmn.feel.lang.types.impl.ComparablePeriod.class,
                                                   new org.kie.kogito.dmn.rest.DMNFEELComparablePeriodSerializer()))
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

    @PostMapping(value = "$dmnMethodUrl$", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.Operation(operationId ="evaluateDmn_$modelName$", summary="It evaluates the $modelName$ DMN Model")
    @org.eclipse.microprofile.openapi.annotations.parameters.RequestBody(content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = "application/json",schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/InputSet1")), description = "DMN input")
    @org.eclipse.microprofile.openapi.annotations.responses.APIResponse(content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = "application/json", schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/OutputSet1")), description = "DMN output")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",schema = @io.swagger.v3.oas.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/InputSet1")), description = "DMN input")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",schema = @io.swagger.v3.oas.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/OutputSet1")), description = "DMN output")
    public ResponseEntity<?> dmn(@RequestBody(required = false) $inputType$ variables) {
        org.kie.kogito.decision.DecisionModel decision = application.get(org.kie.kogito.decision.DecisionModels.class).getDecisionModel("$modelNamespace$", "$modelName$");
        OutputSet outputSet = (OutputSet)StronglyTypedUtils.convertToOutputSet(variables, OutputSet.class);
        org.kie.dmn.api.core.DMNResult decisionResult = decision.evaluateAll(DMNJSONUtils.ctx(decision, $inputData$));
        KogitoDMNResult result = new KogitoDMNResult("$modelNamespace$", "$modelName$", decisionResult);
        return enrichResponseHeaders(decisionResult, $extractContextMethod$(result));
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @io.swagger.v3.oas.annotations.Operation(operationId ="getDmn_$modelName$", summary = "Get the DMN $modelName$ model file")
    public String dmn() throws java.io.IOException {
        try (InputStream is = this.getClass().getResourceAsStream(CodegenStringUtil.escapeIdentifier("$modelName$") + ".dmn_nologic")) {
            return new String(org.drools.util.IoUtils.readBytesFromInputStream(Objects.requireNonNull(is)));
        }
    }

    private Entry<HttpStatus, ?> buildFailedEvaluationResponse(KogitoDMNResult result){
        return new SimpleEntry(HttpStatus.INTERNAL_SERVER_ERROR, result);
    }

    private Entry<HttpStatus, ?> extractContextIfSucceded(KogitoDMNResult result){
        if (!result.hasErrors()) {
            return new SimpleEntry(HttpStatus.OK, buildResponse(result.getDmnContext()));
        } else {
            return buildFailedEvaluationResponse(result);
        }
    }

    private Entry<HttpStatus, ?> extractStronglyTypedContextIfSucceded(KogitoDMNResult result) {
        if (!result.hasErrors()) {
            return new SimpleEntry(HttpStatus.OK, buildResponse((OutputSet)StronglyTypedUtils.extractOutputSet(result, OutputSet.class)));
        } else {
            return buildFailedEvaluationResponse(result);
        }
    }

    private Entry<HttpStatus, ?> extractSingletonDSIfSucceded(KogitoDMNResult result) {
        if (!result.hasErrors()) {
            return new SimpleEntry(HttpStatus.OK, buildResponse(result.getDecisionResults().get(0).getResult()));
        } else {
            return buildFailedEvaluationResponse(result);
        }
    }

    private ResponseEntity buildDMNResultResponse(KogitoDMNResult result) {
        if (!result.hasErrors()) {
            return ResponseEntity.ok(buildResponse(result));
        } else {
            Entry<HttpStatus, ?> response = buildFailedEvaluationResponse(result);
            return ResponseEntity.status(response.getKey()).body(response.getValue());
        }
    }

    private String buildResponse(Object o){
        try{
            return objectMapper.writeValueAsString(o);
        }
        catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity enrichResponseHeaders(org.kie.dmn.api.core.DMNResult result, Entry<HttpStatus, ?> response) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(response.getKey());

        if (!result.getMessages().isEmpty()) {
            String infoWarns = result.getMessages().stream().map(m -> m.getLevel() + " " + m.getMessage()).collect(Collectors.joining(", "));
            bodyBuilder.header(KOGITO_DECISION_INFOWARN_HEADER, infoWarns);
        }

        org.kie.kogito.decision.DecisionExecutionIdUtils.getOptional(result.getContext())
            .ifPresent(executionId -> bodyBuilder.header(KOGITO_EXECUTION_ID_HEADER, executionId));

        return bodyBuilder.body(response.getValue());
    }
}