package com.univ.tracedin.infra.elasticsearch;

import java.io.IOException;

@FunctionalInterface
public interface ESSupplier<T> {

    T get() throws IOException;
}
