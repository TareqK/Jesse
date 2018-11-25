/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse.feature;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tareq
 */
public interface MapperFeature {
    
    public String serialize(Object object) throws WebApplicationException;

    public String getMediaTypeString();

    public MediaType getMediaType();
}
