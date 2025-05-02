package com.perfstor.perfstor.api;

import com.perfstor.perfstor.model.Run;
import com.perfstor.perfstor.repository.RunRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
public class RunApiController {

  private final RunRepository runRepository;

  public RunApiController(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  @PostMapping
  public ResponseEntity<Run> createRun(@RequestBody Run run) {
    Run saved = runRepository.save(run);
    return ResponseEntity.ok(saved);
  }
}
