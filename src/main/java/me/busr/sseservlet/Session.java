/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sseservlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tareq
 */
public class Session {

    private static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);
    private final AsyncContext asyncContext;

    private final ReentrantLock LOCK;
    private static final Logger LOG = Logger.getLogger(Session.class.getName());

    private SessionManager sessionManager;

    Session(SessionManager sessionManager, AsyncContext asyncContext) {
        this.sessionManager = sessionManager;
        this.LOCK = new ReentrantLock();
        this.asyncContext = asyncContext;
        asyncContext.setTimeout(-33);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        openSession();
    }

    public void pushEvent(Event event) {
        EXECUTOR.submit(() -> {
            LOCK.lock();
            try {
                ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
                outputStream.print(event.getString());
                outputStream.flush();
            } catch (IOException | IllegalStateException ex) {
                closeSession();
            } finally {
                LOCK.unlock();
            }
        });
    }

    public void closeSession() {
        sessionManager.onClose(Session.this);
        this.asyncContext.complete();

    }

    private void openSession() {
        sessionManager.onOpen(Session.this);
    }

    protected Session(AsyncContext asyncContext) {
        this.LOCK = new ReentrantLock();
        asyncContext.setTimeout(-33);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        this.asyncContext = asyncContext;
        openSession();
    }

    public String getHeader(String name) {
        HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
        return r.getHeader(name);
    }

    public Cookie[] getCookies() {
        HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
        return r.getCookies();
    }
}
