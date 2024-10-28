package com.univ.tracedin.api.global.util;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class GrpcMappingUtils {

    public static Object convertValue(String value) {
        if (isNumeric(value)) {
            return parseNumeric(value);
        }
        return value;
    }

    private static Object parseNumeric(String value) {
        if (value.contains(".")) {
            return Double.parseDouble(value);
        }
        return Integer.parseInt(value);
    }
}
