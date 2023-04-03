package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {
    public final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Add director request {}", director);
        return directorService.addDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("All directors list request");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Director requested, id {}", id);
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Update director request {}", director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping
    public void deleteAllDirectors() {
        log.info("Delete all directors request");
        directorService.deleteAllDirectors();
    }

    @DeleteMapping("/{id}")
    public Director deleteDirectorById(@PathVariable int id) {
        log.info("Delete director request {}", id);
        return directorService.deleteDirectorById(id);
    }
}