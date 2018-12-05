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
    
    /**
     *
     * @param object
     * @return The object as a string
     * @throws WebApplicationException on serialization error
     */
    public String serialize(Object object) throws WebApplicationException;

    /**
     *
     * @return the media type of the mapper
     */
    public String getMediaTypeString();

    /**
     *
     * @return the mediay type of the mapper
     */
    public MediaType getMediaType();
}
