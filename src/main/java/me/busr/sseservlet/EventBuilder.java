/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sseservlet;

/**
 *
 * @author tareq
 */
public class EventBuilder {
    
    private Object data;
    private String event;
    private String id;
    private String retry;
    
    public EventBuilder(){
        
    }
    
    public EventBuilder data(String data){
        this.data = data;
        return this;
    }
    
    public EventBuilder event(String event){
        this.event = String.valueOf(event);
        return this;
    }
    
    public EventBuilder id(Object id){
        this.id = String.valueOf(id);
        return this;
    }
    
    public EventBuilder retry(long retry){
        this.retry = String.valueOf(retry);
        return this;
    }
    
    
    public Event build(){
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
        return new Event(builder.toString());
    }
}
