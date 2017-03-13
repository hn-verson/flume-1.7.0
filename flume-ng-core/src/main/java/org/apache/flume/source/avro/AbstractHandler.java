package org.apache.flume.source.avro;

import org.apache.flume.Context;
import org.apache.flume.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by verson on 2017/3/11.
 */
public class AbstractHandler implements Handler {

    @Override
    public Event getEvent(AvroFlumeEvent avroEvent) {
        return null;
    }

    /**
     * Helper function to convert a map of CharSequence to a map of String.
     */
    public static Map<String, String> toStringMap(
            Map<CharSequence, CharSequence> charSeqMap) {
        Map<String, String> stringMap =
                new HashMap<String, String>();
        for (Map.Entry<CharSequence, CharSequence> entry : charSeqMap.entrySet()) {
            stringMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return stringMap;
    }

    @Override
    public void configure(Context context){}

}
