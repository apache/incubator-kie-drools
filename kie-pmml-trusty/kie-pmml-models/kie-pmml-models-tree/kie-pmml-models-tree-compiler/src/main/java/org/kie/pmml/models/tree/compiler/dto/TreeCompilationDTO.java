package org.kie.pmml.models.tree.compiler.dto;

import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

public class TreeCompilationDTO extends AbstractSpecificCompilationDTO<TreeModel> {

    private static final long serialVersionUID = 6829515292921161468L;

    private final Double missingValuePenalty;
    private final Node node;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    private TreeCompilationDTO(final CompilationDTO<TreeModel> source) {
        super(source);
        missingValuePenalty = source.getModel().getMissingValuePenalty() != null ?
                source.getModel().getMissingValuePenalty().doubleValue() : null;
        node = source.getModel().getNode();
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    public static TreeCompilationDTO fromCompilationDTO(final CompilationDTO<TreeModel> source) {
        return new TreeCompilationDTO(source);
    }

    public Double getMissingValuePenalty() {
        return missingValuePenalty;
    }

    public Node getNode() {
        return node;
    }
}
