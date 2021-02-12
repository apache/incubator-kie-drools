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
package org.kie.kogito.jobs.service.model.job;

import java.util.Objects;
import java.util.StringJoiner;

public interface Recipient {

    class HTTPRecipient implements Recipient {

        private String endpoint;

        public HTTPRecipient(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getEndpoint() {
            return endpoint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof HTTPRecipient)) {
                return false;
            }
            HTTPRecipient that = (HTTPRecipient) o;
            return Objects.equals(getEndpoint(), that.getEndpoint());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getEndpoint());
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", HTTPRecipient.class.getSimpleName() + "[", "]")
                    .add("endpoint='" + endpoint + "'")
                    .toString();
        }
    }
}
