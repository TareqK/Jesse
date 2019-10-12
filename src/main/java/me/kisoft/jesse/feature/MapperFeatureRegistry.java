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

import java.util.HashMap;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tareq
 */
public class MapperFeatureRegistry {

  private static MapperFeatureRegistry instance = getInstance();
  private final HashMap<MediaType, MapperFeature> mappersMap = new HashMap();

  /**
   * Get the current instance of the mapper registry
   *
   * @return the current mapper registry instance
   */
  public static final MapperFeatureRegistry getInstance() {
    if (instance == null) {
      instance = new MapperFeatureRegistry();
    }
    return instance;
  }

  /**
   * Registers a plain text mapper feature
   */
  private MapperFeatureRegistry() {
    this.register(new PlainTextMapperFeature());
  }

  /**
   * Gets the mapper for a media type
   *
   * @param mediaType the media type to get the mapper for
   * @return the mapper for that media type, if found, the plain text mapper otherwise
   */
  public final MapperFeature get(MediaType mediaType) {
    return mappersMap.getOrDefault(mediaType, mappersMap.get(MediaType.TEXT_PLAIN_TYPE));
  }

  /**
   * Register a new MapperFeature
   *
   * @param mapperFeature the mapper feature to register
   */
  public final void register(MapperFeature mapperFeature) {
    mappersMap.put(mapperFeature.getMediaType(), mapperFeature);
  }

  /**
   * Removes a mapper feature from the registry
   *
   * @param mapperFeature the mapper feature to remove
   */
  public final void unregister(MapperFeature mapperFeature) {
    unregister(mapperFeature.getMediaType());
  }

  /**
   * removes a mapper feature from the registry by media type
   *
   * @param mediaType the media type whose mapper we are removing
   */
  public final void unregister(MediaType mediaType) {
    mappersMap.remove(mediaType);
  }
}
