/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.Objects;
import javax.ws.rs.core.MediaType;
import me.kisoft.jesse.feature.MapperFeatureRegistry;

/**
 *
 * @author tareq
 */
public class SseEvent {

    private Object data;
    private String event;
    private String id;
    private String retry;
    private MediaType mediaType;

    /**
     * Protected construtor for SseEvent.
     */
    protected SseEvent() {

    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Get the SSE event as a string
     *
     * @return the data of the event as a string
     */
    protected String getEventString() {

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
            builder.append("data: ")
                    .append(MapperFeatureRegistry.getInstance()
                            .get(this.mediaType)
                            .serialize(this.data));

        }
        builder.append("\n\n");
        return builder.toString();
    }

    /**
     * Get the SSE Event Builder
     *
     * @return an SSE Ecent Builder
     */
    public static final SseEventBuilder getBuilder() {
        return new SseEventBuilder();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SseEvent other = (SseEvent) obj;
        if (!Objects.equals(this.getEventString(), other.getEventString())) {
            return false;
        }
        return true;
    }

}
