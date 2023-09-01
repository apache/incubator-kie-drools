package org.kie.dmn.model.api;

import javax.xml.stream.Location;

public class RowLocation implements Location {

    private int lineNumber;
    private String publicId;
    private String systemId;

    public RowLocation(Location from) {
        this.lineNumber = from.getLineNumber();
        this.publicId = from.getPublicId();
        this.systemId = from.getSystemId();
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return -1;
    }

    @Override
    public int getCharacterOffset() {
        return -1;
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RowLocation [getLineNumber()=").append(getLineNumber()).append(", getColumnNumber()=").append(getColumnNumber()).append(", getCharacterOffset()=").append(getCharacterOffset()).append(
                                                                                                                                                                                                               ", getPublicId()=")
               .append(getPublicId()).append(", getSystemId()=").append(getSystemId()).append("]");
        return builder.toString();
    }
}