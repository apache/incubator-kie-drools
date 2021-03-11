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
package org.kogito.workitem.openapi;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonNodeResultHandlerTest {

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    void verifyInputModelAsArray() throws JsonProcessingException {
        final JsonNodeResultHandler resultHandler = new JsonNodeResultHandler();
        final String inputModel = "[{\"myKey\": \"myValue\"}]";
        final JsonNode response = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");

        final Object mergedResult = resultHandler.apply(inputModel, response);
        assertNotNull(mergedResult);
        assertTrue(mergedResult instanceof ArrayNode);
        assertEquals(2, ((ArrayNode) mergedResult).size());
    }

    @Test
    void verifyResponseAsArray() throws JsonProcessingException {
        final JsonNodeResultHandler resultHandler = new JsonNodeResultHandler();
        final String inputModel = "{ \"workflowdata\": { \"name\": \"Brazil\" } }";
        final JsonNode response = mapper.readTree(
                "[{\"name\":\"Brazil\",\"topLevelDomain\":[\".br\"],\"alpha2Code\":\"BR\"," +
                        "\"alpha3Code\":\"BRA\",\"callingCodes\":[\"55\"],\"capital\":\"Brasília\"," +
                        "\"altSpellings\":[\"BR\",\"Brasil\",\"Federative Republic of Brazil\",\"República Federativa do Brasil\"]," +
                        "\"region\":\"Americas\",\"subregion\":\"South America\",\"population\":206135893," +
                        "\"latlng\":[-10.0,-55.0],\"demonym\":\"Brazilian\",\"area\":8515767.0,\"gini\":54.7," +
                        "\"timezones\":[\"UTC-05:00\",\"UTC-04:00\",\"UTC-03:00\",\"UTC-02:00\"]," +
                        "\"borders\":[\"ARG\",\"BOL\",\"COL\",\"GUF\",\"GUY\",\"PRY\",\"PER\",\"SUR\",\"URY\",\"VEN\"]," +
                        "\"nativeName\":\"Brasil\",\"numericCode\":\"076\",\"currencies\":[{\"code\":\"BRL\",\"name\":" +
                        "\"Brazilian real\",\"symbol\":\"R$\"}],\"languages\":[{\"iso639_1\":\"pt\",\"iso639_2\":\"por\"," +
                        "\"name\":\"Portuguese\",\"nativeName\":\"Português\"}],\"translations\":{\"de\":\"Brasilien\",\"es\":\"Brasil\"," +
                        "\"fr\":\"Brésil\",\"ja\":\"ブラジル\",\"it\":\"Brasile\",\"br\":\"Brasil\",\"pt\":\"Brasil\",\"nl\":" +
                        "\"Brazilië\",\"hr\":\"Brazil\",\"fa\":\"برزیل\"},\"flag\":\"https://restcountries.eu/data/bra.svg\"," +
                        "\"regionalBlocs\":[{\"acronym\":\"USAN\",\"name\":\"Union of South American Nations\"," +
                        "\"otherAcronyms\":[\"UNASUR\",\"UNASUL\",\"UZAN\"],\"otherNames\":[" +
                        "\"Unión de Naciones Suramericanas\",\"União de Nações Sul-Americanas\"" +
                        ",\"Unie van Zuid-Amerikaanse Naties\",\"South American Union\"]}],\"cioc\":\"BRA\"}]\n");

        final Object mergedResult = resultHandler.apply(inputModel, response);
        assertNotNull(mergedResult);
        assertTrue(mergedResult instanceof ObjectNode);
        assertTrue(((ObjectNode) mergedResult).get("response").isArray());
    }
}
