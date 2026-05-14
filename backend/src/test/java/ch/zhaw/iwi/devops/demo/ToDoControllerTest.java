package ch.zhaw.iwi.devops.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ToDoControllerTest {

    private ToDoController controller;

    @BeforeEach
    void setUp() {
        controller = new ToDoController();
        controller.init();
    }

    @Test
    void testEndpointReturnsAppText() {
        String result = controller.test();

        assertTrue(result.contains("app"));
    }

    @Test
    void countReturnsInitialNumberOfTodos() {
        int result = controller.count();

        assertEquals(5, result);
    }

    @Test
    void getAllTodosReturnsInitialTodos() {
        List<ToDo> result = controller.getAllTodos();

        assertEquals(5, result.size());
    }

    @Test
    void createTodoAddsNewTodo() {
        ToDo todo = new ToDo(0, "Test Todo", "Test Description");

        controller.createTodo(todo);

        assertEquals(6, controller.count());
    }

    @Test
    void deleteTodoRemovesTodo() {
        ToDo deletedTodo = controller.deleteTodo(1);

        assertEquals(4, controller.count());
        assertEquals("Neuer Job", deletedTodo.getTitle());
    }

    @Test
    void deleteAllTodosRemovesEverything() {
        String result = controller.deleteAllTodos();

        assertEquals("All todos deleted", result);
        assertEquals(0, controller.count());
    }

    @Test
    void resetTodosRestoresInitialData() {
        controller.deleteAllTodos();

        String result = controller.resetTodos();

        assertEquals("Init data reset complete", result);
        assertEquals(5, controller.count());
    }
}