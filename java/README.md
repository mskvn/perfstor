# Perfstor — java version

---

## Скачивание и распаковка проекта

Скачайте и распакуйте скелет проекта:

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.3.0 \
  -d baseDir=perfstor \
  -d name=perfstor \
  -d groupId=com.perfstor \
  -d artifactId=perfstor \
  -d description="Spring MVC CRUD app for Run entity" \
  -d packageName=com.perfstor.perfstor \
  -d dependencies=web,data-jpa,mysql,h2,thymeleaf,devtools \
  -o perfstor.zip

unzip perfstor.zip -d perfstor
cd perfstor
```

---

## Конфигурация БД

### `application-dev.properties` (H2):

```properties
spring.datasource.url=jdbc:h2:file:./data/perfstor
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

---

## Модель `Run`

`src/main/java/com/perfstor/perfstor/model/Run.java`:

```java
package com.perfstor.perfstor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Run {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testName;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private Double duration;

    // Getters and setters
}
```

---

## Репозиторий

`src/main/java/com/perfstor/perfstor/repository/RunRepository.java`:

```java
package com.perfstor.perfstor.repository;

import com.perfstor.perfstor.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<Run, Long> {
}
```

---

## CRUD-контроллер (UI)

`src/main/java/com/perfstor/perfstor/controller/HomeController.java`:

```java
package com.perfstor.perfstor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping("/")
  public String home() {
    return "redirect:/runs";
  }
}

```

`src/main/java/com/perfstor/perfstor/controller/RunController.java`:

```java
package com.perfstor.perfstor.controller;

import com.perfstor.perfstor.model.Run;
import com.perfstor.perfstor.repository.RunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
```

---

## Шаблоны (Thymeleaf + Bootstrap)

Создайте папку `src/main/resources/templates/runs/` и добавьте два файла:

### `list.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><title>Runs</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/></head>
<body class="p-4">
<h1>Runs</h1>
<a class="btn btn-primary mb-3" th:href="@{/runs/new}">New Run</a>
<table class="table table-bordered">
<thead><tr><th>ID</th><th>Test Name</th><th>Start</th><th>End</th><th>Duration</th><th>Actions</th></tr></thead>
<tbody>
<tr th:each="run : ${runs}">
    <td th:text="${run.id}"></td>
    <td th:text="${run.testName}"></td>
    <td th:text="${run.timeStart}"></td>
    <td th:text="${run.timeEnd}"></td>
    <td th:text="${run.duration}"></td>
    <td>
        <a th:href="@{'/runs/' + ${run.id} + '/edit'}" class="btn btn-sm btn-warning">Edit</a>
        <form th:action="@{'/runs/' + ${run.id} + '/delete'}" method="post" style="display:inline">
            <button class="btn btn-sm btn-danger">Delete</button>
        </form>
    </td>
</tr>
</tbody>
</table>
</body>
</html>
```

### `form.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <title>Run Form</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="p-4">
<h1 th:if="${run.id != null}">Edit Run</h1>
<h1 th:if="${run.id == null}">New Run</h1>

<form th:action="@{${run.id} != null ? '/runs/' + ${run.id} : '/runs'}" method="post" th:object="${run}">
  <div class="mb-3">
    <label>Test Name</label>
    <input class="form-control" type="text" th:field="*{testName}" />
  </div>
  <div class="mb-3">
    <label>Start Time</label>
    <input class="form-control" type="datetime-local" th:field="*{timeStart}" />
  </div>
  <div class="mb-3">
    <label>End Time</label>
    <input class="form-control" type="datetime-local" th:field="*{timeEnd}" />
  </div>
  <div class="mb-3">
    <label>Duration (sec)</label>
    <input class="form-control" type="number" step="0.1" th:field="*{duration}" />
  </div>
  <button class="btn btn-success">Save</button>
</form>
</body>
</html>
```

---

## API

`src/main/java/com/perfstor/perfstor/api/RunApiController.java`:

```java
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
```

Тестовый запрос:

```bash
curl -X POST http://localhost:8080/api/run \
-H "Content-Type: application/json" \
-d '{
  "testName": "OpenSiteRoot",
  "timeStart": "2025-04-19T12:00:00",
  "timeEnd": "2025-04-19T12:15:00",
  "duration": 900.0
}'
```

---

Чтобы собрать и запустить:

```bash
./mvnw spring-boot:run
```
