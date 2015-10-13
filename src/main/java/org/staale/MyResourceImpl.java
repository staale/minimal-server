package org.staale;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/")
public class MyResourceImpl implements MyResource {

    private static final Map<String, MediaType> MEDIA_TYPES = new HashMap<>();

    static {
        MEDIA_TYPES.put("html", MediaType.TEXT_HTML_TYPE);
        MEDIA_TYPES.put("css", MediaType.valueOf("text/css"));
        MEDIA_TYPES.put("js", MediaType.valueOf("application/javascript"));
    }

    @Override
    public Map<String, String> getIt() {
        return Collections.singletonMap("key", "value");
    }

    @Override
    public Response webjars(final String filename, final Date date) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL content = classLoader.getResource("META-INF/resources/webjars/" + filename);
        if (content == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(filename)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }
        final MediaType mediaType = MEDIA_TYPES.entrySet().stream()
                .filter(entry -> content.getFile().endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        final URLConnection urlConnection = content.openConnection();
        if (date != null && date.getTime() >= urlConnection.getLastModified()) {
            return Response
                    .notModified()
                    .type(mediaType)
                    .build();
        }
        return Response
                .ok()
                .entity(urlConnection.getInputStream())
                .lastModified(new Date(urlConnection.getLastModified()))
                .type(mediaType)
                .build();
    }
}
