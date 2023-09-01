package org.drools.drl.extensions;

import org.kie.api.io.ResourceType;

/**
 * Result of conversion from custom resource (like guided dtable or guided template) into DRL or DSLR.
 */
public class ResourceConversionResult {
    private final String content;
    private final ResourceType type;

    public ResourceConversionResult(String content, ResourceType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ResourceConversionResult{" +
                "content='" + content + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceConversionResult that = (ResourceConversionResult) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}
