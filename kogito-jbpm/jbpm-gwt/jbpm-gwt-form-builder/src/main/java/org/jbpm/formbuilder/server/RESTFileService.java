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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jbpm.formbuilder.server.file.FileException;
import org.jbpm.formbuilder.server.file.FileService;
import org.jbpm.formbuilder.server.xml.FileListDTO;

@Path("/files")
public class RESTFileService extends RESTBaseService {

    private FileService fileService = null;
    
    protected void setContext(ServletContext context) {
        if (fileService == null) {
        	this.fileService = ServiceFactory.getInstance().getFileService();
        }
    }
    
    @POST @Path("/package/{pkgName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @DoNotUseJAXBProvider
    public Response saveFile(@PathParam("pkgName") String packageName, @Context HttpServletRequest request) {
        setContext(request.getSession().getServletContext());
        if (isMultipart(request)) {
            //read multipart request and populate request accordingly for display
            ServletFileUpload upload = createFileUpload();
            try {
                List<?> files = parseFiles(request, upload);
                if (files == null || files.isEmpty()) {
                    return error("there should be one file at least", null);
                }
                FileItem item = (FileItem) files.iterator().next();
                byte[] content = readItem(item);
                String fileName = item.getName();
                String expositionUrl = fileService.storeFile(packageName, fileName, content);
                return Response.ok(expositionUrl, MediaType.TEXT_PLAIN).build();
            } catch (FileException e) {
                return error("Problem storing file to guvnor", e);
            } catch (IOException e) {
                return error("Problem reading input of file", e);
            } catch (FileUploadException e) {
                return error("Problem reading upload of file", e);
            }
        } else {
            return error("Must be a multipart form data post", null);
        }
    }

    protected boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    protected byte[] readItem(FileItem item) throws IOException {
        return IOUtils.toByteArray(item.getInputStream());
    }

    protected List<?> parseFiles(HttpServletRequest request, ServletFileUpload upload)
            throws FileUploadException {
        return upload.parseRequest(request);
    }

    protected ServletFileUpload createFileUpload() {
        int maxMemorySize = 2400000;
        File tmpDirectory = new File(System.getProperty("java.io.tmpdir"));
        DiskFileItemFactory factory = new DiskFileItemFactory(maxMemorySize, tmpDirectory);
        ServletFileUpload upload = new ServletFileUpload(factory);
        return upload;
    }
    
    @DELETE @Path("/package/{pkgName}/{fileName}")
    public Response deleteFile(@Context HttpServletRequest request, @PathParam("pkgName") String packageName, @PathParam("fileName") String fileName) {
        setContext(request.getSession().getServletContext());
        try {
            fileService.deleteFile(packageName, fileName);
            return Response.noContent().build();
        } catch (FileException e) {
            return error("Problem deleting file in guvnor", e);
        }
    }

    @GET @Path("/package/{pkgName}/")
    public Response getFiles(@Context HttpServletRequest request, @PathParam("pkgName") String packageName, @QueryParam("type") String[] fileTypes) {
        setContext(request.getSession().getServletContext());
        try {
        	List<String> allFiles = new ArrayList<String>();
        	for (String fileType : fileTypes) {
        		allFiles.addAll(fileService.loadFilesByType(packageName, fileType));
        	}
        	Collections.sort(allFiles);
            FileListDTO dto = new FileListDTO(allFiles);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (FileException e) {
            return error("Problem loading file names", e);
        }
    }
    
    @GET @Path("/package/{pkgName}/{fileName}")
    public Response getFile(@Context HttpServletRequest request, @PathParam("pkgName") String packageName, @PathParam("fileName") String fileName) {
        setContext(request.getSession().getServletContext());
        try {
            byte[] content = fileService.loadFile(packageName, fileName);
            return Response.ok(content, MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (FileException e) {
            return error("Problem loading file " + fileName, e);
        }
    }
    
    /**
     * @param fileService the fileService to set (for test case purposes)
     */
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
    
    public FileService getFileService() {
        return fileService;
    }
}
