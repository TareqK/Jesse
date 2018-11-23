/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

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
import javax.ws.rs.core.Response;

/**
 *
 * @author tareq
 */
public class SseSession {

    private static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);
    private final AsyncContext asyncContext;

    private final ReentrantLock LOCK;
    private static final Logger LOG = Logger.getLogger(SseSession.class.getName());

    private SseSessionManager sessionManager;

    protected SseSession(SseSessionManager sessionManager, AsyncContext asyncContext) {
        this.sessionManager = sessionManager;
        this.LOCK = new ReentrantLock();
        this.asyncContext = asyncContext;
        asyncContext.setTimeout(-1);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        openSession();
    }

    /**
     * Pushes an event to this SseSession
     *
     * @param event
     */
    public void pushEvent(SseEvent event) {
        EXECUTOR.submit(() -> {
            LOCK.lock();
            try {
                ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
                outputStream.print(event.getString());
                outputStream.flush();
            } catch (IOException | NullPointerException | IllegalStateException ex) {
                sessionManager.onError(this);
                closeSession();
            } finally {
                LOCK.unlock();
            }
        });
    }

    /**
     * Closes this sseSession
     */
    public void closeSession() {
        try {
            sessionManager.onClose(this);
        } catch (WebApplicationException ex) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.sendError(ex.getResponse().getStatus());
                response.flushBuffer();
            } catch (IOException | ClassCastException | NullPointerException | IllegalStateException ex1) {
                LOG.severe(ex1.getMessage());
            }
        } finally {
            this.asyncContext.complete();
        }

    }

    private void openSession() {
        try {
            sessionManager.onOpen(this);
        } catch (WebApplicationException ex) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.sendError(ex.getResponse().getStatus());
                response.flushBuffer();
            } catch (IOException | ClassCastException | NullPointerException | IllegalStateException ex1) {
                LOG.severe(ex1.getMessage());
            } finally {
                this.asyncContext.complete();
            }
        }
    }

    /**
     * Create a new SseSession without a sessionManager
     *
     * @param asyncContext
     */
    protected SseSession(AsyncContext asyncContext) {
        this.LOCK = new ReentrantLock();
        asyncContext.setTimeout(-1);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        this.asyncContext = asyncContext;
        openSession();
    }

    /**
     * Get the SseSession cookies
     *
     * @return the Session Cookies
     */
    public Cookie[] getCookies() throws WebApplicationException {
        try {
            HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
            return r.getCookies();
        } catch (ClassCastException | NullPointerException | IllegalStateException ex) {
            LOG.severe(ex.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param cookieName the name of the cookie
     * @return the cookie, if found
     * @throws WebApplicationException if the cookie is not found
     */
    public Cookie getCookie(String cookieName) throws WebApplicationException {
        try {
            HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
            Cookie[] cookies = r.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        return cookie;
                    }
                }
            }
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } catch (ClassCastException | NullPointerException | IllegalStateException ex) {
            LOG.severe(ex.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param cookieName the cookie we are searching for
     * @return the value field of the cookie
     */
    public String getCookieValue(String cookieName) throws WebApplicationException {
        return getCookie(cookieName).getValue();
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.asyncContext);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
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
        final SseSession other = (SseSession) obj;
        if (!Objects.equals(this.asyncContext, other.asyncContext)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SseSession{" + "asyncContext=" + asyncContext + '}';
    }

}
