package com.livk.autoconfigure.mybatis.handler.time;

import com.livk.autoconfigure.mybatis.handler.FunctionHandle;

import java.time.LocalDate;

/**
 * <p>
 * LocalDateFunction
 * </p>
 *
 * @author livk
 *
 */
public class LocalDateFunction implements FunctionHandle<LocalDate> {

    @Override
    public LocalDate handler() {
        return LocalDate.now();
    }

}