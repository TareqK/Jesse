/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse.feature;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tareq
 */
public interface MapperFeature {

  /**
   * Convert an object to a string format to send
   *
   * @param object the object to convert
   * @return The object as a string
   * @throws WebApplicationException on serialization error
   */
  public String serialize(Object object) throws WebApplicationException;

  /**
   * get the media type string of this mapper
   *
   * @return the media type of the mapper
   */
  public String getMediaTypeString();

  /**
   * get the media tupe of this mapper
   *
   * @return the media type of the mapper
   */
  public MediaType getMediaType();
}
