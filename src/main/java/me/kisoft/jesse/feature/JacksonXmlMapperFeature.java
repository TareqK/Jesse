/*
 * Copyright 2019 tareq.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.kisoft.jesse.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tareq
 */
public class JacksonXmlMapperFeature implements MapperFeature {

  private final XmlMapper mapper;

  /**
   * Create a new JacksonXmlMapperFeature with the default mapper
   */
  public JacksonXmlMapperFeature() {
    this.mapper = new XmlMapper();
  }

  /**
   * Create a new JacksonXmlMapperFeature with a provided XmlMapper;
   *
   * @param myMapper the custom mapper you want to serialize with.
   */
  public JacksonXmlMapperFeature(XmlMapper myMapper) {
    this.mapper = myMapper;
  }

  @Override
  public String serialize(Object object) throws WebApplicationException {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException ex) {
      throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
    }
  }

  @Override
  public String getMediaTypeString() {
    return MediaType.APPLICATION_XML;
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.APPLICATION_XML_TYPE;
  }

}
