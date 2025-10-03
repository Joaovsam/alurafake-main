package br.com.alura.AluraFake.task;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody TaskDTO req) {
        Task saved = taskService.createOpenText(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody TaskDTO req) {
        Task saved = taskService.createSingleChoice(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@RequestBody TaskDTO req) {
        Task saved = taskService.createMultipleChoice(req);
        return ResponseEntity.ok().build();
    }

}
