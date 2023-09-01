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
