package io.oneasset;

import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.adapter.outbound.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude="
          + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
          + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
          + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
    })
class OneassetApiApplicationTests {

  @MockitoBean private ProjectJpaRepository projectJpaRepository;

  @MockitoBean private UserJpaRepository userJpaRepository;

  @Test
  void contextLoads() {}
}
