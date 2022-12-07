package com.capstone.lifesabit.gateguard.login;

import java.util.UUID;

public class Session {
  long duration;
  long startTime;
  long endTime;
  UUID sessionKey;
  Member member;

  public Session(long duration, Member member) {
    this.duration = duration;
    this.startTime = System.currentTimeMillis();
    this.endTime = startTime + duration;
    this.sessionKey = UUID.randomUUID();
    this.member = member;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() >= (this.endTime + this.duration);
  }

  public long getStartTime() {
    return this.startTime;
  }

  public long getEndTime() {
    return this.endTime;
  }

  public long getDuration() {
    return this.duration;
  }

  public UUID getSessionKey() {
    return this.sessionKey;
  }

  public Member getMember() {
    return this.member;
  }
}
