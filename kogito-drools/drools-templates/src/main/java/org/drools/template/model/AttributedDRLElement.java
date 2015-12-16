/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.template.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Intermediary class, extending DRLElement with the capability of
 * storing attributes, as required for rules and the package itself.
 */
public abstract class AttributedDRLElement extends DRLElement {

    private Map<String, String> _attr2value = new HashMap<String, String>();

    protected AttributedDRLElement() {
    }

    protected AttributedDRLElement(final Integer salience) {
        if (salience != null) {
            this._attr2value.put("salience", Integer.toString(salience));
        }
    }

    protected void renderDRL(final DRLOutput out) {
        for (Map.Entry<String, String> entry : _attr2value.entrySet()) {
            String attribute = entry.getKey();
            String value = entry.getValue();
            out.writeLine("\t" + attribute + " " + value);
        }
    }

    protected String asStringLiteral(String value) {
        // Keep the quotes if they come in the right places.
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }
        return '"' + value.replaceAll("\"", Matcher.quoteReplacement("\\\"")) + '"';
    }

    protected String asTimerLiteral(String value) {
        // Keep the brackets if they come in the right places.
        if (value.startsWith("(") && value.endsWith(")") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }
        return "(" + value + ")";
    }

    public void setSalience(final Integer value) {
        this._attr2value.put("salience", Integer.toString(value));
    }

    public void setSalience(final String value) {
        this._attr2value.put("salience", value);
    }

    public void setDuration(final Long value) {
        this._attr2value.put("duration", Long.toString(value));
    }

    public void setTimer(final String value) {
        this._attr2value.put("timer", asTimerLiteral(value));
    }

    public void setEnabled(final boolean value) {
        this._attr2value.put("enabled", Boolean.toString(value));
    }

    public void setCalendars(final String value) {
        this._attr2value.put("calendars", asStringLiteral(value));
    }

    public void setActivationGroup(final String value) {
        this._attr2value.put("activation-group", asStringLiteral(value));
    }

    public void setRuleFlowGroup(final String value) {
        this._attr2value.put("ruleflow-group", asStringLiteral(value));
    }

    public void setAgendaGroup(final String value) {
        this._attr2value.put("agenda-group", asStringLiteral(value));
    }

    public void setNoLoop(final boolean value) {
        this._attr2value.put("no-loop", Boolean.toString(value));
    }

    public void setLockOnActive(final boolean value) {
        this._attr2value.put("lock-on-active", Boolean.toString(value));
    }

    public void setAutoFocus(final boolean value) {
        this._attr2value.put("auto-focus", Boolean.toString(value));
    }

    public void setDateEffective(final String value) {
        this._attr2value.put("date-effective", asStringLiteral(value));
    }

    public void setDateExpires(final String value) {
        this._attr2value.put("date-expires", asStringLiteral(value));
    }

    public String getAttribute(String name) {
        return this._attr2value.get(name).toString();
    }

    public String getSalience() {
        return this._attr2value.get("salience");
    }


}
