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
}
