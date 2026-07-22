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

import java.util.Collection;

import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management/processes")
public class SourceFilesRestController extends BaseSourceFilesResource<ResponseEntity> {

    @Autowired
    @Lazy
    public SourceFilesRestController(SourceFilesProvider sourceFilesProvider) {
        super(sourceFilesProvider);
    }

    @Override
    @GetMapping(value = "sources", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity getSourceFileByUri(@RequestParam("uri") String uri) throws Exception {
        return super.getSourceFileByUri(uri);
    }

    @Override
    @GetMapping(value = "{processId}/sources", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<SourceFile> getSourceFilesByProcessId(@PathVariable("processId") String processId) {
        return super.getSourceFilesByProcessId(processId);
    }

    @Override
    @GetMapping(value = "{processId}/source", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getSourceFileByProcessId(@PathVariable("processId") String processId) throws Exception {
        return super.getSourceFileByProcessId(processId);
    }

    @Override
    protected ResponseEntity buildPlainResponse(byte[] content) {
        return ResponseEntity.ok(content);
    }

    @Override
    protected ResponseEntity buildStreamResponse(byte[] content, String fileName) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(content);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(content);
    }

    @Override
    protected ResponseEntity buildNotFoundResponse() {
        return ResponseEntity.notFound().build();
    }
}
