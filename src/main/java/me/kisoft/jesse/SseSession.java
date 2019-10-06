/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

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

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private final AsyncContext asyncContext;

    private final ReentrantLock lock;
    private static final Logger LOG = Logger.getLogger(SseSession.class.getName());

    private final SseSessionManager sessionManager;
    private final boolean keepAlive;

    /**
     *
     * @param sessionManager
     * @param asyncContext
     */
    protected SseSession(SseSessionManager sessionManager, AsyncContext asyncContext) {
        this.sessionManager = sessionManager;
        this.lock = new ReentrantLock();
        this.asyncContext = asyncContext;
        asyncContext.setTimeout(-1);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        this.keepAlive = false;
        openSession();
    }

    /**
     *
     * @param sessionManager
     * @param asyncContext
     * @param keepAlive
     */
    protected SseSession(SseSessionManager sessionManager, AsyncContext asyncContext, boolean keepAlive) {
        this.sessionManager = sessionManager;
        this.lock = new ReentrantLock();
        this.asyncContext = asyncContext;
        asyncContext.setTimeout(-1);
        asyncContext.getResponse().setContentType(MediaType.SERVER_SENT_EVENTS);
        asyncContext.getResponse().setCharacterEncoding("UTF-8");
        this.keepAlive = keepAlive;
        openSession();
    }

    private void removeKeepAlive() {
        if (this.keepAlive == true) {
            SseSessionKeepAlive.removeSession(this);
        }
    }

    private void addKeepAlive() {
        if (this.keepAlive == true) {
            SseSessionKeepAlive.addSession(this);
        }
    }

    /**
     * Pushes an event to this SseSession
     *
     * @param event
     */
    public void pushEvent(SseEvent event) {
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if (event != null) {
                        ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
                        outputStream.print(event.getString());
                        outputStream.flush();
                    }
                } catch (IOException ex) {
                    closeSession();
                } catch (NullPointerException | IllegalStateException ex) {
                    sessionError(ex);
                } catch (Throwable ex) {
                    LOG.severe(ex.getMessage());
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    private void sessionError(Throwable ex) {
        LOG.severe(ex.getMessage());
        removeKeepAlive();
        this.asyncContext.complete();
        sessionManager.onError(this);

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
            }
        } finally {
            removeKeepAlive();
            this.asyncContext.complete();
        }

    }

    private void openSession() {
        try {
            sessionManager.onOpen(this);
            addKeepAlive();
        } catch (WebApplicationException ex) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.sendError(ex.getResponse().getStatus());
                response.flushBuffer();
            } catch (IOException | ClassCastException | NullPointerException | IllegalStateException ex1) {

            } finally {
                removeKeepAlive();
                this.asyncContext.complete();
            }
        }
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
        return this.asyncContext.equals(other.asyncContext);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "SseSession{" + "asyncContext=" + asyncContext + '}';
    }

}
