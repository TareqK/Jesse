/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sseservlet;

/**
 *
 * @author tareq
 */
public class Configuration {
    
    public static void setSessionManager(SessionManager sessionManager){
        Session.setManager(sessionManager);
    }
}
