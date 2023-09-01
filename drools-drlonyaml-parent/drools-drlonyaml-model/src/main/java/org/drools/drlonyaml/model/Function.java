package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.drl.ast.descr.FunctionDescr;

public class Function {
    private String name;
    private String returnType;
    private List<Parameter> parameters = new ArrayList<>();
    private String body;
    
    public static Function from(FunctionDescr f) {
        Objects.requireNonNull(f);
        Function result = new Function();
        result.name = f.getName();
        result.returnType = f.getReturnType();
        for (int i = 0; i < f.getParameterNames().size(); i++) {
            Parameter p = new Parameter();
            p.name = f.getParameterNames().get(i);
            p.type = f.getParameterTypes().get(i);
            result.parameters.add(p);
        }
        result.body = f.getBody();
        return result;
    }
    
    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }
    
    public static class Parameter {
        private String name;
        private String type;
        
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
    }
}
