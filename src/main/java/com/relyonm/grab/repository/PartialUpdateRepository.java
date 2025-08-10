package com.relyonm.grab.repository;

import java.util.Map;

public interface PartialUpdateRepository<ID> {

  void partialUpdate(ID id, Map<String, Object> updates);
}
