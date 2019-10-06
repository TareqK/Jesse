/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tareq
 */
public class JacksonMapperFeature implements MapperFeature{

    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String serialize(Object object) throws WebApplicationException {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex.getMessage(),Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public String getMediaTypeString() {
        return MediaType.APPLICATION_JSON;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.APPLICATION_JSON_TYPE;
    }

}
