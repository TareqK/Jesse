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
public class PlainTextMapperFeature implements MapperFeature{

    @Override
    public String serialize(Object object) throws WebApplicationException{
        return String.valueOf(object);
    }

    @Override
    public String getMediaTypeString() {
        return MediaType.TEXT_PLAIN;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.TEXT_PLAIN_TYPE;
    }
    
}
