package com.capstone.lifesabit.gateguard.notifications;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    public static class LoadNotifsRequest {
        public String sessionKey;
    }

    public static class LoadNotifsResponse {
        public Notification[] notifications;
    }

    // @RequestMapping(value = "/samplerequest", method = RequestMethod.POST)
    // ResponseEntity<LoadNotifsResponse> sampleRequestHandler(HttpServletRequest request, @RequestBody LoadNotifsRequest inputs) {
    //     LoadNotifsResponse resp = new LoadNotifsResponse();
    //     return ResponseEntity.ok(resp);
    // }
}