package org.holovin.privatbank_demo.infra.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UtilController {

    @GetMapping("/uuid")
    public ResponseEntity<String> generateUuid() {
        var uuid = UUID.randomUUID().toString();
        return ResponseEntity.ok(uuid);
    }
}
