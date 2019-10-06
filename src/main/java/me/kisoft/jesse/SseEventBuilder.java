/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.HashMap;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import me.kisoft.jesse.feature.MapperFeature;
import me.kisoft.jesse.feature.PlainTextMapperFeature;

/**
 *
 * @author tareq
 */
public class SseEventBuilder {

  private Object data;
  private String event;
  private String id;
  private String retry;
  private MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
  private static final HashMap<MediaType, MapperFeature> MAPPER_MAP = new HashMap();

  static {
    MAPPER_MAP.put(MediaType.TEXT_PLAIN_TYPE, new PlainTextMapperFeature());
  }

  /**
   * Creates a new event builder
   */
  protected SseEventBuilder() {

  }

  /**
   * Adds data to the event
   *
   * @param data the data of the event
   * @return the sse event builder
   */
  public SseEventBuilder data(Object data) {
    this.data = data;
    return this;
  }

  /**
   * Adds the event type
   *
   * @param event the type/name of the event
   * @return the sse event builder
   */
  public SseEventBuilder event(String event) {
    this.event = String.valueOf(event);
    return this;
  }

  /**
   * Adds the event id
   *
   * @param id the Id of the event
   * @return the sse event builder
   */
  public SseEventBuilder id(Object id) {
    this.id = String.valueOf(id);
    return this;
  }

  /**
   * Adds the retry interval
   *
   * @param retry the retry interval for the SSE event, in milliseconds
   * @return the sse event builder
   */
  public SseEventBuilder retry(long retry) {
    this.retry = String.valueOf(retry);
    return this;
  }

  /**
   * The media type of this event
   *
   * @param mediaType the media type of the event
   * @return the sse event builder
   */
  public SseEventBuilder mediaType(String mediaType) {
    this.mediaType = MediaType.valueOf(mediaType);
    return this;
  }

  /**
   * Builds the event in the RFC specified event format.
   *
   * @return an SSE event containing the string of the event
   */
  public SseEvent build() throws WebApplicationException {

    StringBuilder builder = new StringBuilder();
    if (this.id != null) {
      builder.append("id: ").append(id).append("\n");
    }
    if (this.event != null) {
      builder.append("event: ").append(event).append("\n");
    }
    if (this.retry != null) {
      builder.append("retry: ").append(retry).append("\n");
    }
    if (this.data != null) {
      try {
        builder.append("data: ").append(MAPPER_MAP.getOrDefault(this.mediaType, new PlainTextMapperFeature()).serialize(this.data));
      } catch (NullPointerException ex) {
        throw new WebApplicationException(
         builder.append("Error : No serializer for media type ")
          .append(this.mediaType.getType())
          .append(" was found").toString(), 400);
      }
    }
    builder.append("\n\n");
    return new SseEvent(builder.toString());
  }

  /**
   * Adds a new feature mapper to be used to build events
   *
   * @param mapper the mapper to add
   */
  protected static final void addMapper(MapperFeature mapper) {
    MAPPER_MAP.put(mapper.getMediaType(), mapper);
  }

}
