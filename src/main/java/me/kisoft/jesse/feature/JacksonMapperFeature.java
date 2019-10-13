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
public class JacksonMapperFeature implements MapperFeature {

  private final ObjectMapper mapper;

  /**
   * Create a new JacksonMapperFeature with the default mapper
   */
  public JacksonMapperFeature() {
    this.mapper = new ObjectMapper();
  }

  /**
   * Create a new JacksonMapperFeature with a provided ObjectMapper;
   *
   * @param myMapper the custom mapper you want to serialize with.
   */
  public JacksonMapperFeature(ObjectMapper myMapper) {
    this.mapper = myMapper;
  }

  @Override
  public String serialize(Object object) throws WebApplicationException {
    try {
      return mapper.writer().writeValueAsString(object);
    } catch (JsonProcessingException ex) {
      throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
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
