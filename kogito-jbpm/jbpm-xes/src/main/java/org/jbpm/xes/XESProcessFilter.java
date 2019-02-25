/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes;

import java.util.Date;
import java.util.List;

public class XESProcessFilter {

    private String processId;

    private String processVersion;

    private Date since;

    private Date to;

    private List<Integer> status;

    private Boolean allNodeTypes = Boolean.FALSE;

    private Integer nodeInstanceLogType;

    public String getProcessId() {
        return processId;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public void setAllNodeTypes(Boolean allNodeTypes) {
        this.allNodeTypes = allNodeTypes;
    }

    public Boolean isAllNodeTypes() {
        return allNodeTypes;
    }

    public void setNodeInstanceLogType(Integer nodeInstanceLogType) {
        this.nodeInstanceLogType = nodeInstanceLogType;
    }

    public Integer getNodeInstanceLogType() {
        return nodeInstanceLogType;
    }

    @Override
    public String toString() {
        return "XESProcessFilter{" +
                "processId='" + processId + '\'' +
                ", processVersion='" + processVersion + '\'' +
                ", since=" + since +
                ", to=" + to +
                ", status=" + status +
                ", allNodeTypes=" + allNodeTypes +
                ", nodeInstanceLogType=" + nodeInstanceLogType +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private XESProcessFilter xESProcessFilter;

        private Builder() {
            xESProcessFilter = new XESProcessFilter();
        }

        public Builder withProcessId(String processId) {
            xESProcessFilter.setProcessId(processId);
            return this;
        }

        public Builder withProcessVersion(String processVersion) {
            xESProcessFilter.setProcessVersion(processVersion);
            return this;
        }

        public Builder withSince(Date since) {
            xESProcessFilter.setSince(since);
            return this;
        }

        public Builder withTo(Date to) {
            xESProcessFilter.setTo(to);
            return this;
        }

        public Builder withStatus(List<Integer> status) {
            xESProcessFilter.setStatus(status);
            return this;
        }

        public Builder withAllNodeTypes() {
            xESProcessFilter.setAllNodeTypes(true);
            return this;
        }

        public Builder withNodeInstanceLogType(Integer type) {
            xESProcessFilter.setNodeInstanceLogType(type);
            return this;
        }

        public XESProcessFilter build() {
            return xESProcessFilter;
        }
    }
}
