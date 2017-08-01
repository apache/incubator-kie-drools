/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing LDAP search capabilities.
 */
public class LdapSearcher {

    private static final Logger log = LoggerFactory.getLogger(LdapSearcher.class);

    public static final String SEARCH_SCOPE = "ldap.search.scope";

    private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String DEFAULT_SECURITY_AUTHENTICATION = "simple";

    private final List<SearchResult> searchResults = new ArrayList<>();

    private final Properties config;

    /**
     * @param config LDAP connection properties
     * @see javax.naming.Context
     */
    public LdapSearcher(Properties config) {
        this.config = config;
    }

    /**
     * Search LDAP and stores the results in searchResults field.
     * @param context the name of the context where the search starts (the depth depends on ldap.search.scope)
     * @param filterExpr the filter expression to use for the search. The expression may contain variables of the form
     * "<code>{i}</code>" where <code>i</code> is a non-negative integer. May not be null.
     * @param filterArgs the array of arguments to substitute for the variables in <code>filterExpr</code>. The value of
     * <code>filterArgs[i]</code> will replace each occurrence of "<code>{i}</code>". If null, an equivalent of an empty
     * array is used.
     * @return this
     */
    public LdapSearcher search(String context, String filterExpr, Object... filterArgs) {
        searchResults.clear();

        LdapContext ldapContext = null;
        NamingEnumeration<SearchResult> ldapResult = null;
        try {
            ldapContext = buildLdapContext();
            ldapResult = ldapContext.search(context, filterExpr, filterArgs, createSearchControls());
            while (ldapResult.hasMore()) {
                searchResults.add(ldapResult.next());
            }
        } catch (NamingException ex) {
            throw new RuntimeException("LDAP search has failed", ex);
        } finally {
            if (ldapResult != null) {
                try {
                    ldapResult.close();
                } catch (NamingException ex) {
                    log.error("Failed to close LDAP results enumeration", ex);
                }
            }
            if (ldapContext != null) {
                try {
                    ldapContext.close();
                } catch (NamingException ex) {
                    log.error("Failed to close LDAP context", ex);
                }
            }
        }

        return this;
    }

    public SearchResult getSingleSearchResult() {
        return searchResults.isEmpty() ? null : searchResults.get(0);
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public String getSingleAttributeResult(String attributeId) {
        List<String> attributeResults = getAttributeResults(attributeId);
        return attributeResults.isEmpty() ? null : attributeResults.get(0);
    }

    public List<String> getAttributeResults(String attributeId) {
        return searchResults.stream()
                .map(searchResult -> getAttribute(searchResult, attributeId))
                .collect(Collectors.toList());
    }

    private String getAttribute(SearchResult searchResult, String attributeId) {
        if (searchResult == null) {
            return null;
        }

        Attribute entry = searchResult.getAttributes().get(attributeId);

        if (entry == null) {
            log.warn("The attribute with ID '{}' has not been found.", attributeId);
            return null;
        }

        try {
            return entry.get().toString();
        } catch (NamingException ex) {
            log.error("Failed to get attribute value", ex);
            return null;
        }
    }

    private LdapContext buildLdapContext() throws NamingException {
        config.putIfAbsent(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_INITIAL_CONTEXT_FACTORY);
        config.putIfAbsent(Context.SECURITY_AUTHENTICATION, DEFAULT_SECURITY_AUTHENTICATION);

        String protocol = config.getProperty(Context.SECURITY_PROTOCOL);
        config.putIfAbsent(Context.PROVIDER_URL, createDefaultProviderUrl(protocol));

        if (log.isDebugEnabled()) {
            log.debug("Using following InitialLdapContext properties:");
            log.debug("Initial Context Factory: {}", config.getProperty(Context.INITIAL_CONTEXT_FACTORY));
            log.debug("Authentication Type: {}", config.getProperty(Context.SECURITY_AUTHENTICATION));
            log.debug("Protocol: {}", config.getProperty(Context.SECURITY_PROTOCOL));
            log.debug("Provider URL: {}", config.getProperty(Context.PROVIDER_URL));
            log.debug("User DN: {}", config.getProperty(Context.SECURITY_PRINCIPAL));
            log.debug("Password: {}", config.getProperty(Context.SECURITY_CREDENTIALS));
        }

        return new InitialLdapContext(config, null);
    }

    private String createDefaultProviderUrl(String protocol) {
        String port = "ssl".equalsIgnoreCase(protocol) ? "636" : "389";
        return "ldap://localhost:" + port;
    }

    private SearchControls createSearchControls() {
        SearchControls searchControls = new SearchControls();

        String searchScope = config.getProperty(SEARCH_SCOPE);
        if (searchScope != null) {
            searchControls.setSearchScope(parseSearchScope(searchScope));
        }

        return searchControls;
    }

    private int parseSearchScope(String searchScope) {
        log.debug("Search scope: {}", searchScope);

        try {
            return SearchScope.valueOf(searchScope).ordinal();
        } catch (IllegalArgumentException ex) {
            return SearchScope.ONELEVEL_SCOPE.ordinal();
        }
    }

    public enum SearchScope {
        OBJECT_SCOPE, ONELEVEL_SCOPE, SUBTREE_SCOPE
    }

}
