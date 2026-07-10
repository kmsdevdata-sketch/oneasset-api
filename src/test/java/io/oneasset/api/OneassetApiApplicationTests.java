package io.oneasset;

import io.oneasset.adapter.outbound.apikey.persistence.ApiKeyJpaRepository;
import io.oneasset.adapter.outbound.asset.persistence.AssetJpaRepository;
import io.oneasset.adapter.outbound.assetvariant.persistence.AssetVariantJpaRepository;
import io.oneasset.adapter.outbound.processing.persistence.ProcessingLogJpaRepository;
import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.adapter.outbound.projectmember.persistence.ProjectMemberJpaRepository;
import io.oneasset.adapter.outbound.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude="
          + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
          + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
          + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration",
      "COGNITO_ISSUER_URI=https://cognito-idp.ap-northeast-2.amazonaws.com/test"
    })
class OneassetApiApplicationTests {

  @MockitoBean
  private AssetJpaRepository assetJpaRepository;

  @MockitoBean
  private AssetVariantJpaRepository assetVariantJpaRepository;

  @MockitoBean
  private ProcessingLogJpaRepository processingLogJpaRepository;

  @MockitoBean
  private ApiKeyJpaRepository apiKeyJpaRepository;

  @MockitoBean
  private ProjectJpaRepository projectJpaRepository;

  @MockitoBean
  private ProjectMemberJpaRepository projectMemberJpaRepository;

  @MockitoBean
  private UserJpaRepository userJpaRepository;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @Test
  void contextLoads() {}
}
