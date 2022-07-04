package org.optaplanner.core.config.phase.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

@XmlType(propOrder = {
        "customPhaseCommandClassList",
        "customProperties",
})
public class CustomPhaseConfig extends PhaseConfig<CustomPhaseConfig> {

    public static final String XML_ELEMENT_NAME = "customPhase";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XmlElement(name = "customPhaseCommandClass")
    protected List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> customProperties = null;

    @XmlTransient
    protected List<CustomPhaseCommand> customPhaseCommandList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public List<Class<? extends CustomPhaseCommand>> getCustomPhaseCommandClassList() {
        return customPhaseCommandClassList;
    }

    public void setCustomPhaseCommandClassList(List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList) {
        this.customPhaseCommandClassList = customPhaseCommandClassList;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public List<CustomPhaseCommand> getCustomPhaseCommandList() {
        return customPhaseCommandList;
    }

    public void setCustomPhaseCommandList(List<CustomPhaseCommand> customPhaseCommandList) {
        this.customPhaseCommandList = customPhaseCommandList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public CustomPhaseConfig withCustomPhaseCommandClassList(
            List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList) {
        this.customPhaseCommandClassList = customPhaseCommandClassList;
        return this;
    }

    public CustomPhaseConfig withCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
        return this;
    }

    public CustomPhaseConfig withCustomPhaseCommandList(List<CustomPhaseCommand> customPhaseCommandList) {
        this.customPhaseCommandList = customPhaseCommandList;
        return this;
    }

    public <Solution_> CustomPhaseConfig withCustomPhaseCommands(CustomPhaseCommand<Solution_>... customPhaseCommands) {
        this.customPhaseCommandList = Arrays.asList(customPhaseCommands);
        return this;
    }

    @Override
    public CustomPhaseConfig inherit(CustomPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        customPhaseCommandClassList = ConfigUtils.inheritMergeableListProperty(
                customPhaseCommandClassList, inheritedConfig.getCustomPhaseCommandClassList());
        customPhaseCommandList = ConfigUtils.inheritMergeableListProperty(
                customPhaseCommandList, inheritedConfig.getCustomPhaseCommandList());
        customProperties = ConfigUtils.inheritMergeableMapProperty(
                customProperties, inheritedConfig.getCustomProperties());
        return this;
    }

    @Override
    public CustomPhaseConfig copyConfig() {
        return new CustomPhaseConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (getTerminationConfig() != null) {
            getTerminationConfig().visitReferencedClasses(classVisitor);
        }
        if (customPhaseCommandClassList != null) {
            customPhaseCommandClassList.forEach(classVisitor);
        }
    }

}
