package com.capstone.lifesabit.gateguard;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    public static class SampleInput {
        public String thisIsAString;
        public boolean thisIsABool;
    }

    public static class SampleResponse {
        public int thisIsAnInt;
    }

    @RequestMapping(value = "/samplerequest", method = RequestMethod.POST)
    ResponseEntity<SampleResponse> sampleRequestHandler(HttpServletRequest request, @RequestBody SampleInput inputs) {
        SampleResponse resp = new SampleResponse();
        resp.thisIsAnInt = 42;
        return ResponseEntity.ok(resp);
    }
}