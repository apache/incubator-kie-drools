package org.jbpm.formbuilder.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.jbpm.formbuilder.server.file.FileException;
import org.jbpm.formbuilder.server.file.FileService;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FileUploadServlet extends UploadAction {

    private static final long serialVersionUID = 560635045151739627L;
    

    private FileService fileService = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        fileService = ServiceFactory.getInstance().getFileService();
    }
    
    public String executeAction(HttpServletRequest request,
            List<FileItem> sessionFiles) throws UploadActionException {
        String packageName = request.getParameter("packageName");
        for (FileItem item : getSessionFileItems(request)) {
            if (!item.isFormField()) {
                String fileName = item.getName();
                byte[] content = item.get();
                if (packageName != null && !"".equals(packageName)) {
                    // uploadFile called. Store it.
                    try {
                        return fileService.storeFile(packageName, fileName, content);
                    } catch (FileException e) {
                        request.getSession().getServletContext().log("Couldn't store file", e);
                    }
                } else {
                    // uploadAction called it from a formItem. Do nothing
                }
            }
        }
        super.removeSessionFileItems(request);
        return "file(s) uploaded";
    }
}
