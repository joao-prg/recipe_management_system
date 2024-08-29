package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.recipes.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path="/register", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody @Valid final UserCreate userCreate) {
        log.info(String.format("Creating user [e-mail: %s]", userCreate.getEmail()));
        userService.create(userCreate);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

