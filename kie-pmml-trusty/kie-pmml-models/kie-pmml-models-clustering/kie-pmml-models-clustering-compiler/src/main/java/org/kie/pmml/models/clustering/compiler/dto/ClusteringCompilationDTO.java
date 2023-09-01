package org.kie.pmml.models.clustering.compiler.dto;

import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;

public class ClusteringCompilationDTO extends AbstractSpecificCompilationDTO<ClusteringModel> {

    private static final long serialVersionUID = -5903743905468597652L;

    /**
     * Private constructor
     * @param source
     */
    private ClusteringCompilationDTO(final CompilationDTO<ClusteringModel> source) {
        super(source);
    }

    /**
     * Default builder
     * @param source
     * @return
     */
    public static ClusteringCompilationDTO fromCompilationDTO(final CompilationDTO<ClusteringModel> source) {
        return new ClusteringCompilationDTO(source);
    }
}
