package com.workqueue.producer.service;

import java.util.Set;
import java.util.TreeSet;

public class UnknownTaskTypeException extends RuntimeException {
    public UnknownTaskTypeException(String type, Set<String> knownTypes) {
        super("Unknown task type '" + type + "'. Known types: " + new TreeSet<>(knownTypes));
    }
}