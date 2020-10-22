package org.kie.dmn.kogito.quarkus.example;

import java.time.Period;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.kogito.Application;
import org.kie.kogito.dmn.rest.DMNEvaluationErrorException;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.dmn.rest.DMNResult;
import org.kie.kogito.dmn.util.StronglyTypedUtils;

@Path("/$nameURL$")
public class DMNRestResourceTemplate {

    Application application;
    
    private static final String KOGITO_DECISION_INFOWARN_HEADER = "X-Kogito-decision-messages";
    
    @javax.ws.rs.core.Context
    private org.jboss.resteasy.spi.HttpResponse httpResponse;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @org.eclipse.microprofile.openapi.annotations.parameters.RequestBody(content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = "application/json",schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/InputSet1")))
    @org.eclipse.microprofile.openapi.annotations.responses.APIResponse(content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = "application/json", schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/OutputSet1")))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",schema = @io.swagger.v3.oas.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/InputSet1")))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",schema = @io.swagger.v3.oas.annotations.media.Schema(ref = "/dmnDefinitions.json#/definitions/OutputSet1")))
    public $outputType$ dmn($inputType$ variables) {
        org.kie.kogito.decision.DecisionModel decision = application.decisionModels().getDecisionModel("$modelNamespace$", "$modelName$");
        OutputSet outputSet = (OutputSet)StronglyTypedUtils.convertToOutputSet(variables, OutputSet.class);
        org.kie.kogito.dmn.rest.DMNResult result = new org.kie.kogito.dmn.rest.DMNResult("$modelNamespace$", "$modelName$", decision.evaluateAll(DMNJSONUtils.ctx(decision, $inputData$)));
        return $extractContextMethod$(result);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String dmn() throws java.io.IOException {
        return new String(org.drools.core.util.IoUtils.
                          readBytesFromInputStream(this.getClass()
                                                   .getResourceAsStream(org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.escapeIdentifier("$modelName$") + 
                                                                        ".dmn_nologic")));
    }

    @javax.ws.rs.ext.Provider
    public static class DMNEvaluationErrorExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<org.kie.kogito.dmn.rest.DMNEvaluationErrorException> {
        public javax.ws.rs.core.Response toResponse(org.kie.kogito.dmn.rest.DMNEvaluationErrorException e) {
            return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR).entity(e.getResult()).build();
        }
    }

    private Object extractContextIfSucceded(DMNResult result){
        if (!result.hasErrors()) {
            try {
                enrichResponseHeaders(result);
                return objectMapper.writeValueAsString(result.getDmnContext());
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new DMNEvaluationErrorException(result);
        }
    }

    private OutputSet extractStronglyTypedContextIfSucceded(DMNResult result) {
        if (!result.hasErrors()) {
            enrichResponseHeaders(result);
            return (OutputSet)StronglyTypedUtils.extractOutputSet(result, OutputSet.class);
        } else {
            throw new DMNEvaluationErrorException(result);
        }
    }

    private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .registerModule(new com.fasterxml.jackson.databind.module.SimpleModule()
                            .addSerializer(org.kie.dmn.feel.lang.types.impl.ComparablePeriod.class,
                                           new org.kie.kogito.dmn.rest.DMNFEELComparablePeriodSerializer()))
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

    private Object extractSingletonDSIfSucceded(DMNResult result) {
        if (!result.hasErrors()) {
            try {
                enrichResponseHeaders(result);
                return objectMapper.writeValueAsString(result.getDecisionResults().get(0).getResult());
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new DMNEvaluationErrorException(result);
        }
    }
    
    private void enrichResponseHeaders(DMNResult result) {
        if (!result.getMessages().isEmpty()) {
            String infoWarns = result.getMessages().stream().map(m -> m.getLevel() + " " + m.getMessage()).collect(java.util.stream.Collectors.joining(", "));
            httpResponse.getOutputHeaders().add(KOGITO_DECISION_INFOWARN_HEADER, infoWarns);
        }
    }
}