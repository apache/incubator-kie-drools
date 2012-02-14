/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.client.command;

import org.jbpm.formapi.common.panels.ConfirmDialog;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class LanguageCommand implements BaseCommand {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    @Override
    public void execute() {
        /* do nothing */
    }

    @Override
    public void setEmbeded(String profile) {
        /* do not disable */
    }

    @Override
    public void setItem(final MenuItem item) {
        MenuBar subMenu = new MenuBar(true);
        String[] availableLocaleNames = LocaleInfo.getAvailableLocaleNames();
        for (final String localeName : availableLocaleNames) {
            String html = LocaleInfo.getLocaleNativeDisplayName(localeName);
            if (html == null || "".equals(html)) {
                html = i18n.LocaleDefault();
            }
            subMenu.addItem(html, new Command() {
                @Override
                public void execute() {
                    reloadLocale(localeName, item);
                }
            });
        }
        item.setSubMenu(subMenu);
        item.setCommand(null);
    }

    protected void reloadLocale(final String localeName, final MenuItem target) {
        final ConfirmDialog dialog = new ConfirmDialog(i18n.WarningLocaleReload());
        dialog.addOkButtonHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String href = Window.Location.getHref();
                if (href.contains("&locale=")) {
                    href = removeParameterFromHref("locale", href);
                } else if (href.contains("?locale=")) {
                    href = removeParameterFromHref("locale", href);
                }
                if (href.contains("?")) {
                    href += "&locale=" + localeName;
                } else {
                    href += "?locale=" + localeName;
                }
                Window.Location.replace(href);
                dialog.hide();
            }
        });
        dialog.showRelativeTo(target);
    }
    
    protected String removeParameterFromHref(String paramName, String href) {
        String retval = href;
        int fromIndex = href.indexOf(paramName) - 1;
        if (fromIndex > 0) {
            int toIndex = href.indexOf("&", fromIndex + 1);
            if (toIndex < fromIndex) {
                toIndex = href.length();
            }
            String firstPart = href.substring(0, fromIndex);
            String lastPart = href.length() == toIndex ? "" : href.substring(toIndex);
            retval = firstPart + lastPart;
        }
        return retval;
    }
}
