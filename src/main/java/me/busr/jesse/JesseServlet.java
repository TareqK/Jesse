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
import me.busr.jesse.feature.MapperFeature;

/**
 *
 * @author tareq
 */
public class JesseServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(JesseServlet.class.getName());
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private SseSessionManager manager = new DefaultSessionManager();
    private String domain = "*";
    private boolean keepAlive = false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        HttpServletResponse asyncContextResponse = (HttpServletResponse) asyncContext.getResponse();

        if (domain == null || domain.contains("*")) {
            asyncContextResponse.addHeader("Access-Control-Allow-Origin", request.getServerName());
        } else {
            asyncContextResponse.addHeader("Access-Control-Allow-Origin", domain);
        }
        asyncContextResponse.addHeader("Access-Control-Expose-Headers", "*");
        asyncContextResponse.addHeader("Access-Control-Allow-Credentials", "true");
        EXECUTOR.submit(() -> {
            SseSessionBuilder.buildSession(asyncContext, manager, keepAlive);

        });
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        HttpServletResponse asyncContextResponse = (HttpServletResponse) asyncContext.getResponse();

        if (domain == null || domain.contains("*")) {
            asyncContextResponse.addHeader("Access-Control-Allow-Origin", request.getServerName());
        } else {
            asyncContextResponse.addHeader("Access-Control-Allow-Origin", domain);
        }
        asyncContextResponse.addHeader("Access-Control-Expose-Headers", "*");
        asyncContextResponse.addHeader("Access-Control-Allow-Credentials", "true");
        asyncContextResponse.flushBuffer();
        asyncContext.complete();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String sessionManagerClassNameParameter = getServletConfig().getInitParameter("me.busr.jesse.session.manager");
        String domainInitParameter = getServletConfig().getInitParameter("me.busr.jesse.session.domains");
        String keepAliveParameter = getServletConfig().getInitParameter("me.busr.jesse.session.keepalive.enabled");
        String keepAliveTimerParameter = getServletConfig().getInitParameter("me.busr.jesse.session.keepalive.interval");
        String mapperFeaturesParameter = getServletConfig().getInitParameter("me.busr.jesse.feature");
        if (domainInitParameter != null) {
            this.domain = domainInitParameter;
        }
        if (keepAliveParameter != null && keepAliveParameter.equals("true")) {
            if (keepAliveTimerParameter != null) {
                try {
                    long interval = Long.parseLong(keepAliveTimerParameter.trim());
                    SseSessionKeepAlive.setInterval(interval);
                    LOG.info("Set the Keep-Alive interval to ".concat(String.valueOf(interval)).concat(" seconds"));
                } catch (Exception ex) {
                    LOG.severe(ex.getMessage());
                    LOG.info("Defaulting to Keep-Alive interval of 120 seconds");
                }
            }
            SseSessionKeepAlive.start();
            this.keepAlive = true;
        }
        try {
            Class<?> sessionManagerClass = Class.forName(sessionManagerClassNameParameter);
            SseSessionManager sessionManager = (SseSessionManager) sessionManagerClass.newInstance();
            this.manager = sessionManager;
            LOG.info("using ".concat(sessionManagerClass.getCanonicalName()).concat(" as session manager"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
            LOG.warning(ex.getMessage().concat(" defaulting to built in session manager"));
        }

        if (mapperFeaturesParameter != null) {
            if (mapperFeaturesParameter.contains(",")) {
                String[] features = mapperFeaturesParameter.split(",");
                for (String feature : features) {
                    addFeature(feature);
                }
            }
            else{
                addFeature(mapperFeaturesParameter);
            }
        }
    }

    /**
     * Add a mapper feature by class name.
     * @param featureClassName
     */
    public void addFeature(String featureClassName) {
        Class<?> mapperFeatureClass;
        try {
            mapperFeatureClass = Class.forName(featureClassName);
            MapperFeature mapperFeature = (MapperFeature) mapperFeatureClass.newInstance();
            SseEventBuilder.addMapper(mapperFeature);
            LOG.info("using ".concat(mapperFeatureClass.getCanonicalName()));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
            LOG.warning(ex.getMessage());
        }
    }
}
