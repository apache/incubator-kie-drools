package org.kie.pmml.api.models;

import java.util.List;

/**
 * User-friendly representation of a <b>PMML</b> model
 */
public interface PMMLModel {

    String getFileName();

    String getName();

    List<MiningField> getMiningFields();

    List<OutputField> getOutputFields();
}
