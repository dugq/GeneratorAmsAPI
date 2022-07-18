package com.dugq.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dugq
 * @date 2022/6/29 1:04 上午
 */
public class SqlException extends RuntimeException{
    private static final long serialVersionUID = 5744755788462430786L;
    private final List<String> warns;

    public SqlException(List<String> warns) {
        this.warns = warns;
    }

    public SqlException(String warn) {
        super(warn);
        this.warns = new ArrayList<>();
        this.warns.add(warn);
    }

    public List<String> getWarns() {
        return warns;
    }
}
