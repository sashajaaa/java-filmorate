package ru.yandex.practicum.filmorate.controller;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class DirectorController {

    public final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping("/directors")
    public Director addDirector(@Valid @RequestBody Director director){
        log.info("Запрос на добавления режиссера {}",director);
        return directorService.addDirector(director);
    }

    @GetMapping("/directors")
    public List<Director> getAllDirectors(){
        log.info("Запрошен список всех режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id){
        log.info("Запрошен режиссер id {}",id);
        return directorService.getDirectorById(id);
    }

    @PutMapping("/directors")
    public Director updateDirector(@Valid @RequestBody Director director){
        log.info("Запрошено обновление режиссера {}",director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors")
    public void deleteAllDirectors(){
        log.info("Запрошено удаление всех режиссеров");
        directorService.deleteAllDirectors();
    }

    @DeleteMapping("/directors/{id}")
    public Director deleteDirectorById(@PathVariable int id){
        log.info("Запрошено удаление режиссера {}",id);
        return directorService.deleteDirectorById(id);
    }


}
