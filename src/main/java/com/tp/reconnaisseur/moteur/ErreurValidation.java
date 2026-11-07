package com.tp.reconnaisseur.moteur;

/** Erreur de saisie. {@code position} est 1-based (null si non applicable). */
public record ErreurValidation(String champ, String message, Integer position) {
}
