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
    
    /**
     * Create an Sse Event from a String
     * @param eventString
     */
    protected SseEvent(String eventString) {
        this.eventString  = eventString;
    }

    /**
     *  Get the SSE event as a string
     * @return
     */
    protected String getString() {
        return eventString;
    }
    
    /**
     * Get the SSE Event Builder
     * @return
     */
    public static final SseEventBuilder getBuilder(){
        return new SseEventBuilder();
    }
    
}
