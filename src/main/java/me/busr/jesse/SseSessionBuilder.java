/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

import javax.servlet.AsyncContext;

/**
 *
 * @author tareq
 */
public class SseSessionBuilder {

    protected static SseSession buildSession(AsyncContext asyncContext, SseSessionManager manager) {
        return new SseSession(manager,asyncContext);
    }
}
