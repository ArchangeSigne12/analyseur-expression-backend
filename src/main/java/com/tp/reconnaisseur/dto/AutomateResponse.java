package com.tp.reconnaisseur.dto;

/**
 * afn : automate de Thompson (avec ε-transitions) ;
 * afd : résultat de la construction par sous-ensembles ;
 * afdMinimal : AFD minimisé (celui qu'on affiche par défaut et qui sert à la reconnaissance).
 */
public record AutomateResponse(String postfixe, AutomateDto afn, AutomateDto afd, AutomateDto afdMinimal) {
}
