package org.jbpm.formbuilder.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;

@Path("/user")
public class RESTUserService extends RESTBaseService {

    private static final String[] AVAILABLE_ROLES = new String[] { 
        "admin", "webdesigner", "functionalanalyst" 
    };
    
    @GET @Path("/current/roles")
    @Consumes("*/*")
    @Produces("text/plain")
    @DoNotUseJAXBProvider
    public Response getCurrentRoles(@Context HttpServletRequest request) {
        List<String> roles = getRoles(request);
        StringBuilder txtRoles = new StringBuilder();
        for (Iterator<String> iter = roles.iterator(); iter.hasNext(); ) {
            txtRoles.append(iter.next());
            if (iter.hasNext()) {
                txtRoles.append(",");
            }
        }
        return Response.ok(txtRoles.toString()).build();
    }

    @POST @Path("/current/logout")
    public Response logout(@Context HttpServletRequest request) {
        request.getSession().invalidate();
        return Response.ok().build();
    }
    
    public static List<String> getRoles(HttpServletRequest request) {
        List<String> roles = new ArrayList<String>();
        for (String role : AVAILABLE_ROLES) {
            if (request.isUserInRole(role)) {
                roles.add(role);
            }
        }
        return roles;
    }

    public static boolean hasDesignerPrivileges(HttpServletRequest request) {
        List<String> roles = getRoles(request);
        return roles.contains("admin") || roles.contains("webdesigner");
    }
}
