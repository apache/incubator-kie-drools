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
package org.jbpm.formbuilder.server.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.easymock.EasyMock;
import org.jbpm.formbuilder.server.GuvnorHelper;
import org.jbpm.formbuilder.server.mock.MockAnswer;
import org.jbpm.formbuilder.server.mock.MockDeleteMethod;
import org.jbpm.formbuilder.server.mock.MockGetMethod;
import org.jbpm.formbuilder.server.mock.MockPostMethod;
import org.jbpm.formbuilder.server.mock.MockPutMethod;

public class GuvnorFileServiceTest extends TestCase {

    public void testStoreFileOK() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, Integer> statuses = new HashMap<String, Integer>();
        statuses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/fileName-upfile", 404);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses)).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).andReturn(201).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        String url = service.storeFile("somePackage", "fileName.txt", new byte[] { 1,2,3,4,5,6,7,8,9 } );
        EasyMock.verify(client);
        
        assertNotNull("url shouldn't be null", url);
    }
    
    public void testStoreFileOKForUpdate() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, Integer> statuses = new HashMap<String, Integer>();
        statuses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/fileName-upfile", 200);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses)).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).andReturn(201).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andReturn(204).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        String url = service.storeFile("somePackage", "fileName.txt", new byte[] { 1,2,3,4,5,6,7,8,9 } );
        EasyMock.verify(client);
        
        assertNotNull("url shouldn't be null", url);
    }
    
    public void testStoreFileProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, Integer> statuses = new HashMap<String, Integer>();
        statuses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/fileName-upfile", 404);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses)).once();
        IOException exception = new IOException("mock io error");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.storeFile("somePackage", "fileName.txt", new byte[] { 1,2,3,4,5,6,7,8,9 } );
            fail("storeFile(...) should not succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testStoreFileDeleteOlderProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        IOException exception = new IOException("mock io error");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.storeFile("somePackage", "fileName.txt", new byte[] { 1,2,3,4,5,6,7,8,9 } );
            fail("storeFile(...) should not succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            Throwable cause2 = cause.getCause();
            assertNotNull("cause2 shouldn't be null", cause2);
            assertTrue("cause2 should be of type IOException", cause2 instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testDeleteFileOK() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, Integer> statuses = new HashMap<String, Integer>();
        statuses.put("DELETE http://www.redhat.com/rest/packages/somePackage/assets/fileName-upfile", 204);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andAnswer(new MockAnswer(statuses)).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        service.deleteFile("somePackage", "fileName.txt");
        EasyMock.verify(client);
    }
    
    public void testDeleteFileIOProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        IOException exception = new IOException();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteFile("somePackage", "fileName.txt");
            fail("storeFile(...) should not succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }

    public void testDeleteFileUnknownProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        NullPointerException exception = new NullPointerException();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteFile("somePackage", "fileName.txt");
            fail("deleteFile(...) should not succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testLoadFilesByTypeOK() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets><asset>" +
            "<binaryLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/binary</binaryLink>" + 
            "<refLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1</refLink>" +
            "<sourceLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/source</sourceLink>" +
            "<metadata><format>txt</format></metadata>" +
            "</asset></assets>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<String> files = service.loadFilesByType("somePackage", "txt");
        EasyMock.verify(client);
        
        assertNotNull("files shouldn't be null", files);
        assertFalse("files shouldn't be empty", files.isEmpty());
    }
    
    public void testLoadFilesByTypeNoneOfType() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets><asset>" +
            "<binaryLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/binary</binaryLink>" + 
            "<refLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1</refLink>" +
            "<sourceLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/source</sourceLink>" +
            "<metadata><format>drg</format></metadata>" +
            "</asset></assets>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<String> files = service.loadFilesByType("somePackage", "txt");
        EasyMock.verify(client);
        
        assertNotNull("files shouldn't be null", files);
        assertTrue("files should be empty", files.isEmpty());
    }
    
    public void testLoadFilesByTypeNoTypeSpecified() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets><asset>" +
            "<binaryLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/binary</binaryLink>" + 
            "<refLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1</refLink>" +
            "<sourceLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/source</sourceLink>" +
            "<metadata><format>drg</format></metadata>" +
            "</asset></assets>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<String> files = service.loadFilesByType("somePackage", "");
        EasyMock.verify(client);
        
        assertNotNull("files shouldn't be null", files);
        assertFalse("files shouldn't be empty", files.isEmpty());
    }
    
    public void testLoadFilesByTypeNoTypeSpecified2() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets><asset>" +
            "<binaryLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/binary</binaryLink>" + 
            "<refLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1</refLink>" +
            "<sourceLink>http://www.redhat.com/rest/packages/somePackage/assets/asset1/source</sourceLink>" +
            "<metadata><format>drg</format></metadata>" +
            "</asset></assets>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<String> files = service.loadFilesByType("somePackage", null);
        EasyMock.verify(client);
        
        assertNotNull("files shouldn't be null", files);
        assertFalse("files shouldn't be empty", files.isEmpty());
    }
    
    public void testLoadFilesByTypeEmpty() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets></assets>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<String> files = service.loadFilesByType("somePackage", "txt");
        EasyMock.verify(client);
        
        assertNotNull("files shouldn't be null", files);
        assertTrue("files should be empty", files.isEmpty());
    }
    
    public void testLoadFilesByTypeIOProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        IOException exception = new IOException("mock io error");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.loadFilesByType("somePackage", "txt");
            fail("loadFilesByType(...) shouldn't succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testLoadFilesByTypeJAXBProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String props = "<?xml version=\"1.0\"?><assets></assetsBROKENXMLWHATSHAPPENINGITOLDYOUBRO>";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/", props);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.loadFilesByType("somePackage", "txt");
            fail("loadFilesByType(...) shouldn't succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type JAXBException", cause instanceof JAXBException);
        }
        EasyMock.verify(client);
    }

    public void testLoadFilesByTypeUnknownProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.loadFilesByType("somePackage", "txt");
            fail("loadFilesByType(...) shouldn't succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testLoadFileOK() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String txt = "This is a test";
        responses.put("GET http://www.redhat.com/rest/packages/somePackage/assets/someFile-upfile/source", txt);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);

        EasyMock.replay(client);
        byte[] retval = service.loadFile("somePackage", "someFile.txt");
        EasyMock.verify(client);
        
        assertNotNull("retval shouldn't be null", retval);
        assertEquals("retval and txt should be the same length", retval.length, txt.length());
        
        for (int i = 0; i < retval.length; i++) {
            assertEquals("byte " + i + " should be the same in both arrays", txt.getBytes()[i], retval[i]);
        }
    }
    
    public void testLoadFileIOProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.loadFile("somePackage", "someFile.txt");
            fail("loadFile(...) shouldn't succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testLoadFileUnknownProblem() throws Exception {
        GuvnorFileService service = createService("http://www.redhat.com", "user", "pass");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.loadFile("somePackage", "someFile.txt");
            fail("loadFile(...) shouldn't succeed");
        } catch (FileException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    private GuvnorFileService createService(String baseUrl, String user, String pass) {
        GuvnorFileService service = new GuvnorFileService();
        service.setHelper(new GuvnorHelper(baseUrl, user, pass) {
            @Override
            public GetMethod createGetMethod(String url) {
                return new MockGetMethod(url);
            }
            @Override
            public PostMethod createPostMethod(String url) {
                return new MockPostMethod(url);
            }
            @Override
            public DeleteMethod createDeleteMethod(String url) {
                return new MockDeleteMethod(url);
            }
            @Override
            public PutMethod createPutMethod(String url) {
                return new MockPutMethod(url);
            }
            @Override
            public void setAuth(HttpClient client, HttpMethod method) {
            }
        });
        return service;
    }
}
