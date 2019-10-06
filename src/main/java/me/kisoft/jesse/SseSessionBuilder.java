/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import javax.servlet.AsyncContext;

/**
 *
 * @author tareq
 */
public class SseSessionBuilder {

    /**
     * Build a session without keepalive
     * @param asyncContext
     * @param manager
     * @return
     */
    protected static SseSession buildSession(AsyncContext asyncContext, SseSessionManager manager) {
        return new SseSession(manager,asyncContext);
    }

    /**
     * Build a session with a keep alive param
     * @param asyncContext
     * @param manager
     * @param keepAlive
     * @return
     */
    protected static SseSession buildSession(AsyncContext asyncContext, SseSessionManager manager, boolean keepAlive) {
        return new SseSession(manager,asyncContext,keepAlive);
    }
}
