/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.spring.auth;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import org.kie.kogito.auth.IdentityProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@Component
public class SpringIdentityProvider implements IdentityProvider {

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public String getName() {
        return getAuthentication().map(Principal::getName).orElse(null);
    }

    @Override
    public Collection<String> getRoles() {
        return getAuthentication().map(a -> a.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toSet())).orElse(emptySet());
    }

    @Override
    public boolean hasRole(String role) {
        return getAuthentication().map(a -> a.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(role))).orElse(false);
    }
}
