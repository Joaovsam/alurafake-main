package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCourseOrderByOrderAsc(Course course);

    boolean existsByCourseAndStatement(Course course, String statement);

    List<Task> findByCourseAndOrderGreaterThanEqualOrderByOrderAsc(Course course, Integer order);
}
