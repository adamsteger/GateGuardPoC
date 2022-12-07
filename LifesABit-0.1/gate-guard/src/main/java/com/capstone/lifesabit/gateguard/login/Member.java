package com.capstone.lifesabit.gateguard.login;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.MessageDigest;

public class Member {
  UUID uuid;
  String username;
  String firstName;
  String lastName;
  String phoneNumber;
  // TODO: Once organizations are a thing
  // Organization organization;
  int orgID;
  String email;
  String saltedHashedPassword;
  MemberType type;
  
  public Member(String username, String firstName, String lastName, String phoneNumber, String email, 
                String hashedPassword, MemberType type) {
    this.firstName = firstName;
    this.username = username;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.type = type;
    this.uuid = UUID.randomUUID();
    // TODO: Once orgs are a thing
    this.orgID = 1;
    // Salt the password
    this.saltedHashedPassword = Member.saltPassword(hashedPassword);
  }

  public Member(UUID uuid, String username, String firstName, String lastName, String phoneNumber, String email, 
                String saltedHashedPassword, MemberType type) {
    this.uuid = uuid;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.type = type;
    // TODO: Once orgs are a thing
    this.orgID = 1;
    this.saltedHashedPassword = saltedHashedPassword;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public int getOrgId() {
    return this.orgID;
  }

  public void setOrgId(int orgID) {
    this.orgID = orgID;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean checkPassword(String hashedPassword) {
    return BCrypt.checkpw(hashedPassword, this.saltedHashedPassword);
  }

  public String getSaltedHashedPassword() {
    return saltedHashedPassword;
  }

  public void setHashedPassword(String hashedPassword) {
    this.saltedHashedPassword = saltPassword(hashedPassword);
  }

  public String getName() {
    return firstName + " " + lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public MemberType getType() {
    return type;
  }

  public void setType(MemberType type) {
    this.type = type;
  }

  /**
   * Salts the member's password
   * @param hashedPassword
   * @return The salted password
   */
  public static String saltPassword(String hashedPassword) {
    return BCrypt.hashpw(hashedPassword, BCrypt.gensalt());
  }

  public static enum MemberType { USER, ADMIN };
}
