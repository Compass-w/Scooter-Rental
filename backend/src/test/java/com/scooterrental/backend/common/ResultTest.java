package com.scooterrental.backend.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {

    @Test
    void successWrapsDataWithStandardCodeAndMessage() {
        Result<String> result = Result.success("ok");

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("Success");
        assertThat(result.getData()).isEqualTo("ok");
    }

    @Test
    void errorWrapsCodeAndMessageWithoutData() {
        Result<Object> result = Result.error(400, "Bad request");

        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Bad request");
        assertThat(result.getData()).isNull();
    }
}
