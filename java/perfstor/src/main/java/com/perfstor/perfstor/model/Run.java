package com.perfstor.perfstor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Run {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public LocalDateTime getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(LocalDateTime timeStart) {
    this.timeStart = timeStart;
  }

  public LocalDateTime getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(LocalDateTime timeEnd) {
    this.timeEnd = timeEnd;
  }

  public Double getDuration() {
    return duration;
  }

  public void setDuration(Double duration) {
    this.duration = duration;
  }

  private String testName;
  private LocalDateTime timeStart;
  private LocalDateTime timeEnd;
  private Double duration;
}
