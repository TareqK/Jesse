/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

/**
 *
 * @author tareq
 */
public class SseEvent {

    private final String eventString;
    
    protected SseEvent(String eventString) {
        this.eventString  = eventString;
    }

    protected String getString() {
        return eventString;
    }
    
    public static final SseEventBuilder getBuilder(){
        return new SseEventBuilder();
    }
    
}
