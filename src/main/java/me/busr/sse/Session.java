/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
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
        asyncContext.setTimeout(-1);
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
        try {
            sessionManager.onClose(Session.this);
        } catch (WebApplicationException ex) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.sendError(ex.getResponse().getStatus());
            } catch (IOException ex1) {
                LOG.severe(ex1.getMessage());
            }
        } finally {
            this.asyncContext.complete();
        }

    }

    private void openSession() {
        try {
            sessionManager.onOpen(Session.this);
        } catch (WebApplicationException ex) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.sendError(ex.getResponse().getStatus());
            } catch (IOException ex1) {
                LOG.severe(ex1.getMessage());
            } finally {
                closeSession();
            }
        }
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.asyncContext);
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
        final Session other = (Session) obj;
        if (!Objects.equals(this.asyncContext, other.asyncContext)) {
            return false;
        }
        return true;
    }
    

}