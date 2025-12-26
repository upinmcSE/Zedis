package io.upinmcSE.core.protocol;

public class DecodeResult {
    private Object value;
    private int nextPos;

    public DecodeResult(Object value, int nextPos) {
        this.value = value;
        this.nextPos = nextPos;
    }

    public Object getValue() {
        return value;
    }
    public int getNextPos() {
        return nextPos;
    }

    public void setNextPos(int nextPos) {
        this.nextPos = nextPos;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
