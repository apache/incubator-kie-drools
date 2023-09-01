package org.kie.api.builder.model;

/**
 * ChannelModel is a model allowing to programmatically define a Channel and wire it to a KieSession
 */
public interface ChannelModel {

    /**
     * @return the name of the channel
     */
    String getName();

    /**
     * Returns the type of this ChannelModel
     * (i.e. the name of the class implementing the Channel)
     */
    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
