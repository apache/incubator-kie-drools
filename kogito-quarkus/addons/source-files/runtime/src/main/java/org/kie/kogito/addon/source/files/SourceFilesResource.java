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
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/management/processes/")
public final class SourceFilesResource {

    SourceFilesProvider sourceFilesProvider;

    @GET
    @Path("sources")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getSourceFileByUri(@QueryParam("uri") String uri) throws Exception {
        Optional<SourceFile> sourceFile = sourceFilesProvider.getSourceFilesByUri(uri);

        if (sourceFile.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try (InputStream file = new ByteArrayInputStream(sourceFile.get().readContents())) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "inline; filename=\"" + java.nio.file.Path.of(sourceFile.get().getUri()).getFileName() + "\"")
                    .build();
        }

    }

    @GET
    @Path("{processId}/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SourceFile> getSourceFilesByProcessId(@PathParam("processId") String processId) {
        return sourceFilesProvider.getProcessSourceFiles(processId);
    }

    @GET
    @Path("{processId}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSourceFileByProcessId(@PathParam("processId") String processId) throws Exception {
        Optional<SourceFile> sourceFile = sourceFilesProvider.getProcessSourceFile(processId);

        if (sourceFile.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sourceFile.get().readContents()).build();

    }

    @Inject
    void setSourceFilesProvider(SourceFilesProvider sourceFilesProvider) {
        this.sourceFilesProvider = sourceFilesProvider;
    }
}
