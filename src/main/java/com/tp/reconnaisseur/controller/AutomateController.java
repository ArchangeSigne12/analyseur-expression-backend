package com.tp.reconnaisseur.controller;

import com.tp.reconnaisseur.dto.*;
import com.tp.reconnaisseur.service.AutomateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AutomateController {

    private final AutomateService service;

    public AutomateController(AutomateService service) {
        this.service = service;
    }

    @PostMapping("/valider")
    public ValidationResponse valider(@Valid @RequestBody ValiderRequest requete) {
        return service.valider(requete.alphabet(), requete.regex());
    }

   
    @PostMapping("/automate")
    public AutomateResponse automate(@Valid @RequestBody ValiderRequest requete) {
        return service.construire(requete.alphabet(), requete.regex());
    }

   
    @PostMapping("/reconnaitre")
    public ReconnaissanceResponse reconnaitre(@Valid @RequestBody ReconnaitreRequest requete) {
        return service.reconnaitre(requete.alphabet(), requete.regex(), requete.mot());
    }
}
