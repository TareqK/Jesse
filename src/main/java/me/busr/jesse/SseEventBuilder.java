/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

import java.util.HashMap;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import me.busr.jesse.feature.PlainTextMapperFeature;
import me.busr.jesse.feature.MapperFeature;

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
     * @param data
     * @return
     */
    public SseEventBuilder data(Object data) {
        this.data = data;
        return this;
    }

    /**
     * Adds the event type
     *
     * @param event
     * @return
     */
    public SseEventBuilder event(String event) {
        this.event = String.valueOf(event);
        return this;
    }

    /**
     * Adds the event id
     *
     * @param id
     * @return
     */
    public SseEventBuilder id(Object id) {
        this.id = String.valueOf(id);
        return this;
    }

    /**
     * Adds the retry interval
     *
     * @param retry
     * @return
     */
    public SseEventBuilder retry(long retry) {
        this.retry = String.valueOf(retry);
        return this;
    }

    public SseEventBuilder mediaType(String mediaType) {
        this.mediaType = MediaType.valueOf(mediaType);
        return this;
    }

    /**
     * Builds the event
     *
     * @return
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

    protected static final void addMapper(MapperFeature mapper) {
        MAPPER_MAP.put(mapper.getMediaType(), mapper);
    }

    private String writeAsString(Object data, MediaType mediaType) throws WebApplicationException {
        MapperFeature mapper = MAPPER_MAP.getOrDefault(mediaType, new PlainTextMapperFeature());
        return mapper.serialize(data);
    }
}
