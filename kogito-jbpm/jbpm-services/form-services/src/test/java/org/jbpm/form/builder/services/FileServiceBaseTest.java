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
package org.jbpm.form.builder.services;

import java.io.File;
import java.util.List;
import javax.inject.Inject;


import org.apache.commons.io.FileUtils;
import org.jbpm.form.builder.services.impl.fs.FSFileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class FileServiceBaseTest {

    private String baseUrl = "/tmp/fileServiceBaseTestFolder";
    @Inject
    private FSFileService service;

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(new File(baseUrl));
        service.setBaseUrl(baseUrl);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(baseUrl));
    }

    @Test
    public void testStoreFileOK() throws Exception {


        String url = service.storeFile("fileName.txt", "This is the file content".getBytes());
        String readFileToString = FileUtils.readFileToString(new File(url));
        assertEquals("This is the file content", readFileToString);

        service.deleteFile(url);

    }

    @Test
    public void testDeleteFileOK() throws Exception {


        String url = service.storeFile("fileName.txt", "This is the file content".getBytes());

        service.deleteFileByURL(url);

        assertFalse(new File(url).exists());



    }

    @Test
    public void testLoadFilesByTypeOK() throws Exception {


        String url = service.storeFile("fileName1.txt", "This is the file content 1".getBytes());
        String url2 = service.storeFile("fileName2.txt", "This is the file content 2".getBytes());

        List<String> files = service.loadFilesByType("txt");

        assertEquals(2, files.size());
        boolean urlOk = false;
        boolean url2Ok = false;
        if (url.equals(files.get(0)) || url.equals(files.get(1))) {
            urlOk = true;
        }
        if (url2.equals(files.get(0)) || url2.equals(files.get(1))) {
            url2Ok = true;
        }

        assertTrue(urlOk);
        assertTrue(url2Ok);

        service.deleteFileByURL(url);

        service.deleteFileByURL(url2);

    }

//    @Test
//    public void testLoadFilesByTypeNoTypeSpecified() throws Exception {
//
//
//        List<String> files = service.loadFilesByType("somePackage", "*");
//
//        assertEquals(0, files.size());
//
//
//    }

    @Test
    public void testLoadFileOK() throws Exception {


        String url = service.storeFile("someFile.txt", "This is the file content".getBytes());

        byte[] retval = service.loadFile("someFile.txt");

        assertEquals("This is the file content", new String(retval));


        service.deleteFileByURL(url);


    }
}
