package com.advancedit.ppms.utils;

import java.util.Base64;
import java.util.function.Supplier;

public class GeneralUtils {

    public static <T> void ifNoNull(T t, Supplier<T> supplier){
    }

    public static String encode(String code){
       return Base64.getEncoder().encodeToString(code.getBytes());
    }

    public static String decode(String code){
        return new String(Base64.getDecoder().decode(code.getBytes()));
    }
}
