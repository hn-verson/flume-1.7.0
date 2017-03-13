package org.apache.flume.source.avro;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.event.JSONEvent;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by verson on 2017/3/11.
 */
public class Log4jHandler extends AbstractHandler implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(Log4jHandler.class);
    private static final String CHARSET_NAME = "UTF-8";

    private final static String LOG_LEVEL = "flume.client.log4j.log.level";
    private final static String MESSAGE_ENCODING = "flume.client.log4j.message.encoding";
    private final static String LOGGER_NAME = "flume.client.log4j.logger.name";
    private final static String TIMESTAMP = "flume.client.log4j.timestamp";

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static String CONFIG_TYPE = "type";
    private final static String DEFAULT_TYPE = "syslog";
    private volatile String type;

    @Override
    public Event getEvent(AvroFlumeEvent avroEvent) {

        Map<String, String> headers = toStringMap(avroEvent.getHeaders());

        String charsetName = headers.get(MESSAGE_ENCODING) == null ? CHARSET_NAME : headers.get(MESSAGE_ENCODING).toString();

        String body = new String(avroEvent.getBody().array(), Charset.forName(charsetName));
        Map<String, String> bodys = new HashMap<>();

        String uuid = UUID.randomUUID().toString();
        bodys.put("log_id",uuid);

        bodys.put("name", headers.get(LOGGER_NAME));

        String levelStr = Level.toLevel(Integer.valueOf(headers.get(LOG_LEVEL).toString())).toString();
        bodys.put("level", levelStr);

        // convert +8:00 timezone to utc(+0:00)
        long timestamp = Long.valueOf(headers.get(TIMESTAMP));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(timestamp));
        c.add(Calendar.HOUR, -8);
        bodys.put("create_time",sdf.format(c.getTime()));

        bodys.put("message", body);

        // clean headers
        headers.clear();
        headers.put(CONFIG_TYPE, type);

        // pack event
        JSONEvent jsonEvent = new JSONEvent();
        jsonEvent.setBody(bodys);


        return EventBuilder.withBody(jsonEvent.getBody(), headers);

    }

    @Override
    public void configure(Context context) {
        type = context.getString(CONFIG_TYPE, DEFAULT_TYPE);
    }

}
