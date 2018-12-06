[![Maven Central](https://img.shields.io/maven-central/v/me.busr/jesse.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22me.busr%22%20AND%20a:%22jesse%22)
# Jesse

Jesse stands for **J**ava **E**nterprise **S**erver **S**ide **E**vents. It is a framework built using JEE APIs to provide server-side event capability in a JEE web application.

# Getting Started

## Adding It to your project

Add This to your dependencies : 

```xml

    <dependency>
        <groupId>me.busr</groupId>
        <artifactId>jesse</artifactId>
    </dependency>

```
## Setup
You need to add some entries into your web.xml 

```xml

    <servlet>
        <servlet-name>{Servlet Name}</servlet-name>
        <servlet-class>me.busr.jesse.JesseServlet</servlet-class >
        <load-on-startup>1</load-on-startup>
        <async-supported>true</async-supported>
    </servlet>
    <servlet-mapping>
        <servlet-name>{Servlet Name}</servlet-name>
        <url-pattern>{URI mapping}</url-pattern>
    </servlet-mapping>

```

If you would like to use your own implementation of the SseSessionManager, then you need to add the following init params to your servlet defenition

```xml

    <init-param >
        <param-name>me.busr.jesse.session.manager</param-name >
        <param-value>{Class name of session manager}</param-value >
    </init-param >

```

If you would like to use regular keep-alive, then you have to add this init parameter

```xml

    <init-param >
        <param-name>me.busr.jesse.session.keepalive.enabled</param-name >
        <param-value>{true/false}</param-value >
    </init-param >

```

You can also set the interval for the Keep-Alive, by using this paramter

```xml

    <init-param >
        <param-name>me.busr.jesse.session.keepalive.interval</param-name >
        <param-value>{interval in secconds}</param-value >
    </init-param >

```

You can also specify the domains that are allowed to access the event stream, by setting this parameter. Otherwise, it defaults to the domain that made the request(effectively all domains)


```xml

    <init-param >
        <param-name>me.busr.jesse.session.domains</param-name >
        <param-value>{comma,separated,domain,names}</param-value >
    </init-param >

```

Aditionally, you can add mappers for MediaTypes, by adding this to the web.xml

```xml

    <init-param>
        <param-name>me.busr.jesse.feature</param-name>
        <param-value>{comma,seperated,features,class,names}</param-value>
    </init-param>
    
```
You can add your own custom mappers by implementing the ```MapperFeature``` interface.

For example,this is a complete configuration with a custom session manager and the Jackson Mapper

```xml

    <servlet >
        <servlet-name> EventStream Endpoint </servlet-name>
        <servlet-class>me.busr.jesse.JesseServlet</servlet-class >
        <init-param>
            <param-name >me.busr.jesse.session.manager</param-name >
            <param-value>me.busr.core.sse.BusrSessionManager</param-value >
        </init-param>
        <init-param >
            <param-name>me.busr.jesse.session.keepalive.enabled</param-name >
            <param-value>true</param-value >
         </init-param >
         <init-param>
            <param-name>me.busr.jesse.feature</param-name>
            <param-value>me.busr.jesse.feature.JacksonMapperFeature</param-value>
        </init-param>
         <load-on-startup>1</load-on-startup>
         <async-supported>true</async-supported>
    </servlet>
    <servlet-mapping>
        <servlet-name> EventStream Endpoint </servlet-name>
        <url-pattern>/event/*</url-pattern>
    </servlet-mapping>
    
```

And Thats it! you are now ready to go

# Usage

## Sending Sse Events

```java

 DefaultSessionManager.broadcastAll(SseEvent.getBuilder()
                            .event("test")
                            .id(33)
                            .retry(500)
                            .mediaType(MediaType.APPLICATION_JSON)
                            .data(notificationData)
                            .build())

```

Lets break this code down 

``` DefaultSessionManager ``` is the default implementation of the session manager, which is used by default if no custom SessionManager is provided. 
It stores all active sessions in a list, and you can broadcast events to groups of sessions, individual sessions, or all sessions. 


``` SseEventBuilder ``` is a utility class to build a new ``` SseEvent ```. ```event("test")``` sets the type of the event to "test", ```mediaType(MediaType.APPLICATION_JSON)``` sets the media type to JSON,  ``` id(33) ``` sets the event id to "33",  ``` retry(500) ``` sets the retry interval to 500ms, ``` data(notificationData) ``` sends notification as the event data, and ``` build() ``` creates the ``` SseEvent ``` based on the previous functions. The resulting event would be

```
id: 33
event: test
retry: 500
data:  {"parameter1":"value1","parameter2":"value2",.....,"parametern":"valuen"}

```

## Custom Session Managers

a custom session manager can be created by extending the ``` SseSessionManager ``` class.
By default, the sessions are not stored anywhere, so it is up to you to store them in whatever way you see fit.

The following methods are mandatory to implement

``` onOpen(SseSession sseSession) throws WebApplicationExceptoion ``` : This method is called when a new session is opened. ``` WebApplicationException ``` will terminate this session with the corrseponding Http Response code.

``` onClose(SseSession sseSession) throws WebApplicationExceptoion ``` : This method is called when a session is being closed. ``` WebApplicationException ``` will terminate this session with the corrseponding Http Response code.

``` onError(SseSession sseSession)``` : This method is called if there is a transport error(The session has been closed for example, or some internal server error). I reccomend you use this to remove the session from your sessions list/map/whatever you are storing the sessions in.


## Custom Feature Mapping

a custom feature mapper can be created by implementing the ```MapperFeature``` interface. As a note, feature registration is global for Jesse, meaning that all your SseSessions will use the same mapper modules. You need to
implement these functions 

``` serialize(Object object) throws WebApplicationException``` converts the Object into a String form for this mapper feature.

``` getMediaTypeString() ``` returns the String representation of the ```MediaType``` that the Mapper will handle.

``` getMediaType() ``` returns the JAX-RS MediaType representation that the Mapper will handle.

For now, MapperFeatures are restricted to handling the JAX-RS MediaTypes only, but that might change in the future.

# Building

Nothing fancy, just clone and build using the pom.xml file. I havent gotten around to putting in test classes yet though.
