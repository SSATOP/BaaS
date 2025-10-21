package com.baas.securities.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StompCommandTest {

    @Test
    void isCloseCommandTest() {
        // given
        String unsubscribe = "UNSUBSCRIBE";
        String disconnect = "DISCONNECT";
        // when
        Assertions.assertThat(CustomStompCommand.isCloseCommand(unsubscribe)).isTrue();
        Assertions.assertThat(CustomStompCommand.isCloseCommand(disconnect)).isTrue();
        // then
    }

}