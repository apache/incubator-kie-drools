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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/management/process/")
@RolesAllowed("source-files-client")
public final class SourceFilesResource {

    @Inject
    SourceFilesProvider sourceFilesProvider;

    @GET
    @Path("{id}/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SourceFile> getSourceFiles(@PathParam("id") String processId) {
        return sourceFilesProvider.getSourceFiles(processId);
    }

    @GET
    @Path("sources")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Collection<SourceFile>> getSourceFiles() {
        return sourceFilesProvider.getSourceFiles();
    }

    @GET
    @Path("sources/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getSourceFile(@QueryParam("fileName") String fileName) throws IOException {
        if (sourceFilesProvider.contains(fileName)) {
            try (InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
                if (file == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    java.nio.file.Path path = java.nio.file.Paths.get(fileName);
                    byte[] bytes = file.readAllBytes();
                    return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM)
                            .header("Content-Disposition", "inline; filename=\""
                                    + path.subpath(path.getNameCount() - 1, path.getNameCount()) + "\"")
                            .header("Content-Length", bytes.length)
                            .build();
                }
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
