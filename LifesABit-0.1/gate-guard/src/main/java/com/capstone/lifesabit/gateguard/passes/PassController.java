package com.capstone.lifesabit.gateguard.passes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import com.capstone.lifesabit.gateguard.login.Member.MemberType;

@RestController
public class PassController {

    public static class VerifyPassRequest {
        public String passID;
    }

    public static class VerifyPassResponse {
        public boolean isValid;
        public boolean usageBased;
        public long expirationDate;
        public int usesLeft;
        public int usesTotal;
        public String message;
    }

    public static class UsePassRequest {
        public String passID;
    }

    public static class UsePassResponse {
        public boolean isValid;
        public boolean usageBased;
        public long expirationDate;
        public int usesLeft;
        public int usesTotal;
        public String message;
    }
    
    public static class LoadPassesRequest {
        public String sessionKey;
    }

    public static class LoadPassesResponse {
        public Pass[] passList;
        public String message;
    }

    public static class LoadPassesAdminRequest {
        public String sessionKey;
        public String userID;
    }

    public static class RevokePassAdminRequest {
        public String sessionKey;
        public String passID;
    }

    public static class CreatePassRequest {
        public String sessionKey;
        public boolean usageBased;
        public String firstName;
        public String lastName;
        public String email;
        public long expirationDate;
        public int usesLeft;
        public int usesTotal;
    }

    public static class CreatePassResponse {
        public boolean success;
        public String message;
        public String passID;
    }

    public static class EditPassRequest {
        public String sessionKey;
        public String passID;
        public boolean usageBased;
        public String firstName;
        public String lastName;
        public String email;
        public long expirationDate;
        public int usesLeft;
        public int usesTotal;
    }

    public static class EditPassResponse {
        public boolean success;
        public String message;
    }

    public static class RevokePassRequest {
        public String sessionKey;
        public String passID;
    }

    public static class RevokePassResponse {
        public boolean success;
        public String message;
    }

    @RequestMapping(value = "/load-passes", method = RequestMethod.POST)
    ResponseEntity<LoadPassesResponse> loadPassesHandler(HttpServletRequest request,
            @RequestBody LoadPassesRequest inputs) {
        LoadPassesResponse resp = new LoadPassesResponse();
        if (inputs.sessionKey == null || inputs.sessionKey.isEmpty()) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        ArrayList<Pass> passesList = SQLLinker.getInstance().loadPasses(userSession.getMember());
        if (passesList == null) {
            resp.message = "Error loading passes";
        } else {
            resp.message = "Passes loaded successfully";
            resp.passList = new Pass[passesList.size()];
            for (int i = 0; i < passesList.size(); i++) {
                resp.passList[i] = passesList.get(i);
            }
        }

        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/load-passes-admin", method = RequestMethod.POST)
    ResponseEntity<LoadPassesResponse> loadPassesHandler(HttpServletRequest request,
            @RequestBody LoadPassesAdminRequest inputs) {
        LoadPassesResponse resp = new LoadPassesResponse();
        if (inputs.sessionKey == null || inputs.sessionKey.isEmpty()) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        if (userSession.getMember().getType() != MemberType.ADMIN) {
            resp.message = "You are not an administrator.";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
        }
        ArrayList<Pass> passesList = SQLLinker.getInstance().loadPasses(UUID.fromString(inputs.userID));
        if (passesList == null) {
            resp.message = "Error loading passes";
        } else {
            resp.message = "Passes loaded successfully";
            resp.passList = new Pass[passesList.size()];
            for (int i = 0; i < passesList.size(); i++) {
                resp.passList[i] = passesList.get(i);
            }
        }

        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/create-pass", method = RequestMethod.POST)
    ResponseEntity<CreatePassResponse> createPassHandler(HttpServletRequest request,
            @RequestBody CreatePassRequest inputs) throws ParseException {
        CreatePassResponse resp = new CreatePassResponse();
        if (inputs.sessionKey == null || inputs.sessionKey.isEmpty()) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        Pass pass = null;
        if (inputs.usageBased) {
            pass = new Pass(inputs.firstName, inputs.lastName, inputs.email, userSession.getMember().getUuid(),
                    inputs.usesTotal);
        } else {
            pass = new Pass(inputs.firstName, inputs.lastName, inputs.email, userSession.getMember().getUuid(),
                    inputs.expirationDate);
        }
        resp.success = SQLLinker.getInstance().addPass(pass);
        if (!resp.success) {
            resp.message = "Error creating pass";
        } else {
            resp.message = "Pass successfully created";
        }
        resp.passID = pass.getPassID().toString();
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/edit-pass", method = RequestMethod.POST)
    ResponseEntity<EditPassResponse> editPassHandler(HttpServletRequest request, @RequestBody EditPassRequest inputs)
            throws ParseException {
        EditPassResponse resp = new EditPassResponse();
        if (inputs.sessionKey == null || inputs.sessionKey.isEmpty()) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        // TODO determine whether pass is expired
        if (inputs.usageBased) {
            resp.success = SQLLinker.getInstance().editPass(UUID.fromString(inputs.passID), inputs.firstName,
                    inputs.lastName, inputs.email, inputs.usageBased, inputs.usageBased, inputs.usesLeft,
                    inputs.usesTotal);
        } else if (!inputs.usageBased) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            resp.success = SQLLinker.getInstance().editPass(UUID.fromString(inputs.passID), inputs.firstName,
                    inputs.lastName, inputs.email, inputs.usageBased, inputs.usageBased,
                    inputs.expirationDate);
        }

        if (!resp.success) {
            resp.message = "Error editing pass";
        } else {
            resp.message = "Pass successfully edited";
        }

        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/revoke-pass", method = RequestMethod.POST)
    ResponseEntity<RevokePassResponse> revokePassHandler(HttpServletRequest request,
            @RequestBody RevokePassRequest inputs) {
        RevokePassResponse resp = new RevokePassResponse();
        if (inputs.sessionKey == null || inputs.sessionKey.isEmpty()) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
            resp.message = "Error: Invalid session key";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
        Member user = userSession.getMember();
        Pass pass = SQLLinker.getInstance().getPass(UUID.fromString(inputs.passID));
        if (pass.getUserID().equals(user.getUuid()) || user.getType() == MemberType.ADMIN) {
            resp.success = SQLLinker.getInstance().deletePass(UUID.fromString(inputs.passID));
        } else {
            resp.message = "Error: You do not have permission to delete this pass.";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
        }

        if (!resp.success) {
            resp.message = "Error deleting pass";
        } else {
            resp.message = "Pass successfully deleted";
        }
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/verify-pass", method = RequestMethod.POST)
    ResponseEntity<VerifyPassResponse> revokePassHandler(HttpServletRequest request,
            @RequestBody VerifyPassRequest inputs) {
        VerifyPassResponse resp = new VerifyPassResponse();
        // Check if pass exists
        Pass pass = SQLLinker.getInstance().getPass(inputs.passID);
        if (pass == null) {
            resp.isValid = false;
            resp.message = "That pass does not exist";
            return ResponseEntity.ok(resp);
        }
        // Fill in pass info
        resp.isValid = !pass.isExpired();
        resp.expirationDate = (pass.getExpirationDate() == null) ? 0 : pass.getExpirationDate();
        resp.usageBased = pass.getUsageBased();
        resp.usesLeft = pass.getUsesLeft();
        resp.usesTotal = pass.getUsesTotal();
        resp.message = resp.isValid ? "Successful" : "Pass expired";
        return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/use-pass", method = RequestMethod.POST)
    ResponseEntity<UsePassResponse> revokePassHandler(HttpServletRequest request,
            @RequestBody UsePassRequest inputs) {
        UsePassResponse resp = new UsePassResponse();
        // Check if pass exists
        Pass pass = SQLLinker.getInstance().getPass(inputs.passID);
        if (pass == null) {
            resp.isValid = false;
            resp.message = "That pass does not exist";
            return ResponseEntity.ok(resp);
        }
        // Use the pass
        boolean used = pass.use();
        if (used && pass.getUsageBased()) {
            SQLLinker.getInstance().updatePass(UUID.fromString(inputs.passID), pass);
        }
        // Fill in pass info
        resp.isValid = !pass.isExpired();
        resp.expirationDate = (pass.getExpirationDate() == null) ? 0 : pass.getExpirationDate();
        resp.usageBased = pass.getUsageBased();
        resp.usesLeft = pass.getUsesLeft();
        resp.usesTotal = pass.getUsesTotal();
        resp.message = resp.isValid ? "Successful" : "Pass expired";
        return ResponseEntity.ok(resp);
    }
}