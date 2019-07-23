/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.kubernetes.client.graal;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * Removes EC Keys support from Fabric8 Kubernetes client dependency on native binaries. 
 * This avoids clients to add <code>--allow-incomplete-classpath</code> option on their build configuration.
 * <p/>
 * Cloned from <a href="https://github.com/quarkusio/quarkus/blob/master/extensions/kubernetes-client/runtime/src/main/java/io/quarkus/kubernetes/client/runtime/graal/CertUtilsSubstitutions.java">Quarkus Kubernetes Extension</a> to not add Quarkus dependencies to this project
 */
@TargetClass(className = "io.fabric8.kubernetes.client.internal.CertUtils")
public final class KubernetesClientCertUtilsSubstitutions {

    @Substitute
    static PrivateKey handleECKey(InputStream keyInputStream) throws IOException {
        throw new RuntimeException("EC Keys are not supported when using the native binary");
    }

}
