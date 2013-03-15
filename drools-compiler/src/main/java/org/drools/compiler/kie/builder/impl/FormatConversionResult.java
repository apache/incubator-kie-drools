package org.drools.compiler.kie.builder.impl;

public class FormatConversionResult {

    private final String convertedName;
    private final byte[] content;

    public FormatConversionResult(String convertedName, byte[] content) {
        this.convertedName = convertedName;
        this.content = content;
    }

    public String getConvertedName() {
        return convertedName;
    }

    public byte[] getContent() {
        return content;
    }
}
