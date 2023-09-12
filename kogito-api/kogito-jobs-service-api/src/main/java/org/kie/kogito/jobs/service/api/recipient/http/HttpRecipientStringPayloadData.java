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
package org.kie.kogito.jobs.service.api.recipient.http;

import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(allOf = { HttpRecipientPayloadData.class })
public class HttpRecipientStringPayloadData extends HttpRecipientPayloadData<String> {

    @JsonProperty("data")
    private String dataString;

    public HttpRecipientStringPayloadData() {
        // Marshalling constructor.
    }

    private HttpRecipientStringPayloadData(String data) {
        this.dataString = data;
    }

    public String getData() {
        return dataString;
    }

    public static HttpRecipientStringPayloadData from(String data) {
        return new HttpRecipientStringPayloadData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpRecipientStringPayloadData)) {
            return false;
        }
        HttpRecipientStringPayloadData that = (HttpRecipientStringPayloadData) o;
        return Objects.equals(dataString, that.dataString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataString);
    }
}
