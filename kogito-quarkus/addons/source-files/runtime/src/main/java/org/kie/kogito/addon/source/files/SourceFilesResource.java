/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.source.files;

import java.util.Collection;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/management/processes/")
public final class SourceFilesResource {

    SourceFilesProvider sourceFilesProvider;

    @GET
    @Path("{processId}/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SourceFile> getSourceFilesByProcessId(@PathParam("processId") String processId) {
        return sourceFilesProvider.getProcessSourceFiles(processId);
    }

    @GET
    @Path("{processId}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSourceFileByProcessId(@PathParam("processId") String processId) {
        Optional<String> processSourceFileContent = sourceFilesProvider.getProcessSourceFile(processId);
        if (processSourceFileContent.isPresent()) {
            return Response.ok(processSourceFileContent.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Inject
    void setSourceFilesProvider(SourceFilesProvider sourceFilesProvider) {
        this.sourceFilesProvider = sourceFilesProvider;
    }
}
