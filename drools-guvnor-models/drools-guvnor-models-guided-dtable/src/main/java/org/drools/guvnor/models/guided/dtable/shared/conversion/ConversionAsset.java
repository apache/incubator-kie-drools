package org.drools.guvnor.models.guided.dtable.shared.conversion;

/**
 * A container for a new Asset created during the conversion process
 */
public class ConversionAsset {

    private static final long serialVersionUID = 540L;

    private String uuid;
    private String format;

    public ConversionAsset() {
    }

    public ConversionAsset( final String uuid,
                            final String format ) {
        this.uuid = uuid;
        this.format = format;
    }

    public String getUUID() {
        return uuid;
    }

    public String getFormat() {
        return format;
    }

}