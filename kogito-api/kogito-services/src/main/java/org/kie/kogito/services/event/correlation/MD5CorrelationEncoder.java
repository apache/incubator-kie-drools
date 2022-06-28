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
package org.kie.kogito.services.event.correlation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationEncoder;

import static java.util.stream.Collectors.joining;

public class MD5CorrelationEncoder implements CorrelationEncoder {

    @Override
    public String encode(Correlation correlation) {
        try {
            String rawCorrelationString = encodeCorrelation(correlation);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(rawCorrelationString.getBytes());
            byte[] digest = md.digest();
            String myHash = bytesToHex(digest);
            return myHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating MD5 hash for correlation " + correlation);
        }
    }

    private String encodeCorrelation(Correlation correlation) {
        if (correlation instanceof CompositeCorrelation) {
            CompositeCorrelation compositeCorrelation = (CompositeCorrelation) correlation;
            return compositeCorrelation.getValue().stream().map(this::encodeCorrelation).sorted().collect(joining("|"));
        }
        return new StringJoiner("|").add(correlation.getKey()).add(correlation.asString()).toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
