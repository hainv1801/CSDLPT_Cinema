package com.cinema.cinema.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message; // Có thể là String hoặc List<String> cho validation
    private T data;

    public RestResponse() {
    }

    public RestResponse(int statusCode, String error, Object message, T data) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public RestResponse(Object message, T data) {
        this.statusCode = 200;
        this.message = message;
        this.data = data;
    }

}
