package com.perfstor.perfstor.controller;

import com.perfstor.perfstor.model.Run;
import com.perfstor.perfstor.repository.RunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/runs")
public class RunController {
  private final RunRepository runRepository;

  public RunController(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  @GetMapping
  public String listRuns(Model model) {
    model.addAttribute("runs", runRepository.findAll());
    return "runs/list";
  }

  @GetMapping("/new")
  public String newRunForm(Model model) {
    model.addAttribute("run", new Run());
    return "runs/form";
  }

  @PostMapping
  public String saveRun(@ModelAttribute Run run) {
    runRepository.save(run);
    return "redirect:/runs";
  }

  @GetMapping("/{id}/edit")
  public String editRunForm(@PathVariable Long id, Model model) {
    model.addAttribute("run", runRepository.findById(id).orElseThrow());
    return "runs/form";
  }

  @PostMapping("/{id}")
  public String updateRun(@PathVariable Long id, @ModelAttribute Run run) {
    run.setId(id);
    runRepository.save(run);
    return "redirect:/runs";
  }

  @PostMapping("/{id}/delete")
  public String deleteRun(@PathVariable Long id) {
    runRepository.deleteById(id);
    return "redirect:/runs";
  }

  @GetMapping("/{id}/report")
  public String runReport(@PathVariable Long id, Model model) {
      Run run = runRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      model.addAttribute("run", run);
      return "runs/report";
  }
}
