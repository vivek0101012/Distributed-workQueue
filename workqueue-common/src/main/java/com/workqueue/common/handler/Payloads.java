package com.workqueue.common.handler;

import java.util.Map;

public final class  Payloads {
    private  Payloads(){}
    public static String requireText(Map<String, Object> payload, String key){

        Object value=payload.get(key);

        if(!(value instanceof String text) || text.isBlank()) {
            throw new InvalidPayloadException("payload." + key +
                    " is required and must be non-empty text");
        }

        return text;
    }

    public static int requirePositiveInt(Map<String, Object> payload, String key){

        Object value=payload.get(key);
        if ( ! (value instanceof Number number) || number.intValue()<=0){

            throw new InvalidPayloadException("payload." + key + " is required and must be a positive number");

        }

            return number.intValue();
    }
}
