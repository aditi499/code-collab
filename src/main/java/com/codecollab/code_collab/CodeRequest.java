package com.codecollab.code_collab;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeRequest {

    @JsonProperty("language")
    public String language;

    @JsonProperty("code")
    public String code;
}