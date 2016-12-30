/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flume.interceptor;

import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import static org.apache.flume.interceptor.TypeInterceptor.Constants.*;

/**
 * Type interceptor,require request must contains specific header
 */
public class TypeInterceptor implements Interceptor {

  private static final Logger logger = LoggerFactory
          .getLogger(TypeInterceptor.class);

  private final boolean preserveExisting;
  private final String header;

  /**
   * Only {@link TypeInterceptor.Builder} can build me
   */
  private TypeInterceptor(boolean preserveExisting, String header) {
    this.preserveExisting = preserveExisting;
    this.header = header;
  }

  @Override
  public void initialize() {
    // no-op
  }

  /**
   * Filter events in-place.
   */
  @Override
  public Event intercept(Event event) {
    Map<String, String> headers = event.getHeaders();

    if (preserveExisting && headers.containsKey(header)) {
      return event;
    } else if(preserveExisting && !headers.containsKey(header)) {
      logger.warn("Unknown " + header + ": " + event.toString());
    }

    return null;
  }

  /**
   * Delegates to {@link #intercept(Event)} in a loop.
   * @param events
   * @return
   */
  @Override
  public List<Event> intercept(List<Event> events) {
    List<Event> newEvents = Lists.newArrayList();
    for (Event event : events) {
      Event e = intercept(event);
      if (e != null) {
        newEvents.add(e);
      }
    }
    return newEvents;
  }

  @Override
  public void close() {
    // no-op
  }

  /**
   * Builder which builds new instances of the HostInterceptor.
   */
  public static class Builder implements Interceptor.Builder {

    private boolean preserveExisting = PRESERVE_DFLT;
    private String header = TYPE;

    @Override
    public Interceptor build() {
      return new TypeInterceptor(preserveExisting, header);
    }

    @Override
    public void configure(Context context) {
      preserveExisting = context.getBoolean(PRESERVE, PRESERVE_DFLT);
      header = context.getString(TYPE_HEADER, TYPE);
    }

  }

  public static class Constants {
    public static String TYPE = "type";

    public static String PRESERVE = "preserveExisting";
    public static boolean PRESERVE_DFLT = true;

    public static String TYPE_HEADER = "typeHeader";
  }

}
