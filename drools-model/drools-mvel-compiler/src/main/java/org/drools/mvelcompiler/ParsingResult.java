package org.drools.mvelcompiler;

import org.drools.mvelcompiler.context.MvelCompilerContext;

public class ParsingResult {

    private String result;

    public ParsingResult(MvelCompilerContext mvelCompilerContext, String result) {
        this.result = result;
    }

    public String resultAsString() {
        return result;
    }

    @Override
    public String toString() {
        return "ParsingResult{" +
                "result='" + result + '\'' +
                '}';
    }
}
