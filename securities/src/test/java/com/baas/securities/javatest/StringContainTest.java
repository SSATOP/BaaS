package com.baas.securities.javatest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringContainTest {
    @Test
    void contains () {
        // given
        String val = "|12344";
        // when
        Assertions.assertThat(val.contains("|")).isTrue();
        // then
    }
}
