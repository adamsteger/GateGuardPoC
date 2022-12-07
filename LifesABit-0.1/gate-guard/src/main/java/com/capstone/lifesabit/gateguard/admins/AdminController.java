package com.capstone.lifesabit.gateguard.admins;

import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.lifesabit.gateguard.SQLLinker;
import com.capstone.lifesabit.gateguard.login.Member;
import com.capstone.lifesabit.gateguard.login.Session;
import com.capstone.lifesabit.gateguard.login.SessionManager;

@RestController
public class AdminController {

    public static class LoadUsersRequest {
        public String sessionKey;
    }

    public static class LoadUsersResponse {
        public Member[] userList;
        public String message;
    }

    public static class DeleteUserRequest {
        public String sessionKey;
        public String userID;
    }

    public static class DeleteUserResponse {
        public boolean success;
        public String message;
    }

    public static class DeletePassRequest {
        public String sessionKey;
        public String passID;
    }

    public static class DeletePassResponse {
        public boolean success;
        public String message;
    }

    @RequestMapping(value = "/load-users", method = RequestMethod.POST)
    ResponseEntity<LoadUsersResponse> loadUsersHandler(HttpServletRequest request, @RequestBody LoadUsersRequest inputs) {
        LoadUsersResponse resp = new LoadUsersResponse();
        Session adminSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        ArrayList<Member> memberList = SQLLinker.getInstance().loadMembers(adminSession.getMember().getOrgId());
        if (memberList == null) {
            resp.message = "Error loading users";
        } else {
            resp.message = "Users loaded successfully";
            resp.userList = new Member[memberList.size()];
            for (int i = 0; i < memberList.size(); i++) {
                resp.userList[i] = memberList.get(i);
            }
        }
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/delete-user", method = RequestMethod.POST)
    ResponseEntity<DeleteUserResponse> deleteUserHandler(HttpServletRequest request, @RequestBody DeleteUserRequest inputs) {
        DeleteUserResponse resp = new DeleteUserResponse();
        Session adminSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        SQLLinker.getInstance().deletePasses(UUID.fromString(inputs.userID));
        resp.success = SQLLinker.getInstance().deleteUser(UUID.fromString(inputs.userID));
        if (!resp.success) {
            resp.message = "Error deleting user";
        } else {
            resp.message = "User successfully deleted";
        }

        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/delete-pass", method = RequestMethod.POST)
    ResponseEntity<DeletePassResponse> deletePassHandler(HttpServletRequest request, @RequestBody DeletePassRequest inputs) {
        DeletePassResponse resp = new DeletePassResponse();
        Session adminSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        SQLLinker.getInstance().deletePasses(UUID.fromString(inputs.passID));
        resp.success = SQLLinker.getInstance().deletePass(UUID.fromString(inputs.passID));
        if (!resp.success) {
            resp.message = "Error deleting pass";
        } else {
            resp.message = "User successfully deleted";
        }

        return ResponseEntity.ok(resp);
    }
}