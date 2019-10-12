/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import javax.ws.rs.core.MediaType;

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

    /**
     * Creates a new event builder
     */
    protected SseEventBuilder() {

    }

    /**
     * Adds data to the event
     *
     * @param eventData the data of the event
     * @return the sse event builder
     */
    public SseEventBuilder data(Object eventData) {
        this.data = eventData;
        return this;
    }

    /**
     * Adds the event type
     *
     * @param eventName the type/name of the event
     * @return the sse event builder
     */
    public SseEventBuilder event(String eventName) {
        this.event = String.valueOf(eventName);
        return this;
    }

    /**
     * Adds the event id
     *
     * @param eventId the Id of the event
     * @return the sse event builder
     */
    public SseEventBuilder id(Object eventId) {
        this.id = String.valueOf(eventId);
        return this;
    }

    /**
     * Adds the retry interval
     *
     * @param eventRetryInterval the retry interval for the SSE event, in
     * milliseconds
     * @return the sse event builder
     */
    public SseEventBuilder retry(long eventRetryInterval) {
        this.retry = String.valueOf(eventRetryInterval);
        return this;
    }

    /**
     * The media type of this event
     *
     * @param eventMediaType the media type of the event
     * @return the sse event builder
     */
    public SseEventBuilder mediaType(String eventMediaType) {
        this.mediaType = MediaType.valueOf(eventMediaType);
        return this;
    }

    /**
     * Builds the SSe Event
     *
     * @return the built SSe event
     */
    public SseEvent build() {
        SseEvent built = new SseEvent();
        built.setData(data);
        built.setEvent(event);
        built.setId(id);
        built.setMediaType(mediaType);
        built.setRetry(retry);
        return built;
    }

}
