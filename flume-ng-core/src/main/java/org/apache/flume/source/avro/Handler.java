package org.apache.flume.source.avro;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

/**
 * Created by verson on 2017/3/11.
 */
public interface Handler extends Configurable {

    Event getEvent(AvroFlumeEvent avroEvent);

}
