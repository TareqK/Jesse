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
import javax.servlet.ServletResponse;
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

   private SseSessionManager manager = new DefaultSessionManager();
    private String domain = "*";
    private boolean keepAlive = false;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        HttpServletResponse asyncContextResponse = (HttpServletResponse) asyncContext.getResponse();
        asyncContextResponse.addHeader("Access-Control-Allow-Origin", domain);
        asyncContextResponse.addHeader("Access-Control-Expose-Headers", "*");
        asyncContextResponse.addHeader("Access-Control-Allow-Credentials", "true");
        EXECUTOR.submit(() -> {
            SseSessionBuilder.buildSession(asyncContext, manager, keepAlive);

        });
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String sessionManagerClassName = getServletConfig().getInitParameter("me.busr.jesse.session.manager");
        String domainInitParameter = getServletConfig().getInitParameter("me.busr.jesse.session.domains");
        String keepAliveParameter = getServletConfig().getInitParameter("me.busr.jesse.session.keepalive");
        if (domainInitParameter != null) {
            this.domain = domainInitParameter;
        }
        if(keepAliveParameter!=null && keepAliveParameter.equals("true")){
            SseSessionKeepAlive.start();
            this.keepAlive=true;
        }
        try {
            Class<?> sessionManagerClass = Class.forName(sessionManagerClassName);
            SseSessionManager sessionManager = (SseSessionManager) sessionManagerClass.newInstance();
            this.manager = sessionManager;
            LOG.info("using ".concat(sessionManagerClass.getCanonicalName()).concat(" as session manager"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
            LOG.warning(ex.getMessage().concat(" defaulting to built in session manager"));
        }
    }
    
}
