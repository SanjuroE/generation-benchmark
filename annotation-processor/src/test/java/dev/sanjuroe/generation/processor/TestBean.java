package dev.sanjuroe.generation.processor;

import dev.sanjuroe.generation.annotation.Bean;

@Bean
public class TestBean {

    private String str;

    private int i;

    private boolean b;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }
}
