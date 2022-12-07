package com.capstone.lifesabit.gateguard.passes;

import java.util.Date;
import java.util.UUID;

public class Pass {
    UUID passID;
    String firstName;
    String lastName;
    UUID userID;
    String email;
    Long expirationDate;
    boolean expired;
    int usesTotal;
    int usesLeft;
    boolean usageBased;

    public Pass(UUID passID, String firstName, String lastName, String email, UUID userID, Long expirationDate) {
        this.passID = passID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
        this.expirationDate = expirationDate;
        usageBased = false;
        expired = this.isExpired();
    }

    public Pass(String firstName, String lastName, String email, UUID userID, Long expirationDate) {
        passID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
        this.expirationDate = expirationDate;
        usageBased = false;
        expired = this.isExpired();
    }

    public Pass(UUID passID, String firstName, String lastName, String email, UUID userID, int usesLeft,
            int usesTotal) {
        this.passID = passID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
        this.usesLeft = usesLeft;
        this.usesTotal = usesTotal;
        usageBased = true;
        expired = this.isExpired();
    }

    public Pass(String firstName, String lastName, String email, UUID userID, int usesTotal) {
        passID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
        this.usesLeft = usesTotal;
        this.usesTotal = usesTotal;
        usageBased = true;
        expired = this.isExpired();
    }

    public boolean isExpired() {
        boolean ret = true;
        Date now = new Date();
        Long nowAsLong = now.getTime();
        if (!usageBased && expirationDate.compareTo(nowAsLong) >= 0) {
            ret = false;
        } else if (usageBased && usesLeft != 0) {
            ret = false;
        }
        return ret;
    }

    /**
     * "Uses" a pass, exhausting one
     * instance of its uses (if usage-based)
     * @return True if pass was successfully used, false otherwise
     */
    public boolean use() {
        if (usageBased && usesLeft > 0) {
            this.usesLeft--;
            return true;
        } else if (!usageBased) {
            return !isExpired();
        }
        return false;
    }

    public UUID getPassID() {
        return passID;
    }

    public void setPassID(UUID passID) {
        this.passID = passID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean getExpired() {
        expired = this.isExpired();
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void setUsesLeft(int usesLeft) {
        if (usesLeft < 0 || usesLeft > usesTotal) {
            return;
        }
        this.usesLeft = usesLeft;
    }

    public int getUsesTotal() {
        return usesTotal;
    }

    public void setUsesTotal(int usesTotal) {
        this.usesTotal = usesTotal;
    }

    public boolean getUsageBased() {
        return usageBased;
    }

    public void setUsageBased(boolean usageBased) {
        this.usageBased = usageBased;
    }

    public String toString() {
        String ret = "";
        ret += "Pass ID: " + passID + "\n";
        ret += "User ID: " + userID + "\n";
        ret += "Name: " + firstName + " " + lastName + "\n";
        ret += "Email: " + email + "\n";
        ret += "Expired: " + expired + "\n";

        if (!usageBased) {
            ret += "Expiration Date: " + expirationDate + "\n";
        } else if (usageBased) {
            ret += "Uses Used: " + usesLeft + "\n";
            ret += "Uses Total: " + usesTotal + "\n";
        }

        return ret;
    }

}
