package io.upinmcSE.core.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class RespCodecTest {
    private RespCodec respCodec;

    @BeforeEach
    void setUp(){
        respCodec = new RespCodec();
    }

    @Test
    @DisplayName("+OK\r\n -> OK, 5")
    void testReadSimpleString_Valid(){
        String rawInput = "+OK\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readSimpleString(data, 1);

        Assertions.assertEquals("OK", result.getValue());
        Assertions.assertEquals(5, result.getNextPos());
    }

    @Test
    @DisplayName("+\r\n -> '', 1")
    void testReadSimpleString_Empty(){
        String  rawInput = "+\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readSimpleString(data, 1);

        Assertions.assertEquals("", result.getValue());
        Assertions.assertEquals(3, result.getNextPos());
    }

    @Test
    @DisplayName(":-123\\r\\n -> -123")
    void testReadInt_Valid1(){
        String rawInput = ":-123\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readSimpleString(data, 1);

        Assertions.assertEquals("-123", result.getValue());
        Assertions.assertEquals(7, result.getNextPos());
    }

    @Test
    @DisplayName(":123\\r\\n -> 123")
    void testReadInt_Valid2(){
        String rawInput = ":123\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readSimpleString(data, 1);

        Assertions.assertEquals("123", result.getValue());
        Assertions.assertEquals(6, result.getNextPos());
    }

    @Test
    @DisplayName("-Error message\r\n -> Error message")
    void testReadError_Valid(){
        String rawInput = "-Error message\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readError(data);

        Assertions.assertEquals("Error message", result.getValue());
        Assertions.assertEquals(16, result.getNextPos());
    }

    @Test
    @DisplayName("$5\r\nhello\r\n => 5, 4")
    void testReadLength_Valid1(){
        String rawInput = "$5\r\nhello\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readLength(data);

        Assertions.assertEquals(5, result.getValue());
        Assertions.assertEquals(4, result.getNextPos());
    }

    @Test
    @DisplayName("$5\r\nhello\r\n => hello")
    void testReadBulkString_Valid(){
        String rawInput = "$5\r\nhello\r\n";
        byte[] data = rawInput.getBytes(StandardCharsets.UTF_8);

        DecodeResult result = respCodec.readBulkString(data, 0);

        Assertions.assertEquals("hello", result.getValue());
        Assertions.assertEquals(9, result.getNextPos());
    }

}
