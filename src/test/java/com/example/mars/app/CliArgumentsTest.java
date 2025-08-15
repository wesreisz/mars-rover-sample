package com.example.mars.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mars.exec.BoundaryPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for CliArguments class, covering CLI argument parsing, validation,
 * and boundary policy mapping functionality.
 */
class CliArgumentsTest {

  /**
   * Test default behavior with no arguments
   */
  @Test
  void parse_noArguments_usesDefaults() throws CliArgumentsException {
    // When parsing empty arguments
    CliArguments cli = CliArguments.parse(new String[]{});
    
    // Then defaults are applied
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.STRICT);
    assertThat(cli.isFailFast()).isTrue();
    assertThat(cli.isCollectErrors()).isFalse();
  }

  /**
   * Test explicit --strict flag
   */
  @Test
  void parse_strictFlag_setsStrictPolicy() throws CliArgumentsException {
    // When parsing --strict flag
    CliArguments cli = CliArguments.parse(new String[]{"--strict"});
    
    // Then strict policy is set
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.STRICT);
  }

  /**
   * Test --ignore-oob flag
   */
  @Test
  void parse_ignoreOobFlag_setsIgnorePolicy() throws CliArgumentsException {
    // When parsing --ignore-oob flag
    CliArguments cli = CliArguments.parse(new String[]{"--ignore-oob"});
    
    // Then ignore policy is set
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.IGNORE);
  }

  /**
   * Test --stop-on-oob flag
   */
  @Test
  void parse_stopOnOobFlag_setsStopPolicy() throws CliArgumentsException {
    // When parsing --stop-on-oob flag
    CliArguments cli = CliArguments.parse(new String[]{"--stop-on-oob"});
    
    // Then stop policy is set
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.STOP_ON_OOB);
  }

  /**
   * Test --fail-fast flag (explicit)
   */
  @Test
  void parse_failFastFlag_setsFailFast() throws CliArgumentsException {
    // When parsing --fail-fast flag
    CliArguments cli = CliArguments.parse(new String[]{"--fail-fast"});
    
    // Then fail-fast is enabled
    assertThat(cli.isFailFast()).isTrue();
    assertThat(cli.isCollectErrors()).isFalse();
  }

  /**
   * Test --collect-errors flag
   */
  @Test
  void parse_collectErrorsFlag_setsCollectErrors() throws CliArgumentsException {
    // When parsing --collect-errors flag
    CliArguments cli = CliArguments.parse(new String[]{"--collect-errors"});
    
    // Then collect errors is enabled
    assertThat(cli.isFailFast()).isFalse();
    assertThat(cli.isCollectErrors()).isTrue();
  }

  /**
   * Test boundary policy precedence - later flags override earlier ones
   */
  @Test
  void parse_multipleBoundaryFlags_lastFlagWins() throws CliArgumentsException {
    // When parsing multiple boundary policy flags
    CliArguments cli = CliArguments.parse(new String[]{"--strict", "--ignore-oob", "--stop-on-oob"});
    
    // Then the last flag wins
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.STOP_ON_OOB);
  }

  /**
   * Test error handling policy precedence
   */
  @Test
  void parse_multipleErrorFlags_lastFlagWins() throws CliArgumentsException {
    // When parsing multiple error handling flags
    CliArguments cli = CliArguments.parse(new String[]{"--fail-fast", "--collect-errors"});
    
    // Then the last flag wins
    assertThat(cli.isFailFast()).isFalse();
    assertThat(cli.isCollectErrors()).isTrue();
  }

  /**
   * Test combination of different flag types
   */
  @Test
  void parse_combinedFlags_allApplied() throws CliArgumentsException {
    // When parsing combined boundary and error handling flags
    CliArguments cli = CliArguments.parse(new String[]{"--ignore-oob", "--collect-errors"});
    
    // Then both settings are applied
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.IGNORE);
    assertThat(cli.isFailFast()).isFalse();
    assertThat(cli.isCollectErrors()).isTrue();
  }

  /**
   * Test unknown flag throws exception with usage message
   */
  @ParameterizedTest
  @ValueSource(strings = {"--wat", "--unknown", "--help", "-h", "--verbose", "--debug"})
  void parse_unknownFlag_throwsExceptionWithUsage(String unknownFlag) {
    // When parsing unknown flag
    assertThatThrownBy(() -> CliArguments.parse(new String[]{unknownFlag}))
        .isInstanceOf(CliArgumentsException.class)
        .hasMessageContaining("Usage: java -jar mars-rovers.jar [OPTIONS]")
        .hasMessageContaining("Options:")
        .hasMessageContaining("--strict")
        .hasMessageContaining("--ignore-oob")
        .hasMessageContaining("--stop-on-oob")
        .hasMessageContaining("--fail-fast")
        .hasMessageContaining("--collect-errors")
        .hasMessageContaining("Unknown option: " + unknownFlag);
  }

  /**
   * Test multiple unknown flags - first one triggers error
   */
  @Test
  void parse_multipleUnknownFlags_firstOneReported() {
    // When parsing multiple unknown flags
    assertThatThrownBy(() -> CliArguments.parse(new String[]{"--unknown1", "--unknown2"}))
        .isInstanceOf(CliArgumentsException.class)
        .hasMessageContaining("Unknown option: --unknown1");
  }

  /**
   * Test mixed valid and invalid flags - invalid one triggers error
   */
  @Test
  void parse_mixedValidInvalidFlags_invalidReported() {
    // When parsing mix of valid and invalid flags
    assertThatThrownBy(() -> CliArguments.parse(new String[]{"--strict", "--invalid", "--ignore-oob"}))
        .isInstanceOf(CliArgumentsException.class)
        .hasMessageContaining("Unknown option: --invalid");
  }

  /**
   * Test all valid flags together
   */
  @Test
  void parse_allValidFlags_parsedCorrectly() throws CliArgumentsException {
    // When parsing all valid flags (boundary policy flags are mutually exclusive, last wins)
    CliArguments cli = CliArguments.parse(new String[]{
        "--strict", "--fail-fast", "--ignore-oob", "--collect-errors", "--stop-on-oob"
    });
    
    // Then final state reflects last boundary policy and error handling flags
    assertThat(cli.getBoundaryPolicy()).isEqualTo(BoundaryPolicy.STOP_ON_OOB);
    assertThat(cli.isFailFast()).isFalse();
    assertThat(cli.isCollectErrors()).isTrue();
  }
}