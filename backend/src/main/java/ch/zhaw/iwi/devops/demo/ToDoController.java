package ch.zhaw.iwi.devops.demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ToDoController {

    private Map<Integer, ToDo> todos = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        resetInitialData();
        System.out.println("Init Data");
    }

    @GetMapping("/test")
    public String test() {
        return "ToDo app - by gallomor";
    }

    @GetMapping("/services/ping")
    public String ping() {
        String languageCode = "de";
        return "{ \"status\": \"ok\", \"userId\": \"admin"
                + "\", \"languageCode\": \"" + languageCode
                + "\",\"version\": \"0.0.2" + "\"}";
    }

    @GetMapping("/count")
    public int count() {
        return this.todos.size();
    }

    @GetMapping("/services/todo")
    public List<PathListEntry> todo() {
        var result = new ArrayList<PathListEntry>();

        for (var todo : this.todos.values()) {
            var entry = new PathListEntry();
            entry.setKey(todo.getId(), "todoKey");
            entry.setName(todo.getTitle());
            entry.getDetails().add(todo.getDescription());
            entry.setTooltip(todo.getDescription());
            result.add(entry);
        }

        return result.stream()
                .sorted(Comparator.comparing(PathListEntry::getName))
                .toList();
    }

    @GetMapping("/services/todo/{key}")
    public ToDo getTodo(@PathVariable("key") Integer key) {
        return this.todos.get(key);
    }

    @PostMapping("/services/todo")
    public void createTodo(@RequestBody ToDo todo) {
        var newId = this.todos.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;

        todo.setId(newId);
        this.todos.put(newId, todo);
    }

    @PutMapping("/services/todo/{key}")
    public void updateTodo(@PathVariable("key") Integer key, @RequestBody ToDo todo) {
        todo.setId(key);
        this.todos.put(key, todo);
    }

    @DeleteMapping("/services/todo/{key}")
    public ToDo deleteTodo(@PathVariable("key") Integer key) {
        return this.todos.remove(key);
    }

    @GetMapping("/services/todo/all")
    public List<ToDo> getAllTodos() {
        return this.todos.values().stream()
                .sorted(Comparator.comparing(ToDo::getId))
                .toList();
    }

    @GetMapping("/services/todo/search")
    public List<ToDo> searchTodos(@RequestParam("text") String text) {
        String searchText = text.toLowerCase();

        return this.todos.values().stream()
                .filter(todo ->
                        todo.getTitle().toLowerCase().contains(searchText)
                                || todo.getDescription().toLowerCase().contains(searchText)
                )
                .sorted(Comparator.comparing(ToDo::getId))
                .toList();
    }

    @DeleteMapping("/services/todo")
    public String deleteAllTodos() {
        this.todos.clear();
        return "All todos deleted";
    }

    @PostMapping("/services/todo/reset")
    public String resetTodos() {
        resetInitialData();
        return "Init data reset complete";
    }

    private void resetInitialData() {
        this.todos.clear();
        this.todos.put(1, new ToDo(1, "Neuer Job", "5 DevOps Engineers einstellen"));
        this.todos.put(2, new ToDo(2, "Geschäftsleitung", "Mit Präsentation von DevOps überzeugen"));
        this.todos.put(3, new ToDo(3, "Unit Tests", "Neues Projekt mit Unit Tests starten"));
        this.todos.put(4, new ToDo(4, "Deployment", "Jede Woche!"));
        this.todos.put(5, new ToDo(5, "Organigramm", "Löschen"));
    }
}