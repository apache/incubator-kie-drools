package org.kie.pmml.pmml_4_2.model;

public class ExternalBeanRef {

    private ExternalBeanDefinition beanDefinition;
    private String modelFieldName;
    private ModelUsage usage;

    public enum ModelUsage {
        MINING,
        OUTPUT;
    }

    public ExternalBeanRef(String modelFieldName, String beanInfo, ModelUsage usage) throws IllegalArgumentException {
        this.modelFieldName = modelFieldName;
        this.usage = usage;
        try {
            this.beanDefinition = new ExternalBeanDefinition(beanInfo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to construct ExternalBeanRef. ", e);
        }
    }

    public String getBeanPackageName() {
        return beanDefinition.getBeanPackageName();
    }

    public void setBeanPackageName(String beanPackageName) {
        this.beanDefinition.setBeanPackageName(beanPackageName);
    }

    public String getBeanName() {
        return beanDefinition.getBeanName();
    }

    public void setBeanName(String beanName) {
        this.beanDefinition.setBeanName(beanName);
    }

    public ExternalBeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public String getQualifiedBeanName() {
        return beanDefinition.getQualifiedBeanName();
    }

    public String getModelFieldName() {
        return modelFieldName;
    }

    public void setModelFieldName(String modelFieldName) {
        this.modelFieldName = modelFieldName;
    }

    public ModelUsage getUsage() {
        return usage;
    }

    public void setUsage(ModelUsage usage) {
        this.usage = usage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beanDefinition == null) ? 0 : beanDefinition.hashCode());
        result = prime * result + ((modelFieldName == null) ? 0 : modelFieldName.hashCode());
        result = prime * result + ((usage == null) ? 0 : usage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExternalBeanRef other = (ExternalBeanRef) obj;
        if (beanDefinition == null) {
            if (other.beanDefinition != null) {
                return false;
            }
        } else if (!beanDefinition.equals(other.beanDefinition)) {
            return false;
        }
        if (modelFieldName == null) {
            if (other.modelFieldName != null) {
                return false;
            }
        } else if (!modelFieldName.equals(other.modelFieldName)) {
            return false;
        }
        if (usage != other.usage) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExternalBeanRef [beanDefinition=" + beanDefinition + ", modelFieldName=" + modelFieldName + ", usage="
                + usage + "]";
    }
}
