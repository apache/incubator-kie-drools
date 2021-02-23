/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.http.HttpHeaders;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class StaticContentTest {

    private static String readStream(InputStream in) throws IOException {
        byte[] data = new byte[1024];
        int r;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((r = in.read(data)) > 0) {
            out.write(data, 0, r);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testIndexHtml() throws Exception {
        given().contentType(ContentType.JSON).when().get("/").then()
                .statusCode(200)
                .body(containsString("<title>Kogito - TrustyAI</title>"));
    }

    @Test
    public void testHeaders() {
        given().contentType(ContentType.JSON).when().get("/").then()
                .statusCode(200)
                .header(HttpHeaders.CACHE_CONTROL.toString(), "no-cache")
                .header(HttpHeaders.CONTENT_TYPE.toString(), "text/html;charset=utf8");
    }
    
    @Test
    public void testHandlePath() {
        given().when().get("/audit")
                .then()
                .statusCode(200);

        given().when().get("/audit/decision/9cf2179f-4fed-4793-b674-a19c45e6cbff/outcomes")
                .then()
                .statusCode(200);
    }
}
