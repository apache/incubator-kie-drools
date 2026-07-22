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
package org.kie.kogito.addon.source.files;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/management/processes/")
public class SourceFilesResource extends BaseSourceFilesResource<Response> {

    public SourceFilesResource() {
        this(null);
        // CDI
    }

    @Inject
    public SourceFilesResource(SourceFilesProvider sourceFilesProvider) {
        super(sourceFilesProvider);
    }

    @GET
    @Path("sources")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getSourceFileByUri(@QueryParam("uri") String uri) throws Exception {
        return super.getSourceFileByUri(uri);
    }

    @GET
    @Path("{processId}/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SourceFile> getSourceFilesByProcessId(@PathParam("processId") String processId) {
        return super.getSourceFilesByProcessId(processId);
    }

    @GET
    @Path("{processId}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSourceFileByProcessId(@PathParam("processId") String processId) throws Exception {
        return super.getSourceFileByProcessId(processId);
    }

    @Override
    protected Response buildPlainResponse(byte[] content) {
        return Response.ok(content).build();
    }

    @Override
    protected Response buildStreamResponse(byte[] content, String fileName) {
        return Response.ok(new ByteArrayInputStream(content), MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

    @Override
    protected Response buildNotFoundResponse() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
