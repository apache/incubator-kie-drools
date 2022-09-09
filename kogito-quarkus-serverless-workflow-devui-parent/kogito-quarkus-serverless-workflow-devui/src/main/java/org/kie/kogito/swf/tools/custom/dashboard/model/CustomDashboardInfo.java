/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.swf.tools.custom.dashboard.model;

import java.time.LocalDateTime;

public class CustomDashboardInfo {
    String name;
    String path;
    LocalDateTime lastUpdated;

    public CustomDashboardInfo(String name, String path, LocalDateTime lastUpdated) {
        this.name = name;
        this.path = path;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomDashboardInfo that = (CustomDashboardInfo) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (path != null ? !path.equals(that.path) : that.path != null) {
            return false;
        }
        return lastUpdated != null ? lastUpdated.equals(that.lastUpdated) : that.lastUpdated == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CustomDashboardInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

}
