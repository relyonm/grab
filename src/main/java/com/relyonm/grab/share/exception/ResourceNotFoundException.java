package com.relyonm.grab.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {

  private final String message;

  public ResourceNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
