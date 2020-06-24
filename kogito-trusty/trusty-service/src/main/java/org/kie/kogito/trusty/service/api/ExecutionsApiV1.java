/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.api;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.kie.kogito.trusty.service.ITrustyService;
import org.kie.kogito.trusty.service.responses.ExecutionHeaderResponse;
import org.kie.kogito.trusty.service.responses.ExecutionsResponse;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The trusty api resource.
 */
@Path("v1/executions")
public class ExecutionsApiV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionsApiV1.class);

    @Inject
    ITrustyService executionService;

    /**
     * Gets all the headers of the executions that were evaluated within a specified time range.
     *
     * @param from   The start datetime.
     * @param to     The end datetime.
     * @param limit  The maximum (non-negative) number of items to be returned.
     * @param offset The non-negative pagination offset.
     * @param prefix The executionId prefix to be matched in the search.
     * @return The execution headers that satisfy the time range, pagination and prefix conditions.
     */
    @GET
    @APIResponses(value = {
            @APIResponse(description = "Returns the execution headers.",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(type = SchemaType.OBJECT, implementation = ExecutionsResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets the execution headers", description = "Gets the execution headers.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExecutions(
            @Parameter(
                    name = "from",
                    description = "Start datetime for the lookup. Date in the format \"yyyy-MM-dd'T'HH:mm:ssZ\"",
                    required = false,
                    schema = @Schema(implementation = String.class)
            ) @DefaultValue("yesterday") @QueryParam("from") String from,
            @Parameter(
                    name = "to",
                    description = "End datetime for the lookup. Date in the format \"yyyy-MM-dd'T'HH:mm:ssZ\"",
                    required = false,
                    schema = @Schema(implementation = String.class)
            ) @DefaultValue("now") @QueryParam("to") String to,
            @Parameter(
                    name = "limit",
                    description = "Maximum number of results to return.",
                    required = false,
                    schema = @Schema(implementation = Integer.class)
            ) @DefaultValue("100") @QueryParam("limit") int limit,
            @Parameter(
                    name = "offset",
                    description = "Offset for the pagination.",
                    required = false,
                    schema = @Schema(implementation = Integer.class)
            ) @DefaultValue("0") @QueryParam("offset") int offset,
            @Parameter(
                    name = "search",
                    description = "Execution ID prefix to be matched",
                    required = false,
                    schema = @Schema(implementation = String.class)
            ) @DefaultValue("") @QueryParam("search") String prefix) {

        if (limit < 0 || offset < 0) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Pagination parameters can not have negative values.").build();
        }

        OffsetDateTime fromDate;
        OffsetDateTime toDate;
        try {
            fromDate = parseParameterDate(from);
            toDate = parseParameterDate(to);
        } catch (DateTimeParseException e) {
            LOGGER.warn("Invalid date", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Date format should be yyyy-MM-dd'T'HH:mm:ssZ").build();
        }

        List<Execution> executions = executionService.getExecutionHeaders(fromDate, toDate, limit, offset, prefix);

        List<ExecutionHeaderResponse> headersResponses = new ArrayList<>();
        executions.forEach(x -> headersResponses.add(ExecutionHeaderResponse.fromExecution(x)));
        return Response.ok(new ExecutionsResponse(headersResponses.size(), limit, offset, headersResponses)).build();
    }

    private OffsetDateTime parseParameterDate(String date) {
        if (date.equals("yesterday")) {
            return OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
        }
        if (date.equals("now")) {
            return OffsetDateTime.now(ZoneOffset.UTC);
        }

        return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toOffsetDateTime();
    }
}