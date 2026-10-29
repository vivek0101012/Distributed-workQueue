package com.workqueue.common.handler;

public class InvalidPayloadException extends  RuntimeException{
    public InvalidPayloadException (String message){
        super(message);
    }
}
