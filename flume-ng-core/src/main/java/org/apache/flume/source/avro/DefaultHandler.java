package org.apache.flume.source.avro;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by verson on 2017/3/11.
 */
public class DefaultHandler extends AbstractHandler implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);

    @Override
    public Event getEvent(AvroFlumeEvent avroEvent) {
        return EventBuilder.withBody(avroEvent.getBody().array(),
                toStringMap(avroEvent.getHeaders()));
    }

}
