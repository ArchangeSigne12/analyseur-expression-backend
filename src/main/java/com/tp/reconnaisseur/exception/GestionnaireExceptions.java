package com.tp.reconnaisseur.exception;

import com.tp.reconnaisseur.dto.ValidationResponse;
import com.tp.reconnaisseur.moteur.ErreurValidation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GestionnaireExceptions {

    @ExceptionHandler(SaisieInvalideException.class)
    public ResponseEntity<ValidationResponse> saisieInvalide(SaisieInvalideException e) {
        return ResponseEntity.badRequest().body(new ValidationResponse(false, e.getErreurs()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationResponse> champsManquants(MethodArgumentNotValidException e) {
        List<ErreurValidation> erreurs = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErreurValidation(f.getField(), f.getDefaultMessage(), null))
                .toList();
        return ResponseEntity.badRequest().body(new ValidationResponse(false, erreurs));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationResponse> corpsIllisible(HttpMessageNotReadableException e) {
        List<ErreurValidation> erreurs =
                List.of(new ErreurValidation("corps", "Corps de la requête invalide (JSON attendu).", null));
        return ResponseEntity.badRequest().body(new ValidationResponse(false, erreurs));
    }
}
