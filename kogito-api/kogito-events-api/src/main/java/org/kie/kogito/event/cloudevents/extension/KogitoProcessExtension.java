/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event.cloudevents.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import io.cloudevents.CloudEventExtension;
import io.cloudevents.CloudEventExtensions;
import io.cloudevents.core.provider.ExtensionProvider;

// The size of this extension could be reevaluated since we could make use of `type`, `source` and `subject` for processId, referenceId and instanceState

/**
 * CloudEvent extension for Kogito Process.
 */
public class KogitoProcessExtension implements CloudEventExtension {

    private final Map<String, String> innerValues;
    private String kogitoProcessInstanceId;
    private String kogitoProcessInstanceVersion;
    private String kogitoRootProcessInstanceId;
    private String kogitoProcessId;
    private String kogitoRootProcessId;
    private String kogitoAddons;
    private String kogitoParentProcessinstanceId;
    private String kogitoProcessInstanceState;
    private String kogitoReferenceId;
    private String kogitoStartFromNode;
    private String kogitoBusinessKey;
    private String kogitoProcessType;

    public KogitoProcessExtension() {
        this.innerValues = new HashMap<>();
    }

    public static void register() {
        ExtensionProvider.getInstance().registerExtension(KogitoProcessExtension.class, KogitoProcessExtension::new);
    }

    public String getKogitoProcessInstanceId() {
        return kogitoProcessInstanceId;
    }

    public void setKogitoProcessInstanceId(String kogitoProcessInstanceId) {
        this.kogitoProcessInstanceId = kogitoProcessInstanceId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_ID, this.kogitoProcessInstanceId);
    }

    public String getKogitoRootProcessInstanceId() {
        return kogitoRootProcessInstanceId;
    }

    public void setKogitoProcessInstanceVersion(String kogitoProcessInstanceVersion) {
        this.kogitoProcessInstanceVersion = kogitoProcessInstanceVersion;
        this.addExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION, this.kogitoProcessInstanceVersion);
    }

    public String getKogitoProcessInstanceVersion() {
        return kogitoProcessInstanceVersion;
    }

    public void setKogitoRootProcessInstanceId(String kogitoRootProcessInstanceId) {
        this.kogitoRootProcessInstanceId = kogitoRootProcessInstanceId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID, this.kogitoRootProcessInstanceId);
    }

    public String getKogitoProcessId() {
        return kogitoProcessId;
    }

    public void setKogitoProcessId(String kogitoProcessId) {
        this.kogitoProcessId = kogitoProcessId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_ID, this.kogitoProcessId);
    }

    public String getKogitoRootProcessId() {
        return kogitoRootProcessId;
    }

    public void setKogitoRootProcessId(String kogitoRootProcessId) {
        this.kogitoRootProcessId = kogitoRootProcessId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, this.kogitoRootProcessId);
    }

    public String getKogitoAddons() {
        return kogitoAddons;
    }

    public void setKogitoAddons(String kogitoAddons) {
        this.kogitoAddons = kogitoAddons;
        this.addExtension(CloudEventExtensionConstants.ADDONS, this.kogitoAddons);
    }

    public String getKogitoParentProcessinstanceId() {
        return kogitoParentProcessinstanceId;
    }

    public void setKogitoParentProcessinstanceId(String kogitoParentProcessinstanceId) {
        this.kogitoParentProcessinstanceId = kogitoParentProcessinstanceId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID, this.kogitoParentProcessinstanceId);
    }

    public String getKogitoProcessInstanceState() {
        return kogitoProcessInstanceState;
    }

    public void setKogitoProcessInstanceState(String kogitoProcessInstanceState) {
        this.kogitoProcessInstanceState = kogitoProcessInstanceState;
        this.addExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_STATE, this.kogitoProcessInstanceState);
    }

    public String getKogitoReferenceId() {
        return kogitoReferenceId;
    }

    public void setKogitoReferenceId(String kogitoReferenceId) {
        this.kogitoReferenceId = kogitoReferenceId;
        this.addExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, this.kogitoReferenceId);
    }

    public String getKogitoStartFromNode() {
        return kogitoStartFromNode;
    }

    public void setKogitoStartFromNode(String kogitoStartFromNode) {
        this.kogitoStartFromNode = kogitoStartFromNode;
        this.addExtension(CloudEventExtensionConstants.PROCESS_START_FROM_NODE, this.kogitoStartFromNode);
    }

    public void setKogitoBusinessKey(String kogitoBusinessKey) {
        this.kogitoBusinessKey = kogitoBusinessKey;
        this.addExtension(CloudEventExtensionConstants.BUSINESS_KEY, this.kogitoBusinessKey);
    }

    public String getKogitoBusinessKey() {
        return this.kogitoBusinessKey;
    }

    public String getKogitoProcessType() {
        return kogitoProcessType;
    }

    public void setKogitoProcessType(String kogitoProcessType) {
        this.kogitoProcessType = kogitoProcessType;
        this.addExtension(CloudEventExtensionConstants.PROCESS_TYPE, kogitoProcessType);
    }

    /**
     * @return every extension attribute as a map populated accordingly
     */
    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(this.innerValues);
    }

    protected void addExtension(String key, String value) {
        if (value != null) {
            innerValues.put(key, value);
        }
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        this.setKogitoAddons(getExtension(extensions, CloudEventExtensionConstants.ADDONS));
        this.setKogitoParentProcessinstanceId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID));
        this.setKogitoProcessId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_ID));
        this.setKogitoProcessInstanceId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_INSTANCE_ID));
        this.setKogitoProcessInstanceVersion(getExtension(extensions, CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION));
        this.setKogitoProcessInstanceState(getExtension(extensions, CloudEventExtensionConstants.PROCESS_INSTANCE_STATE));
        this.setKogitoReferenceId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_REFERENCE_ID));
        this.setKogitoRootProcessId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID));
        this.setKogitoRootProcessInstanceId(getExtension(extensions, CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID));
        this.setKogitoStartFromNode(getExtension(extensions, CloudEventExtensionConstants.PROCESS_START_FROM_NODE));
        this.setKogitoBusinessKey(getExtension(extensions, CloudEventExtensionConstants.BUSINESS_KEY));
        this.setKogitoProcessType(getExtension(extensions, CloudEventExtensionConstants.PROCESS_TYPE));
    }

    private String getExtension(CloudEventExtensions extensions, String key) {
        return Objects.toString(extensions.getExtension(key), "");
    }

    @Override
    public Object getValue(String key) {
        return this.innerValues.get(key);
    }

    @Override
    public Set<String> getKeys() {
        return innerValues.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KogitoProcessExtension that = (KogitoProcessExtension) o;
        return Objects.equals(kogitoProcessInstanceId, that.kogitoProcessInstanceId) &&
                Objects.equals(kogitoProcessInstanceVersion, that.kogitoProcessInstanceVersion) &&
                Objects.equals(kogitoRootProcessInstanceId, that.kogitoRootProcessInstanceId) &&
                Objects.equals(kogitoProcessId, that.kogitoProcessId) &&
                Objects.equals(kogitoRootProcessId, that.kogitoRootProcessId) &&
                Objects.equals(kogitoAddons, that.kogitoAddons) &&
                Objects.equals(kogitoParentProcessinstanceId, that.kogitoParentProcessinstanceId) &&
                Objects.equals(kogitoProcessInstanceState, that.kogitoProcessInstanceState) &&
                Objects.equals(kogitoReferenceId, that.kogitoReferenceId) &&
                Objects.equals(kogitoStartFromNode, that.kogitoStartFromNode) &&
                Objects.equals(kogitoBusinessKey, that.kogitoBusinessKey) &&
                Objects.equals(kogitoProcessType, that.kogitoProcessType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kogitoProcessInstanceId, kogitoRootProcessInstanceId, kogitoProcessInstanceVersion, kogitoProcessId, kogitoRootProcessId, kogitoAddons, kogitoParentProcessinstanceId,
                kogitoProcessInstanceState,
                kogitoReferenceId, kogitoStartFromNode, kogitoBusinessKey, kogitoProcessType);
    }

    @Override
    public String toString() {
        return "KogitoProcessExtension{" +
                "procInstanceId='" + kogitoProcessInstanceId + '\'' +
                ", processId='" + kogitoProcessId + '\'' +
                ", referenceId='" + kogitoReferenceId + '\'' +
                '}';
    }
}
