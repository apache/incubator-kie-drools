package org.jbpm.kie.services.impl.form.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.form.model.forms.FormEncodingException;

public class ScriptRepresentation implements Mappable {

    private String documentation;
    private String id;

    private List<ScriptData> helpers;

    private String type;
    private String src;
    private String content;
    private String invokeFunction;

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getContent() {
        if (helpers != null && !helpers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ScriptData helper : helpers) {
                String asScriptContent = helper.get("script");
                asScriptContent = asScriptContent.replaceAll("\"", "\\\"")
                        .replaceAll("\n", "");
                sb.append(asScriptContent);
            }
            return sb.toString();
        }
        return content;
    }

    public void setHelpers(List<ScriptData> helpers) {
        this.helpers = helpers;
    }

    public List<ScriptData> getHelpers() {
        return helpers;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInvokeFunction() {
        return invokeFunction;
    }

    public void setInvokeFunction(String invokeFunction) {
        this.invokeFunction = invokeFunction;
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("@className", getClass().getName());
        data.put("documentation", this.documentation);
        data.put("id", this.id);
        data.put("type", this.type);
        data.put("src", this.src);
        data.put("content", this.content);
        if (getHelpers() != null) {
            List<Object> helpersMap = new ArrayList<Object>();
            for (ScriptData helper : getHelpers()) {
                helpersMap.add(helper.getDataMap());
            }
            data.put("helpers", helpersMap);
        }
        data.put("invokeFunction", this.invokeFunction);
        return data;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) throws FormEncodingException {
        this.documentation = (String) dataMap.get("documentation");
        this.id = (String) dataMap.get("id");
        this.type = (String) dataMap.get("type");
        this.src = (String) dataMap.get("src");
        this.content = (String) dataMap.get("content");
        this.invokeFunction = (String) dataMap.get("invokeFunction");
        @SuppressWarnings("unchecked")
        List<Object> helpersMap = (List<Object>) dataMap.get("helpers");
        if (helpersMap != null) {
            List<ScriptData> myHelpers = new ArrayList<ScriptData>();
            for (Object obj : helpersMap) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> helperMap = (Map<String, Object>) obj;
                    String helperClass = (String) helperMap.get("@className");
//                    FBScriptHelper helper = (FBScriptHelper) ReflectionHelper
//                            .newInstance(helperClass);
                    Class<?> clazz = Class.forName(helperClass); //TODO porting
                    ScriptData helper = (ScriptData) clazz.newInstance();
                    helper.setDataMap(helperMap);
                } catch (Exception e) {
                    throw new FormEncodingException("Problem creating helper " + obj, e);
                }
            }
            setHelpers(myHelpers);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result
                + ((documentation == null) ? 0 : documentation.hashCode());
        result = prime * result + ((helpers == null) ? 0 : helpers.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((invokeFunction == null) ? 0 : invokeFunction.hashCode());
        result = prime * result + ((src == null) ? 0 : src.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScriptRepresentation other = (ScriptRepresentation) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (documentation == null) {
            if (other.documentation != null)
                return false;
        } else if (!documentation.equals(other.documentation))
            return false;
        if (helpers == null) {
            if (other.helpers != null)
                return false;
        } else if (!helpers.equals(other.helpers))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (invokeFunction == null) {
            if (other.invokeFunction != null)
                return false;
        } else if (!invokeFunction.equals(other.invokeFunction))
            return false;
        if (src == null) {
            if (other.src != null)
                return false;
        } else if (!src.equals(other.src))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
