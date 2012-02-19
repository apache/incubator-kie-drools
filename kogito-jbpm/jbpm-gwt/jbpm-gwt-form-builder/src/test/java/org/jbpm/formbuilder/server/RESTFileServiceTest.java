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
package org.jbpm.formbuilder.server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.easymock.EasyMock;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jbpm.formbuilder.server.file.FileException;
import org.jbpm.formbuilder.server.file.FileService;
import org.jbpm.formbuilder.server.xml.FileListDTO;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RESTFileServiceTest extends RESTAbstractTest {

    public void testSetContextOK() throws Exception {
        RESTFileService restService = new RESTFileService();
        URL pathToClasses = getClass().getResource("/FormBuilder.properties");
		String filePath = pathToClasses.toExternalForm();
		//assumes compilation is in target/classes
		filePath = filePath.replace("target/classes/FormBuilder.properties", "src/main/webapp");
		filePath = filePath + "/WEB-INF/springComponents.xml";
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(filePath);
		ServiceFactory.getInstance().setBeanFactory(ctx);
        ServletContext context = EasyMock.createMock(ServletContext.class);

        EasyMock.replay(context);
        restService.setContext(context);
        EasyMock.verify(context);

        FileService service = restService.getFileService();
        assertNotNull("service shouldn't be null", service);
    }

    public void testError() throws Exception {
        RESTFileService restService = new RESTFileService();
        
        String msg = "mock error message";
        Exception err = new NullPointerException();
        
        Response resp0 = restService.error(null, null);
        assertNotNull("resp0 shouldn't be null", resp0);
        assertStatus(resp0.getStatus(), Status.INTERNAL_SERVER_ERROR);
        Response resp1 = restService.error(msg, null);
        assertNotNull("resp1 shouldn't be null", resp1);
        assertStatus(resp1.getStatus(), Status.INTERNAL_SERVER_ERROR);
        Response resp2 = restService.error(msg, err);
        assertNotNull("resp2 shouldn't be null", resp2);
        assertStatus(resp2.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path of RESTFileService.deleteFile(...)
    public void testDeleteFileOK() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        fileService.deleteFile(EasyMock.same("somePackage"), EasyMock.same("myFile.tmp"));
        EasyMock.expectLastCall().once();
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.deleteFile((HttpServletRequest) mocks[0], "somePackage", "myFile.tmp");
        EasyMock.verify(mocks);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.NO_CONTENT);
        
    }
    
    //test response to a FileException of RESTFileService.deleteFile(...)
    public void testDeleteFileServiceProblem() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        FileException exception = new FileException("Something went wrong", new NullPointerException());
        fileService.deleteFile(EasyMock.same("somePackage"), EasyMock.same("myFile.tmp"));
        EasyMock.expectLastCall().andThrow(exception).once();
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.deleteFile((HttpServletRequest) mocks[0], "somePackage", "myFile.tmp");
        EasyMock.verify(mocks);
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFileService.getFiles(...) returning files
    public void testGetFilesOK() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        List<String> retval = new ArrayList<String>();
        retval.add("myFile1.tmp");
        retval.add("myFile2.tmp");
        EasyMock.expect(fileService.loadFilesByType(EasyMock.same("somePackage"), EasyMock.same("tmp"))).
            andReturn(retval);
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.getFiles((HttpServletRequest) mocks[0], "somePackage", new String[] {"tmp"}); 
        EasyMock.verify(mocks);
        Object objDto = assertXmlOkResponse(resp);
        assertTrue("objDto should be of type FileListDTO", objDto instanceof FileListDTO);
        FileListDTO dto = (FileListDTO) objDto;
        assertNotNull("dto.getFile() shouldn't be null", dto.getFile());
        for (String file : dto.getFile()) {
            assertTrue("retval should contain " + file, retval.contains(file));
        }
        for (String file : retval) {
            assertTrue("dto.getFile() should contain " + file, dto.getFile().contains(file));
        }
    }
    
    //test happy path for RESTFileService.getFiles(...) returning no files
    public void testGetFilesEmpty() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        List<String> retval = new ArrayList<String>();
        EasyMock.expect(fileService.loadFilesByType(EasyMock.same("somePackage"), EasyMock.same("tmp"))).
            andReturn(retval);
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.getFiles((HttpServletRequest) mocks[0], "somePackage", new String[] {"tmp"}); 
        EasyMock.verify(mocks);
        Object objDto = assertXmlOkResponse(resp);
        assertTrue("objDto should be of type FileListDTO", objDto instanceof FileListDTO);
        FileListDTO dto = (FileListDTO) objDto;
        assertNotNull("dto.getFile() shouldn't be null", dto.getFile());
        assertTrue("dto.getFile() should be empty", dto.getFile().isEmpty());
    }
    
    //test response to a FileException of RESTFileService.getFiles(...)
    public void testGetFilesServiceProblem() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        FileException exception = new FileException();
        EasyMock.expect(fileService.loadFilesByType(EasyMock.same("somePackage"), EasyMock.same("tmp"))).
            andThrow(exception);
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.getFiles((HttpServletRequest) mocks[0], "somePackage", new String[] {"tmp"}); 
        EasyMock.verify(mocks);
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFileService.getFile(...)
    public void testGetFileOK() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        byte[] myContent = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
        EasyMock.expect(fileService.loadFile(EasyMock.same("somePackage"), EasyMock.same("myFile.tmp"))).
            andReturn(myContent);
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.getFile((HttpServletRequest) mocks[0], "somePackage", "myFile.tmp");
        EasyMock.verify(mocks);
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.metadata[Content-Type] shouldn't be null", contentType);
        assertEquals("resp.metadata[Content-Type] should be application/octet-stream but is " + contentType, 
                contentType, MediaType.APPLICATION_OCTET_STREAM);
        Object objDto = resp.getEntity();
        assertTrue("objDto should be an array", objDto.getClass().isArray());
        assertTrue("objDto should be a byte array", objDto instanceof byte[]);
        byte[] retval = (byte[]) objDto;
        assertEquals("retval should be the same as " + myContent + " but is " + retval, retval, myContent);
    }
    
    //test response to a FileException for RESTFileService.getFile(...)
    public void testGetFileServiceProblem() throws Exception {
        RESTFileService restService = new RESTFileService();
        List<Object> requestMocks = createRequestMocks();
        FileService fileService = EasyMock.createMock(FileService.class);
        FileException exception = new FileException(new NullPointerException());
        EasyMock.expect(fileService.loadFile(EasyMock.same("somePackage"), EasyMock.same("myFile.tmp"))).
            andThrow(exception);
        requestMocks.add(fileService);
        restService.setFileService(fileService);
        Object[] mocks = requestMocks.toArray();
        EasyMock.replay(mocks);
        Response resp = restService.getFile((HttpServletRequest) mocks[0], "somePackage", "myFile.tmp");
        EasyMock.verify(mocks);
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    public void testSaveFileNotMultipart() throws Exception {
        RESTFileService restService = createSaveFileMockService(null, null, null, false);
        FileService fileService = EasyMock.createMock(FileService.class);
        restService.setFileService(fileService);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        
        EasyMock.replay(fileService, request, session, context);
        Response resp = restService.saveFile("somePackage", request);
        EasyMock.verify(fileService, request, session, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    public void testSaveFileOK() throws Exception {
        byte[] bstream = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String fileName = "fileName";        
        RESTFileService restService = createSaveFileMockService(bstream, fileName, null, true);
        FileService fileService = EasyMock.createMock(FileService.class);
        String url = "http://www.redhat.com";
        EasyMock.expect(fileService.storeFile(EasyMock.eq("somePackage"), EasyMock.eq("fileName"), EasyMock.same(bstream))).
            andReturn(url).once();
        restService.setFileService(fileService);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        
        EasyMock.replay(fileService, request, session, context);
        Response resp = restService.saveFile("somePackage", request);
        EasyMock.verify(fileService, request, session, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.TEXT_PLAIN);
        String retval = entity.toString();
        assertTrue("retval should contain url", retval.contains(url));
    }

    private RESTFileService createSaveFileMockService(final byte[] bstream, final String fileName, 
            final Class<?> exceptionType, final boolean isMultipart) {
        RESTFileService restService = new RESTFileService() {
            @Override
            protected boolean isMultipart(HttpServletRequest request) {
                return isMultipart;
            }
            @Override
            protected ServletFileUpload createFileUpload() {
                return null;
            }
            @Override
            protected List<?> parseFiles(HttpServletRequest request, ServletFileUpload upload) throws FileUploadException {
                if (exceptionType != null && exceptionType.equals(FileUploadException.class)) {
                    throw new FileUploadException();
                }
                List<FileItem> retval = new ArrayList<FileItem>();
                retval.add(new DiskFileItem("fieldName", "application/octet-stream", true, fileName, 0, null));
                return retval;
            }
            @Override
            protected byte[] readItem(FileItem item) throws IOException {
                if (exceptionType != null && exceptionType.equals(IOException.class)) {
                    throw new IOException("mock io error");
                }
                return bstream;
            }
        };
        return restService;
    }
    
    public void testSaveFileServiceProblem() throws Exception {
        byte[] bstream = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String fileName = "fileName";        
        RESTFileService restService = createSaveFileMockService(bstream, fileName, null, true);
        FileService fileService = EasyMock.createMock(FileService.class);
        FileException exception = new FileException();
        EasyMock.expect(fileService.storeFile(EasyMock.eq("somePackage"), EasyMock.eq("fileName"), EasyMock.same(bstream))).
            andThrow(exception).once();
        restService.setFileService(fileService);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        
        EasyMock.replay(fileService, request, session, context);
        Response resp = restService.saveFile("somePackage", request);
        EasyMock.verify(fileService, request, session, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    public void testSaveFileUploadProblem() throws Exception {
        byte[] bstream = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String fileName = "fileName";        
        RESTFileService restService = createSaveFileMockService(bstream, fileName, FileUploadException.class, true);
        FileService fileService = EasyMock.createMock(FileService.class);
        restService.setFileService(fileService);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        
        EasyMock.replay(fileService, request, session, context);
        Response resp = restService.saveFile("somePackage", request);
        EasyMock.verify(fileService, request, session, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    public void testSaveFileIOProblem() throws Exception {
        byte[] bstream = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String fileName = "fileName";        
        RESTFileService restService = createSaveFileMockService(bstream, fileName, IOException.class, true);
        FileService fileService = EasyMock.createMock(FileService.class);
        restService.setFileService(fileService);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        
        EasyMock.replay(fileService, request, session, context);
        Response resp = restService.saveFile("somePackage", request);
        EasyMock.verify(fileService, request, session, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    private List<Object> createRequestMocks() {
        List<Object> requestMocks = new ArrayList<Object>();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();
        EasyMock.expect(session.getServletContext()).andReturn(context).once();
        requestMocks.add(request);
        requestMocks.add(session);
        requestMocks.add(context);
        return requestMocks;
    }
}
