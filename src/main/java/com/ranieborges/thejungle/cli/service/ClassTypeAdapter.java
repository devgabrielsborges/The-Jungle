package com.ranieborges.thejungle.cli.service; // Or your preferred utility package

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class<?>> {

    @Override
    public void write(JsonWriter out, Class<?> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getName()); // Serialize as fully qualified class name
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String className = in.nextString();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to find class: " + className, e);
        }
    }
}
