/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.util.maven.support;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.util.Properties;

import org.kie.api.builder.ReleaseId;

import static java.lang.Character.isWhitespace;

public class ReleaseIdImpl implements ReleaseId, Externalizable {

    private static final String PROJECT_VERSION_MAVEN_PROPERTY = "${project.version}";
    private static final String PARENT_VERSION_MAVEN_PROPERTY = "${project.parent.version}";

    private String groupId;
    private String artifactId;
    private String version;
    private String type;

    private String snapshotVersion;

    public ReleaseIdImpl() {
    }

    public ReleaseIdImpl(String releaseId) {
        String[] split = releaseId.split(":");
        this.groupId = split[0];
        this.artifactId = split[1];
        this.version = split[2];
    }

    public ReleaseIdImpl(String groupId,
                           String artifactId,
                           String version) {
        this(groupId, artifactId, version, "jar");
    }

    public ReleaseIdImpl(String groupId,
                           String artifactId,
                           String version,
                           String type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type == null || type.equals("jar") ?
                groupId + ":" + artifactId + ":" + version :
                groupId + ":" + artifactId + ":" + type + ":" + version;
    }

    public String toExternalForm() {
        return toString();
    }

    public String getPomXmlPath() {
        return getPomXmlPath(this);
    }

    public String getPomPropertiesPath() {
        return getPomPropertiesPath(this);
    }

    public static String getPomXmlPath(ReleaseId releaseId) {
        return "META-INF/maven/" + releaseId.getGroupId() + "/" + releaseId.getArtifactId() + "/pom.xml";
    }

    public static String getPomPropertiesPath(ReleaseId releaseId) {
        return "META-INF/maven/" + releaseId.getGroupId() + "/" + releaseId.getArtifactId() + "/pom.properties";
    }

    public static ReleaseIdImpl fromPropertiesString( String path ) {
        Properties props = new Properties();
        try {
            props.load(new StringReader( path) );
            return getReleaseIdFromProperties(props, path);
        } catch (IOException e) {
            throw new RuntimeException("pom.properties was malformed\n" + path, e);
        }
    }

    public static ReleaseId fromPropertiesStream(InputStream stream, String path ) {
        Properties props = new Properties();
        try {
            props.load(stream);
            return getReleaseIdFromProperties(props, path);
        } catch (IOException e) {
            throw new RuntimeException("pom.properties was malformed\n" + path, e);
        }
    }

    private static ReleaseIdImpl getReleaseIdFromProperties( Properties props, String path ) {
        String groupId = props.getProperty("groupId");
        String artifactId = props.getProperty("artifactId");
        String version = props.getProperty("version");
        if (isEmpty(groupId) || isEmpty(artifactId) || isEmpty(version)) {
            throw new RuntimeException("pom.properties exists but ReleaseId content is malformed\n" + path);
        }
        return new ReleaseIdImpl( groupId, artifactId, version);
    }

    public String getCompilationCachePathPrefix() {
        //return "META-INF/maven/" + groupId + "/" + artifactId + "/";
        return "META-INF/";
    }

    public boolean isSnapshot() {
        return version.endsWith("-SNAPSHOT");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReleaseIdImpl)) {
            return false;
        }

        ReleaseIdImpl that = (ReleaseIdImpl) o;
        return equalsIgnoreNull(artifactId,
                that.artifactId) && equalsIgnoreNull(groupId,
                that.groupId) &&
                equalsIgnoreNull(version,
                        that.version) && equalsIgnoreNull(type,
                that.type);
    }

    private boolean equalsIgnoreNull(Object o1, Object o2) {
        return o1 != null ? o1.equals(o2) : o2 == null;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 29 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 37 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String getSnapshotVersion() {
        return snapshotVersion;
    }

    public void setSnapshotVersion(String snapshotVersion) {
        this.snapshotVersion = snapshotVersion;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(groupId);
        out.writeObject(artifactId);
        out.writeObject(version);
        out.writeObject(type);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        groupId = (String) in.readObject();
        artifactId = (String) in.readObject();
        version = (String) in.readObject();
        type = (String) in.readObject();
    }

    protected static boolean isEmpty(final CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        for (int i = 0, length = str.length(); i < length; i++) {
            if (!isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static String resolveVersion(String versionString, PomModel projectPomModel) {
        if (projectPomModel != null) {
            if (PROJECT_VERSION_MAVEN_PROPERTY.equals(versionString)) {
                return projectPomModel.getReleaseId().getVersion();
            } else if (PARENT_VERSION_MAVEN_PROPERTY.equals(versionString)
                    && (projectPomModel.getParentReleaseId() != null)) {
                return projectPomModel.getParentReleaseId().getVersion();
            } else {
                return versionString;
            }
        } else {
            return versionString;
        }
    }
}
