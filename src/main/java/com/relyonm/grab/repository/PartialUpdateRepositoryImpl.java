package com.relyonm.grab.repository;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.core.RepositoryMethodContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

public class PartialUpdateRepositoryImpl<ID> implements PartialUpdateRepository<ID> {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public PartialUpdateRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  @Transactional
  public void partialUpdate(ID id, Map<String, Object> updates) {
    if (updates == null || updates.isEmpty()) return;

    Class<?> domainType = RepositoryMethodContext.getContext().getMetadata().getDomainType();
    String tableName = resolveTableName(domainType);

    String setClause = updates
      .keySet()
      .stream()
      .map(key -> key + " = :" + key)
      .collect(Collectors.joining(", "));

    String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id = :id";

    MapSqlParameterSource params = new MapSqlParameterSource(updates);
    params.addValue("id", id);

    jdbcTemplate.update(sql, params);
  }

  private String resolveTableName(Class<?> domainType) {
    Table tableAnnotation = domainType.getAnnotation(Table.class);
    if (tableAnnotation != null && !tableAnnotation.value().isEmpty()) {
      return tableAnnotation.value();
    }
    // Fallback to class name (lower_snake_case)
    return toSnakeCase(domainType.getSimpleName());
  }

  private String toSnakeCase(String input) {
    return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
  }
}
