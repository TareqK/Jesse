/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

/**
 *
 * @author tareq
 */
public class SseEventBuilder {
    
    private Object data;
    private String event;
    private String id;
    private String retry;
    
    /**
     * Creates a new event builder
     */
    public SseEventBuilder(){
        
    }
    
    /**
     * Adds data to the event
     * @param data
     * @return
     */
    public SseEventBuilder data(String data){
        this.data = data;
        return this;
    }
    
    /**
     * Adds the event type
     * @param event
     * @return
     */
    public SseEventBuilder event(String event){
        this.event = String.valueOf(event);
        return this;
    }
    
    /**
     * Adds the event id
     * @param id
     * @return
     */
    public SseEventBuilder id(Object id){
        this.id = String.valueOf(id);
        return this;
    }
    
    /**
     * Adds the retry interval
     * @param retry
     * @return
     */
    public SseEventBuilder retry(long retry){
        this.retry = String.valueOf(retry);
        return this;
    }
    
    /**
     * Builds the event
     * @return
     */
    public SseEvent build(){
        StringBuilder builder = new StringBuilder();
        if(this.id != null){
            builder.append("id: ").append(id).append("\n");
        }
        if(this.event != null){
            builder.append("event: ").append(event).append("\n");
        }
        if(this.retry != null){
            builder.append("retry: ").append(retry).append("\n");
        }
        if(this.data != null){
            builder.append("data: ").append(data);
        }
        builder.append("\n\n");
        return new SseEvent(builder.toString());
    }
}
