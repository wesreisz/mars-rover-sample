package com.example.mars.exec;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BoundaryPolicyTest {

  @Test
  void enumValues_containsAllExpectedPolicies() {
    BoundaryPolicy[] values = BoundaryPolicy.values();

    assertThat(values).hasSize(3);
    assertThat(values)
        .containsExactlyInAnyOrder(
            BoundaryPolicy.STRICT, BoundaryPolicy.IGNORE, BoundaryPolicy.STOP_ON_OOB);
  }

  @ParameterizedTest
  @EnumSource(BoundaryPolicy.class)
  void valueOf_retrievesCorrectEnumValue(BoundaryPolicy policy) {
    String name = policy.name();
    BoundaryPolicy retrieved = BoundaryPolicy.valueOf(name);

    assertThat(retrieved).isEqualTo(policy);
  }

  @Test
  void specificValues_areAccessible() {
    assertThat(BoundaryPolicy.STRICT).isNotNull();
    assertThat(BoundaryPolicy.IGNORE).isNotNull();
    assertThat(BoundaryPolicy.STOP_ON_OOB).isNotNull();
  }
}
