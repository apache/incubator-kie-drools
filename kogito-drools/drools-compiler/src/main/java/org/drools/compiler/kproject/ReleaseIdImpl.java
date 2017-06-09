/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Properties;

import org.kie.api.builder.ReleaseId;

import static java.util.stream.Collectors.toList;

public class ReleaseIdImpl extends org.appformer.maven.support.ReleaseIdImpl implements ReleaseId {

    public ReleaseIdImpl() {
    }

    public ReleaseIdImpl(String releaseId) {
        super(releaseId);
    }

    public ReleaseIdImpl(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
    }

    public ReleaseIdImpl(String groupId, String artifactId, String version, String type) {
        super(groupId, artifactId, version, type);
    }

    public static ReleaseIdImpl adapt(org.appformer.maven.support.ReleaseId r ) {
        return new ReleaseIdImpl(r.getGroupId(), r.getArtifactId(), r.getVersion(), ( (org.appformer.maven.support.ReleaseIdImpl) r ).getType() );
    }

    public static Collection<ReleaseId> adaptAll( Collection<org.appformer.maven.support.ReleaseId> rs ) {
        return rs.stream().map(ReleaseIdImpl::adapt).collect(toList());
    }

    public static ReleaseId fromPropertiesString( String path ) {
        Properties props = new Properties();
        try {
            props.load(new StringReader( path) );
            return getReleaseIdFromProperties(props, path);
        } catch (IOException e) {
            throw new RuntimeException("pom.properties was malformed\n" + path, e);
        }
    }

    public static ReleaseId fromPropertiesStream( InputStream stream, String path ) {
        Properties props = new Properties();
        try {
            props.load(stream);
            return getReleaseIdFromProperties(props, path);
        } catch (IOException e) {
            throw new RuntimeException("pom.properties was malformed\n" + path, e);
        }
    }

    private static ReleaseId getReleaseIdFromProperties( Properties props, String path ) {
        String groupId = props.getProperty("groupId");
        String artifactId = props.getProperty("artifactId");
        String version = props.getProperty("version");
        if (isEmpty(groupId) || isEmpty(artifactId) || isEmpty(version)) {
            throw new RuntimeException("pom.properties exists but ReleaseId content is malformed\n" + path);
        }
        return new ReleaseIdImpl( groupId, artifactId, version);
    }
}
