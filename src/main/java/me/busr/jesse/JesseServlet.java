/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tareq
 */
public class JesseServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(JesseServlet.class.getName());
    private static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);

    SseSessionManager manager = new DefaultSessionManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        EXECUTOR.submit(() -> {
            SseSessionBuilder.buildSession(asyncContext, manager);
        });
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String sessionManagerClassName = getServletConfig().getInitParameter("me.busr.jesse.session.manager");
        try {
            Class<?> sessionManagerClass = Class.forName(sessionManagerClassName);
            SseSessionManager sessionManager = (SseSessionManager) sessionManagerClass.newInstance();
            manager = sessionManager;
            LOG.info("using ".concat(sessionManagerClass.getCanonicalName()).concat(" as session manager"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
            LOG.warning(ex.getMessage().concat(" defaulting to built in session manager"));
        }
    }
}
