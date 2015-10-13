package org.staale;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.encoder.EncoderBase;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author <a href="mailto:su@ums.no">Ståle Undheim</a>
 */
public class LogstashEncoder extends EncoderBase<ILoggingEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private static final byte[] LINE_SEPARATOR =
            System.getProperty("line.separator").getBytes(Charset.defaultCharset());


    @Override
    public void doEncode(final ILoggingEvent event) throws IOException {
        final ObjectNode objectNode = MAPPER.createObjectNode();

        // Write MDC properties first, so any fixed value will overwrite any mdc property
        for (Map.Entry<String, String> entry : event.getMDCPropertyMap().entrySet()) {
            objectNode.put(entry.getKey(), entry.getValue());
        }

        final Instant eventInstant = Instant.ofEpochMilli(event.getTimeStamp());
        final ZonedDateTime eventDateTime = ZonedDateTime.ofInstant(eventInstant, ZoneOffset.UTC);
        objectNode.put("@timestamp", eventDateTime.format(FORMAT));
        objectNode.put("message", event.getFormattedMessage());
        if (event.getThrowableProxy() != null) {
            objectNode.put("exception", ThrowableProxyUtil.asString(event.getThrowableProxy()));
        }
        objectNode.put("level", event.getLevel().levelStr);
        objectNode.put("logger", event.getLoggerName());
        objectNode.put("thread", event.getThreadName());
        objectNode.put("application", "backbone-facade");

        MAPPER.writeValue(outputStream, objectNode);
        outputStream.write(LINE_SEPARATOR);
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.write(LINE_SEPARATOR);
    }

}
