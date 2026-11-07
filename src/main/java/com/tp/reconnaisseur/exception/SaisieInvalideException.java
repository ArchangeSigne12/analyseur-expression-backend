package com.tp.reconnaisseur.exception;

import com.tp.reconnaisseur.moteur.ErreurValidation;
import java.util.List;

public class SaisieInvalideException extends RuntimeException {

    private final transient List<ErreurValidation> erreurs;

    public SaisieInvalideException(List<ErreurValidation> erreurs) {
        super("Saisie invalide");
        this.erreurs = erreurs;
    }

    public List<ErreurValidation> getErreurs() {
        return erreurs;
    }
}
