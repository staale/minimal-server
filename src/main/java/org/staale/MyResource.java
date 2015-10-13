package org.staale;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by staaleu on 19/9/15.
 */
public interface MyResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("myresource")
    Map<String, String> getIt();

    @GET
    @Path("/webjars/{filename:.*}")
    Response webjars(@PathParam("filename") String filename,
                     @HeaderParam("If-Modified-Since") Date date) throws IOException;
}
