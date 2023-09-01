package org.kie.pmml.commons.model;

import java.io.Serializable;
import java.util.List;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/GeneralStructure.html#xsdElement_Extension>Extension</a>
 */
public class KiePMMLExtension implements Serializable {

    private static final long serialVersionUID = -7441789522335799516L;
    private String extender;
    private String name;
    private String value;
    private List<Object> content;

    public KiePMMLExtension(String extender, String name, String value, List<Object> content) {
        this.extender = extender;
        this.name = name;
        this.value = value;
        this.content = content;
    }

    public String getExtender() {
        return extender;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<Object> getContent() {
        return content;
    }
}
