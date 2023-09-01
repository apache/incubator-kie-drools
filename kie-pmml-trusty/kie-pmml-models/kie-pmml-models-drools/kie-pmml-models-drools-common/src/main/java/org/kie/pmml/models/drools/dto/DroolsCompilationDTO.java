package org.kie.pmml.models.drools.dto;

import java.util.Map;

import org.dmg.pmml.Model;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

public class DroolsCompilationDTO<T extends Model> extends AbstractSpecificCompilationDTO<T> {

    private static final long serialVersionUID = 3279343826083191443L;
    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     * @param source
     * @param fieldTypeMap
     */
    private DroolsCompilationDTO(final CompilationDTO<T> source,
                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        super(source);
        this.fieldTypeMap = fieldTypeMap;
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     * @param source
     * @param fieldTypeMap
     */
    public static <T extends Model> DroolsCompilationDTO<T> fromCompilationDTO(final CompilationDTO<T> source,
                                                                               final Map<String,
                                                                                       KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new DroolsCompilationDTO(source, fieldTypeMap);
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }
}
