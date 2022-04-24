package com.att.training.sse;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTest {

    @Test
    void givenTwoClientsWithSameNameAndId_whenEquals_thenReturnTrue() {
        var name = "some-client";
        var conferenceId = 101;
        var client1 = new Client(name, conferenceId, new SseEmitter());
        var client2 = new Client(name, conferenceId, new SseEmitter());
        assertThat(client1).isEqualTo(client2);
    }
}