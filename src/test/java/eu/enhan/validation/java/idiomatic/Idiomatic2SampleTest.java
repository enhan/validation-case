package eu.enhan.validation.java.idiomatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.typesafe.config.ConfigException;

/**
 *
 */
public class Idiomatic2SampleTest {

    @Test
    void get_should_return_null_when_throwing_exception() {
        // Given
        List<ConfigErrors.ConfigError>  errors = new ArrayList<>();

        // When
        Optional<String> str = Idiomatic2Sample.get("some.path", errors, () -> {throw new ConfigException.Missing("some.path");} );

        // Then
        Assertions.assertThat(str).isEmpty();
        Assertions.assertThat(errors).hasSize(1);
    }

}
