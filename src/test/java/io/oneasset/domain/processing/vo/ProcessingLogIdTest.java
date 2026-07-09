package io.oneasset.domain.processing.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProcessingLogIdTest {

  @Test
  void createsProcessingLogIdFromUuidString() {
    UUID value = UUID.randomUUID();

    ProcessingLogId processingLogId = ProcessingLogId.fromString(value.toString());

    assertThat(processingLogId.value()).isEqualTo(value);
    assertThat(processingLogId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> ProcessingLogId.fromString("not-a-uuid"))
        .isInstanceOf(RuntimeException.class);
  }
}
