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

package org.kie.internal.security;

import java.io.File;
import java.net.URI;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.URIParameter;
import java.security.cert.Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePolicyHelper {

    private static final Logger               log                          = LoggerFactory.getLogger(KiePolicyHelper.class);

    public static final String                KIE_SECURITY_POLICY_PROPERTY = "kie.security.policy";
    private static final boolean              policyEnabled;
    private static final AccessControlContext context;

    static {
        AccessControlContext ctx = null;
        try {
            String policyFile = System.getProperty(KIE_SECURITY_POLICY_PROPERTY);
            if (policyFile != null) {
                log.info("Kie policy file property defined: " + policyFile);
            }
            SecurityManager securityManager = System.getSecurityManager();
            if (policyFile != null && securityManager == null) {
                log.warn("Security manager not started. The KIE policy file configuration will be ignored. In order to use the policy file, a security manager needs to be started.");
            }
            if (policyFile != null && securityManager != null) {
                URI resource = new File(policyFile).toURI();
                Policy instance = Policy.getInstance("JavaPolicy", new URIParameter(resource));
                PermissionCollection permissions = instance.getPermissions(new CodeSource(null, (Certificate[]) null));
                ProtectionDomain[] pds = {new ProtectionDomain(new CodeSource(null, (Certificate[]) null), permissions)};
                ctx = new AccessControlContext(pds);
                log.info("Kie policy successfuly loaded and installed.");
            } else {
                ctx = null;
            }
        } catch (Exception e) {
            ctx = null;
            log.error("Error loading and installing KIE security policy.", e);
            e.printStackTrace();
        }
        context = ctx;
        policyEnabled = ctx != null;
    }

    private KiePolicyHelper() {
    }

    public static boolean isPolicyEnabled() {
        return policyEnabled;
    }

    public static AccessControlContext getAccessContext() {
        return context;
    }
}
