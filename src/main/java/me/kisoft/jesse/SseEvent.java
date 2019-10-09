/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

/**
 *
 * @author tareq
 */
public class SseEvent {

  private final String eventString;

  /**
   * Create an Sse Event from a String
   *
   * @param eventString the value of the event as a stirng
   */
  protected SseEvent(String eventString) {
    this.eventString = eventString;
  }

  /**
   * Get the SSE event as a string
   *
   * @return the data of the event as a string
   */
  protected String getString() {
    return eventString;
  }

  /**
   * Get the SSE Event Builder
   *
   * @return an SSE Ecent Builder
   */
  public static final SseEventBuilder getBuilder() {
    return new SseEventBuilder();
  }

}
