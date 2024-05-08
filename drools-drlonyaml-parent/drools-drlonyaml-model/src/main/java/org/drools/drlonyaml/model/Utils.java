/**
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
package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.PatternDescr;

public class Utils {
    private static final ObjectMapper mapper;

    static {
        YAMLFactory yamlFactory = YAMLFactory.builder().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).build();
        mapper = new ObjectMapper(yamlFactory);
    }

    private Utils() {
        // only static methods.
    }
    
    public static Base from(BaseDescr o) {
        if (o instanceof PatternDescr patternDescr) {
            return Pattern.from(patternDescr);
        }
        if (o instanceof NotDescr notDescr) {
            return Not.from(notDescr);
        }
        if (o instanceof AndDescr andDescr) {
            return All.from(andDescr);
        }
        if (o instanceof ExistsDescr existsDescr) {
            return Exists.from(existsDescr);
        }
        throw new UnsupportedOperationException("TODO: "+o);
    }

    public static List<Base> from(List<BaseDescr> descrs) {
        List<Base> results = new ArrayList<>();
        for (Object item : descrs) {
            results.add(from((BaseDescr) item));
        }
        return results;
    }

    public static ObjectMapper getYamlMapper() {
        return mapper;
    }
}
