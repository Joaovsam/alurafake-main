package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class InstructorController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public InstructorController(UserRepository userRepository,
            CourseRepository courseRepository,
            TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/instructor/{id}/courses")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (user.getRole() != Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Usuário não é instrutor"));
        }

        List<Course> courses = courseRepository.findByInstructorId(id);
        List<Map<String, Object>> list = new ArrayList<>();

        long totalPublished = 0;
        for (Course c : courses) {
            List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(c);
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("title", c.getTitle());
            item.put("status", c.getStatus());
            item.put("publishedAt", c.getPublishedAt());
            item.put("activitiesCount", tasks.size());
            list.add(item);
            if (c.getStatus() == Status.PUBLISHED) {
                totalPublished++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("courses", list);
        response.put("totalPublished", totalPublished);

        return ResponseEntity.ok(response);
    }
}
