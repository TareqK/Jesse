/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tareq
 */
public class EventStreamServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(EventStreamServlet.class.getName());

    SessionManager manager = new DefaultSessionManager();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        new Session(manager,request.startAsync());
    }

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        String sessionManagerClassName = getServletConfig().getInitParameter("me.busr.sse.session.manager");
        try {
            Class<?> sessionManagerClass = Class.forName(sessionManagerClassName);
            SessionManager sessionManager = (SessionManager)sessionManagerClass.newInstance();
            manager = sessionManager;
            LOG.info("using ".concat(sessionManagerClass.getCanonicalName()).concat(" as session manager"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
            LOG.warning(ex.getMessage().concat(" defaulting to built in session manager"));
        }
    }
}
