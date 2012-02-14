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
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.server.render.Renderer;
import org.jbpm.formapi.server.render.RendererException;
import org.jbpm.formapi.server.render.RendererFactory;
import org.jbpm.formapi.server.trans.Translator;
import org.jbpm.formapi.server.trans.TranslatorException;
import org.jbpm.formapi.server.trans.TranslatorFactory;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formbuilder.server.xml.FormPreviewDTO;
import org.jbpm.formbuilder.server.xml.ListFormsDTO;
import org.jbpm.formbuilder.server.xml.ListFormsItemsDTO;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.form.FormServiceException;

@Path("/form")
public class RESTFormService extends RESTBaseService {

    private FormDefinitionService formService = null;
    
    public void setContext(@Context ServletContext context) {
        if (formService == null) {
        	this.formService = ServiceFactory.getInstance().getFormDefinitionService();
        }
    }
    
    public RESTFormService() {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    @GET @Path("/definitions/package/{pkgName}")
    public Response getForms(@PathParam("pkgName") String pkgName, @Context ServletContext context) {
        setContext(context);
        try {
            List<FormRepresentation> forms = formService.getForms(pkgName);
            ListFormsDTO dto = new ListFormsDTO(forms);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (FormServiceException e) {
            return error("Problem reading forms of package " + pkgName, e);
        } catch (FormEncodingException e) {
            return error("Problem decoding forms of package " + pkgName, e);
        }
    }
    
    @GET @Path("/definitions/package/{pkgName}/id/{formId}")
    public Response getForm(@PathParam("pkgName") String pkgName, @PathParam("formId") String formId, @Context ServletContext context) {
        setContext(context);
        try {
            FormRepresentation form = formService.getForm(pkgName, formId);
            ListFormsDTO dto = new ListFormsDTO(form);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (FormServiceException e) {
            return error("Problem reading form " + formId, e);
        } catch (FormEncodingException e) {
            return error("Problem decoding form " + formId, e);
        }
    }
    
    @POST @Path("/definitions/package/{pkgName}")
    @Consumes("text/plain")
    @DoNotUseJAXBProvider
    public Response saveForm(String jsonBody, @PathParam("pkgName") String pkgName, @Context HttpServletRequest request) {
        setContext(request.getSession().getServletContext());
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        try {
            FormRepresentation form = decoder.decode(jsonBody);
            String formId = formService.saveForm(pkgName, form);
            return Response.ok("<formId>"+formId+"</formId>", MediaType.APPLICATION_XML).
                status(Status.CREATED).build();
        } catch (FormEncodingException e) {
            return error("Problem decoding form", e);
        } catch (FormServiceException e) {
            return error("Problem saving form", e);
        }
    }
    
    @DELETE @Path("/definitions/package/{pkgName}/id/{formId}") 
    public Response deleteForm(@PathParam("pkgName") String pkgName, @PathParam("formId") String formId, @Context HttpServletRequest request) {
        setContext(request.getSession().getServletContext());
        try {
            formService.deleteForm(pkgName, formId);
            return Response.ok().build();
        } catch (FormServiceException e) {
            return error("Problem deleting form " + formId, e);
        }
    }

    @GET @Path("/items/package/{pkgName}")
    public Response getFormItems(@PathParam("pkgName") String pkgName, @Context ServletContext context) {
        setContext(context);
        try {
            Map<String, FormItemRepresentation> formItems = formService.getFormItems(pkgName);
            ListFormsItemsDTO dto = new ListFormsItemsDTO(formItems);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (FormServiceException e) {
            return error("Problem getting form items of package " + pkgName, e);
        } catch (FormEncodingException e) {
            return error("Problem decoding form items of package " + pkgName, e);
        }
    }
    
    @GET @Path("/items/package/{pkgName}/id/{fItemId}") 
    public Response getFormItem(@PathParam("pkgName") String pkgName, @PathParam("fItemId") String formItemId, @Context ServletContext context) {
        setContext(context);
        try {
            FormItemRepresentation formItem = formService.getFormItem(pkgName, formItemId);
            ListFormsItemsDTO dto = new ListFormsItemsDTO(formItemId, formItem);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (FormServiceException e) {
            return error("Problem reading form item " + formItemId, e);
        } catch (FormEncodingException e) {
            return error("Problem decoding form item " + formItemId, e);
        }
    }
    
    @POST @Path("/items/package/{pkgName}/name/{fItemName}")
    @Consumes("*/*")
    @DoNotUseJAXBProvider
    public Response saveFormItem(String jsonBody,
            @PathParam("pkgName") String pkgName, 
            @PathParam("fItemName") String formItemName, @Context HttpServletRequest request) {
        setContext(request.getSession().getServletContext());
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        try {
            FormItemRepresentation item = decoder.decodeItem(jsonBody);
            String formItemId = formService.saveFormItem(pkgName, formItemName, item);
            return Response.ok("<formItemId>"+formItemId+"</formItemId>", 
                    MediaType.APPLICATION_XML).status(Status.CREATED).build();
        } catch (FormEncodingException e) {
            return error("Problem encoding form item", e);
        } catch (FormServiceException e) {
            return error("Problem saving form item", e);
        }
    }

    @DELETE @Path("/items/package/{pkgName}/name/{fItemName}")
    public Response deleteFormItem(@PathParam("pkgName")String pkgName, @PathParam("fItemName") String formItemName, @Context HttpServletRequest request) {
        setContext(request.getSession().getServletContext());
        try {
            formService.deleteFormItem(pkgName, formItemName);
            return Response.ok().build();
        } catch (FormServiceException e) {
            return Response.noContent().build();
        }
    }
    
    @POST @Path("/preview/lang/{language}")
    public Response getFormPreview(FormPreviewDTO dto, @PathParam("language") String language, 
            @Context ServletContext context, @Context HttpServletRequest request) {
        setContext(context);
        try {
            URL url = createTemplate(language, dto);
            Map<String, Object> inputs = dto.getInputsAsMap();
            Renderer renderer = getRenderer(language);
            inputs.put(Renderer.BASE_CONTEXT_PATH, context.getContextPath());
            Locale locale = request.getLocale();
            inputs.put(Renderer.BASE_LOCALE, locale == null ? "default" : locale.getDisplayName(locale));
            Object html = renderer.render(url, inputs);
            String htmlUrl = createHtmlTemplate(html, language, context);
            return Response.ok(htmlUrl, MediaType.TEXT_PLAIN).build();
        } catch (FormEncodingException e) {
            return error("Problem encoding form preview", e);
        } catch (TranslatorException e) {
            return error("Problem transforming form preview to " + language + " language", e);
        } catch (RendererException e) {
            return error("Problem rendering form preview in " + language + " language", e);
        } catch (IOException e) {
            return error("Problem writing form preview in " + language + " language", e);
        }
    }
    
    @POST @Path("/template/lang/{language}")
    public Response getFormTemplate(FormPreviewDTO dto, @PathParam("language") String language, @Context ServletContext context) {
        setContext(context);
        try {
            URL url = createTemplate(language, dto);
            String fileName = url.getFile();
            return Response.ok("<fileName>"+fileName+"</fileName>", MediaType.APPLICATION_XML).build();
        } catch (FormEncodingException e) {
            return error("Problem encoding form for templating", e);
        } catch (TranslatorException e) {
            return error("Problem transforming form to " + language + " language", e);
        }
    }
    
    @POST @Path("/template/file/{action}")
    public void processFormTemplate(
            @PathParam("action") String action,
            @Context ServletContext context, 
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) {
        try {
            request.setAttribute("org.jbpm.formbuilder.server.REST.processFormTemplate.action", action);
            if (ServletFileUpload.isMultipartContent(request)) {
                //read multipart request and populate request accordingly for display
                int maxMemorySize = 240000;
                File tmpDirectory = new File(System.getProperty("java.io.tmpdir"));
                DiskFileItemFactory factory = new DiskFileItemFactory(maxMemorySize, tmpDirectory);
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<?> files = upload.parseRequest(request);
                for (Object obj : files) {
                    FileItem item = (FileItem) obj;
                    request.setAttribute(item.getFieldName(), item.getString());
                }
            }
            String queryString = request.getQueryString();
            if (queryString == null) {
                queryString = "?";
            }
            if (!queryString.startsWith("?")) {
                queryString = "?" + queryString;
            }
            context.getRequestDispatcher("/fbapi/mockProcess.jsp" + queryString).forward(request, response);
        } catch (Exception e) {
            error("Couldn't process form template", e);
        }
    }

    @GET @Path("/template/file/{file}.temp")
    public Response getHtmlTemplate(@PathParam("file") String fileName) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName + ".temp");
            String content = FileUtils.readFileToString(file);
            return Response.ok(content, MediaType.TEXT_HTML).build();
        } catch (IOException e) {
            return error("Problem reading html template for file " + fileName, e);
        }
    }
    
    private String createHtmlTemplate(Object html, String language, ServletContext context) throws IOException {
        String contextPath = context.getContextPath();
        File file = File.createTempFile("createHtmlTemplate", ".temp");
        FileUtils.writeStringToFile(file, html.toString());
        String url = contextPath + "/rest/form/template/file/" + file.getName();
        return url;
    }
    
    
    private URL createTemplate(String language, FormPreviewDTO dto) throws FormEncodingException, TranslatorException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        String json = dto.getRepresentation();
        FormRepresentation form = decoder.decode(json);
        dto.setForm(form);
        Translator translator = getTranslator(language);
        URL url = translator.translateForm(form);
        return url;
    }
    
    @GET @Path("/template/lang/{language}")
    public Response getExportTemplate(@QueryParam("fileName") String fileName,
            @QueryParam("formName") String formName,
            @PathParam("language") String language, @Context ServletContext context) {
        setContext(context);
        File file = new File(fileName);
        String headerValue = new StringBuilder("attachment; filename=\"").
            append(formName).append('.').append(language).
            append("\"").toString();
        try {
            return Response.ok(FileUtils.readFileToByteArray(file), 
                MediaType.APPLICATION_OCTET_STREAM).
                header("Content-Disposition", headerValue).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    protected Translator getTranslator(String language) throws TranslatorException {
        return TranslatorFactory.getInstance().getTranslator(language);
    }
    
    protected Renderer getRenderer(String language) throws RendererException {
        return RendererFactory.getInstance().getRenderer(language);
    }
    
    /**
     * @param formService the formService to set (for test cases purpose)
     */
    public void setFormService(FormDefinitionService formService) {
        this.formService = formService;
    }

    public FormDefinitionService getFormService() {
        return this.formService;
    }
}
