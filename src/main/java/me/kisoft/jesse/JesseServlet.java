/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.kisoft.jesse.feature.MapperFeature;
import me.kisoft.jesse.feature.MapperFeatureRegistry;

/**
 *
 * @author tareq
 */
public class JesseServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(JesseServlet.class.getName());
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
  private static final String SESSION_MANAGER_PROPERTY = "me.kisoft.jesse.session.manager";
  private static final String DOMAINS_PROPERTY = "me.kisoft.jesse.session.domains";
  private static final String KEEPALIVE_ENABLED_PROPERTY = "me.kisoft.jesse.session.keepalive.enabled";
  private static final String KEEPALIVE_INTERVAL_PROPERTY = "me.kisoft.jesse.session.keepalive.interval";
  private static final String FEATURES_PROPERTY = "me.kisoft.jesse.feature";
  private SseSessionManager manager;
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
      SseSessionFactory.getInstance().createSession(asyncContext, manager, keepAlive);

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
    LOG.log(Level.INFO, "Jesse Servlet Starting at context {0}", config.getServletContext().getContextPath());
    String sessionManagerClassNameParameter = getServletConfig().getInitParameter(SESSION_MANAGER_PROPERTY);
    String domainInitParameter = getServletConfig().getInitParameter(DOMAINS_PROPERTY);
    String keepAliveParameter = getServletConfig().getInitParameter(KEEPALIVE_ENABLED_PROPERTY);
    String keepAliveTimerParameter = getServletConfig().getInitParameter(KEEPALIVE_INTERVAL_PROPERTY);
    String mapperFeaturesParameter = getServletConfig().getInitParameter(FEATURES_PROPERTY);
    if (domainInitParameter != null) {
      this.domain = domainInitParameter;
    }
    if (keepAliveParameter != null && keepAliveParameter.equals("true")) {
      if (keepAliveTimerParameter != null) {
        try {
          long interval = Long.parseLong(keepAliveTimerParameter.trim());
          SseSessionKeepAlive.setInterval(interval);
          LOG.info("Set the Keep-Alive interval to ".concat(String.valueOf(interval)).concat(" seconds"));
        } catch (NumberFormatException ex) {
          LOG.severe(ex.getMessage());
          LOG.info("Defaulting to Keep-Alive interval of 120 seconds");
        }
      }
      SseSessionKeepAlive.start();
      this.keepAlive = true;
    }
    try {
      if (sessionManagerClassNameParameter != null && sessionManagerClassNameParameter.length() > 0) {
        Class<?> sessionManagerClass = Class.forName(sessionManagerClassNameParameter);
        SseSessionManager sessionManager = (SseSessionManager) sessionManagerClass.newInstance();
        this.manager = sessionManager;
        LOG.info("using ".concat(sessionManagerClass.getCanonicalName()).concat(" as session manager"));
      } else {
        LOG.info(" defaulting to built in session manager");
        this.manager = new DefaultSessionManager();
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
      this.manager = new DefaultSessionManager();
      LOG.warning(ex.getMessage().concat(" defaulting to built in session manager"));
    }
    if (mapperFeaturesParameter != null) {
      if (mapperFeaturesParameter.contains(",")) {
        String[] features = mapperFeaturesParameter.split(",");
        for (String feature : features) {
          addFeature(feature);
        }
      } else {
        addFeature(mapperFeaturesParameter);
      }
    }
  }

  /**
   * Add a mapper feature by class name.
   *
   * @param featureClassName the name of the feature to add
   */
  public void addFeature(String featureClassName) {
    Class<?> mapperFeatureClass;
    try {
      mapperFeatureClass = Class.forName(featureClassName);
      MapperFeature mapperFeature = (MapperFeature) mapperFeatureClass.newInstance();
      MapperFeatureRegistry.getInstance().register(mapperFeature);
      LOG.info("using ".concat(mapperFeatureClass.getCanonicalName()));
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NullPointerException ex) {
      LOG.warning(ex.getMessage());
    }
  }
}
