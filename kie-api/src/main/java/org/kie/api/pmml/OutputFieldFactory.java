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
package org.kie.api.pmml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OutputFieldFactory {
    
    public static List<PMML4Output<?>> createOutputsFromResults(PMML4Result result) {
        List<PMML4Output<?>> results = new ArrayList<>();
        Set<String> varNames = result.getResultVariables().keySet();
        String correlationId = result.getCorrelationId();
        String segmentationId = result.getSegmentationId();
        String segmentId = result.getSegmentId();
        
        varNames.forEach(vn -> {
            Object obj = result.getResultValue(vn, "value");
            if (obj != null) {
                Double weight = result.getResultValue(vn, "weight", Double.class).orElse(null);
                String displayValue = result.getResultValue(vn, "displayValue", String.class).orElse(null);
                if (obj instanceof Integer) {
                    Integer value = (Integer)obj;
                    IntegerFieldOutput ifo = new IntegerFieldOutput(correlationId, 
                            segmentationId, segmentId, vn, displayValue, weight, value);
                    results.add(ifo);
                } else if (obj instanceof Double) {
                    Double value = (Double)obj;
                    DoubleFieldOutput dfo = new DoubleFieldOutput(correlationId, 
                            segmentationId, segmentId, vn, displayValue, weight, value);
                    results.add(dfo);
                } else if (obj instanceof String) {
                    String value = (String)obj;
                    StringFieldOutput sfo = new StringFieldOutput(correlationId, 
                            segmentationId, segmentId, vn, displayValue, weight, value);
                    results.add(sfo);
                } else {
                    obj = result.getResultValue(vn, null);
                    if (obj != null) {
                        String value = obj.toString();
                        StringFieldOutput sfo = new StringFieldOutput(correlationId, 
                                segmentationId, segmentId, vn, displayValue, weight, value);
                        results.add(sfo);
                    }
                }
            }
        });
        return results;
    }
}
