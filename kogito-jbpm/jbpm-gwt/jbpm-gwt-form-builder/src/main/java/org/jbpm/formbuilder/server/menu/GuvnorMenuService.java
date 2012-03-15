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
package org.jbpm.formbuilder.server.menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.drools.repository.utils.IOUtils;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formapi.shared.menu.MenuOptionDescription;
import org.jbpm.formapi.shared.menu.ValidationDescription;
import org.jbpm.formbuilder.shared.menu.AbstractBaseMenuService;
import org.jbpm.formbuilder.shared.menu.MenuServiceException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GuvnorMenuService extends AbstractBaseMenuService {

    @Override
    public List<MenuOptionDescription> listOptions() throws MenuServiceException {
        Gson gson = new Gson();
        List<MenuOptionDescription> retval = null;
        try {
            URL url = asURL("/menuOptions.json");
            retval = gson.fromJson(createReader(url), new TypeToken<List<MenuOptionDescription>>(){}.getType());
        } catch (URISyntaxException e) {
            throw new MenuServiceException("Problem finding menu options json file", e); 
        } catch (FileNotFoundException e) {
            throw new MenuServiceException("No menu options json file found", e);
        } catch (Exception e) {
            throw new MenuServiceException("Unexpected error", e);
        }
        return retval;
    }

    @Override
    public Map<String, List<MenuItemDescription>> listMenuItems() throws MenuServiceException {
        Map<String, List<MenuItemDescription>> retval = null;
        try {
            FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
            URL url = asURL("/menuItems.json");
            String json = readURL(url);
            retval = decoder.decodeMenuItemsMap(json);
        } catch (FormEncodingException e) {
            throw new MenuServiceException("Problem parsing menu items json file", e);
        } catch (URISyntaxException e) {
            throw new MenuServiceException("Problem finding menu items json file", e);
        } catch (FileNotFoundException e) {
            throw new MenuServiceException("No menu items json file found", e);
        } catch (IOException e) {
            throw new MenuServiceException("Problem reading menu items json file", e);
        } catch (Exception e) {
            throw new MenuServiceException("Unexpected error", e);
        }
        return retval;
    }

    @Override
    public List<ValidationDescription> listValidations() throws MenuServiceException {
        Gson gson = new Gson();
        List<ValidationDescription> retval = null;
        try {
            URL url = asURL("/validations.json");
            retval = gson.fromJson(createReader(url), new TypeToken<List<ValidationDescription>>(){}.getType());
        } catch (URISyntaxException e) {
            throw new MenuServiceException("Problem finding validations json file", e); 
        } catch (FileNotFoundException e) {
            throw new MenuServiceException("No validations json file found", e);
        } catch (Exception e) {
            throw new MenuServiceException("Unexpected error", e);
        }
        return retval;
    }
    
    @Override
    public void saveMenuItem(String groupName, MenuItemDescription item) throws MenuServiceException {
        Map<String, List<MenuItemDescription>> items = listMenuItems();
        addToMap(groupName, item, items);
        writeMenuItems(items);
    }
    
    @Override
    public void deleteMenuItem(String groupName, MenuItemDescription item) throws MenuServiceException {
        Map<String, List<MenuItemDescription>> items = listMenuItems();
        removeFromMap(groupName, item, items);
        writeMenuItems(items);
    }

    @Override
    public Map<String, String> getFormBuilderProperties() throws MenuServiceException {
        InputStream input = getClass().getResourceAsStream("/FormBuilder.properties");
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new MenuServiceException("Couldn't read FormBuilder.properties", e);
        }
        Map<String, String> retval = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            retval.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return retval;
    }
    
    private void writeMenuItems(Map<String, List<MenuItemDescription>> items) throws MenuServiceException {
        try {
            FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
            String json = encoder.encodeMenuItemsMap(items);
            URL url = asURL("/menuItems.json");
            writeToURL(url, json);
        } catch (FormEncodingException e) {
            throw new MenuServiceException("Problem transforming menu items to json", e);
        } catch (URISyntaxException e) {
            throw new MenuServiceException("Problem finding menu items json file", e);
        } catch (FileNotFoundException e) {
            throw new MenuServiceException("No menu items json file found", e);
        } catch (IOException e) {
            throw new MenuServiceException("Problem writing menu items json file", e);
        } catch (Exception e) {
            throw new MenuServiceException("Unexpected error", e);
        }
    }

    protected void writeToURL(URL url, String json) throws FileNotFoundException, IOException {
        if (url.toExternalForm().startsWith("vfs")) {
            FileObject to = VFS.getManager().resolveFile(url.toExternalForm());
            File tmpFile = File.createTempFile("xxFilexx", ".json");
            FileUtils.writeStringToFile(tmpFile, json);
            FileObject from = VFS.getManager().toFileObject(tmpFile);
            to.copyFrom(from, new AllFileSelector());
            FileUtils.deleteQuietly(tmpFile);
        } else {
            FileUtils.writeStringToFile(FileUtils.toFile(url), json);
        }
    }
    
    protected URL asURL(String path) throws URISyntaxException {
        return getClass().getResource(path);
    }
    
    protected Reader createReader(URL url) throws FileNotFoundException, IOException {
        return new InputStreamReader(url.openStream());
    }

    protected String readURL(URL url) throws FileNotFoundException, IOException {
        if (url.toExternalForm().startsWith("vfs")) {
            FileObject from = VFS.getManager().resolveFile(url.toExternalForm());
            return IOUtils.toString(from.getContent().getInputStream());
        } else {
            return FileUtils.readFileToString(FileUtils.toFile(url));
        }
    }
}
