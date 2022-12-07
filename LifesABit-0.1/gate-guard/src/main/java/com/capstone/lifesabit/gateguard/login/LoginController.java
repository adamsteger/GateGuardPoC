package com.capstone.lifesabit.gateguard.login;

import java.util.UUID;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.lifesabit.gateguard.SQLLinker;
import com.capstone.lifesabit.gateguard.login.Member.MemberType;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
public class LoginController {

    public static class LoginRequest {
        public String username;
        public String hashedPassword;
    }

    public static class LoginResponse {
        public String sessionKey;
    }

    public static class LogoutRequest {
      public String sessionKey;
  }

  public static class LogoutResponse {
      public boolean success;
  }

  public static class GetUserInfoRequest {
    public String sessionKey;
  }

  public static class GetUserInfoResponse {
    public String firstName;
    public String lastName;
    public boolean isAdmin;
  }

  public static class CreateAccountRequest {
    public String username;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String emailAddress;
    public String hashedPassword;
    public String verificationCode;
    public String confirmPassword;
  }
  
  public static class CreateAccountResponse {
    public String name;
    public String type;
    public String sessionKey;
    public boolean success;
    public String message;
  }

  public static class LoadMembersRequest {
    public String sessionKey;
  }

  public static class LoadMembersResponse {
    public SimpleMember[] memberList;
  }

    @RequestMapping(value = "/log-in", method = RequestMethod.POST)
    ResponseEntity<LoginResponse> loginHandler(HttpServletRequest request, @RequestBody LoginRequest inputs) {
      LoginResponse resp = new LoginResponse();
      Session userSession = SessionManager.getSession(inputs.username, inputs.hashedPassword);
      if (userSession == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
      }
      if (userSession.isExpired()) {
        userSession = SessionManager.createNewSession(inputs.username, inputs.hashedPassword);
      }
      resp.sessionKey = userSession.getSessionKey().toString();
      return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/log-out", method = RequestMethod.POST)
    ResponseEntity<LogoutResponse> logoutHandler(HttpServletRequest request, @RequestBody LogoutRequest inputs) {
      LogoutResponse resp = new LogoutResponse();
      Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
      resp.success = false;
      if (userSession == null || userSession.isExpired()) {
        resp.success = true;
        return ResponseEntity.ok(resp);
      }
      resp.success = SessionManager.removeSession(UUID.fromString(inputs.sessionKey));
      return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/user-info", method = RequestMethod.POST)
    ResponseEntity<GetUserInfoResponse> getUserNameHandler(HttpServletRequest request, @RequestBody GetUserInfoRequest inputs) {
      GetUserInfoResponse resp = new GetUserInfoResponse();
      Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
      if (!SessionManager.isAuthenticated(inputs.sessionKey)) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
      }
      Member member = userSession.getMember();
      resp.firstName = member.getFirstName();
      resp.isAdmin = member.getType() == MemberType.ADMIN;
      return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/new-member", method = RequestMethod.POST)
    ResponseEntity<CreateAccountResponse> createAccountHandler(HttpServletRequest request, @RequestBody CreateAccountRequest inputs) {
      CreateAccountResponse resp = new CreateAccountResponse();
      if (isUsernameTaken(inputs.username)) {
        resp.success = false;
        resp.message = "This username is already taken. Please choose another one.";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
      }
      Member member = new Member(inputs.username, inputs.firstName, inputs.lastName, inputs.phoneNumber, inputs.emailAddress, inputs.hashedPassword, MemberType.USER);
      // TODO: Register the member with the organization here, once organizations are a thing
      member.setHashedPassword(inputs.hashedPassword);
      SQLLinker.getInstance().addUser(member);
      Session userSession = SessionManager.createNewSession(inputs.username, inputs.hashedPassword);
      resp.name = member.getName();
      resp.type = member.getType().toString().toLowerCase();
      resp.sessionKey = userSession.getSessionKey().toString();
      resp.message = "Account successfully created.";
      resp.success = true;
      return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/load-members", method = RequestMethod.POST)
    ResponseEntity<LoadMembersResponse> loadMembersHandler(HttpServletRequest request, @RequestBody LoadMembersRequest inputs) {
      LoadMembersResponse resp = new LoadMembersResponse();
      Session userSession = SessionManager.getSession(UUID.fromString(inputs.sessionKey));
      // If the user's session key is invalid
      if (userSession == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
      }
      Member adminAccount = userSession.getMember();
      // TODO: Uncomment this later, when we have account types
      // if (adminAccount.getType() != MemberType.ADMIN) {
      //   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
      // }
      SQLLinker linker = SQLLinker.getInstance();
      ArrayList<Member> memberList = linker.getMembers();
      SimpleMember[] simpleMemberList = new SimpleMember[memberList.size()];
      for (int i = 0; i < memberList.size(); i++) {
        SimpleMember simpMem = new SimpleMember();
        simpMem.firstName = memberList.get(i).firstName;
        simpMem.lastName = memberList.get(i).lastName;
        simpMem.email = memberList.get(i).email;
        simpMem.id = memberList.get(i).getUuid().toString();
        simpleMemberList[i] = simpMem;
      }
      resp.memberList = simpleMemberList;
      // If we're at this point, the session key is a valid admin's session key
      return ResponseEntity.ok(resp);
    }

    public boolean isLoginCorrect(String username, String hashedPassword) {
      // TODO: Verify the username/password here
      return !username.equals("wrongusername");
    }

    public boolean isUsernameTaken(String username) {
      Member member = SQLLinker.getInstance().getMember(username);
      return member != null;
    }

    public static class SimpleMember {
      @JsonProperty("id")
      String id;
      @JsonProperty("first_name")
      String firstName;
      @JsonProperty("last_name")
      String lastName;
      @JsonProperty("email")
      String email;
    }
}