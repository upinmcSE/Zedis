package io.upinmcSE.core.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RespCodec {
    private static final String CRLF = "\r\n";
    private static final byte[] RESP_NULL = "$-1\r\n".getBytes(StandardCharsets.UTF_8);

    // +OK\r\n => OK, 5
    public DecodeResult readSimpleString(byte[] data, int position) {
        int start = position;
        int n = data.length;
        while (position < n && data[position] != CRLF.getBytes()[0]) {
            position++;
        }

        if (position >= n) {
            throw new RuntimeException("Invalid RESP: Missing CRLF");
        }

        String value = new String(data, start, position - start, StandardCharsets.UTF_8);
        return new DecodeResult(value, position + 2);
    }

    // :123\r\n => 123
    public DecodeResult readInt(byte[] data, int position) {
        int n = data.length;
        int result = 0;
        int sign = 1;

        if(data[position] == '-'){
            sign = -1;
            position++;
        }else if(data[position] == '+'){
            position++;
        }

        while (position < n && data[position] != CRLF.getBytes()[0]) {
            result = result * 10 + (data[position] - '0');
            position++;
        }

        if (position >= n) {
            throw new RuntimeException("Invalid RESP: Missing CRLF");
        }

        return new DecodeResult(result * sign, position + 2);
    }

    public DecodeResult readError(byte[] data) {
        return readSimpleString(data, 1);
    }

    // $5\r\nhello\r\n => 5, 4
    public DecodeResult readLength(byte[] data) {
        DecodeResult result = readInt(data, 1);
        result.setValue((int) result.getValue());
        return result;
    }

    // $5\r\nhello\r\n => "hello"
    public DecodeResult readBulkString(byte[] data, int position) {
        DecodeResult lengthPos = readLength(data);

        return new DecodeResult(
                new String(data, lengthPos.getNextPos(), position + (int)lengthPos.getValue(), StandardCharsets.UTF_8),
                lengthPos.getNextPos() + (int)lengthPos.getValue());

    }

    // *2\r\n$5\r\nhello\r\n$5\r\nworld\r\n => {"hello", "world"}
    public DecodeResult readArray(byte[] data, int position) {
        return null;
    }

    public DecodeResult decodeOne(byte[] data, int pos){
        if(data.length == 0) {
            return new DecodeResult(null, pos);
        }
        return switch (data[pos]) {
            case '+' -> readSimpleString(data, pos + 1);
            case ':' -> readInt(data, pos + 1);
            case '-' -> readError(data);
            case '$' -> readBulkString(data, pos);
            case '*' -> readArray(data, pos + 1);
            default -> new DecodeResult(null, pos);
        };
    }

    public Object decode(byte[] data){
        return decodeOne(data, 0).getValue();
    }

//    -----------------------END-CODE-----------------------------

    private byte[] encodeString(String s){
        String formatted = String.format("$%d\r\n%s\r\n", s.length(), s);
        return formatted.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] encodeStringList(List<String> sa) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (String s : sa) {
            buf.write(encodeString(s));
        }
        String header = String.format("*%d\r\n", sa.size());

        ByteArrayOutputStream finalBuf = new ByteArrayOutputStream();
        finalBuf.write(header.getBytes(StandardCharsets.UTF_8));
        finalBuf.write(buf.toByteArray());
        return finalBuf.toByteArray();
    }

    public byte[] encode(Object value, boolean isSimpleString){
        try{
            if(value instanceof String){
                String v = (String) value;
                if (isSimpleString) {
                    return String.format("+%s%s", value, CRLF).getBytes(StandardCharsets.UTF_8);
                }
                return String.format("$%d%s%s%s", v.length(), CRLF, v, CRLF).getBytes(StandardCharsets.UTF_8);
            } else if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
                return String.format(":%s%s", value, CRLF).getBytes(StandardCharsets.UTF_8);
            } else if(value instanceof Throwable){
                return String.format("-%s\r\n", ((Throwable) value).getMessage()).getBytes(StandardCharsets.UTF_8);
            } else if (value instanceof List<?> list) {
                if (list.isEmpty()) {
                    return "*0\r\n".getBytes(StandardCharsets.UTF_8);
                }

                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (Object item : list) {
                    buf.write(encode(item, false));
                }

                String header = String.format("*%d\r\n", list.size());
                ByteArrayOutputStream finalBuf = new ByteArrayOutputStream();
                finalBuf.write(header.getBytes(StandardCharsets.UTF_8));
                finalBuf.write(buf.toByteArray());
                return finalBuf.toByteArray();
            } else if (value instanceof Object[] arr) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (Object item : arr) {
                    buf.write(encode(item, false));
                }
                String header = String.format("*%d\r\n", arr.length);
                ByteArrayOutputStream finalBuf = new ByteArrayOutputStream();
                finalBuf.write(header.getBytes(StandardCharsets.UTF_8));
                finalBuf.write(buf.toByteArray());
                return finalBuf.toByteArray();
            } else {
                return RESP_NULL;
            }
        }catch (Exception e){
            return RESP_NULL;
        }
    }
}
