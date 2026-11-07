package com.tp.reconnaisseur.dto;

import com.tp.reconnaisseur.moteur.Simulateur;

/** Configuration (état, reste du mot). Un reste vide se lit ε côté interface. */
public record ConfigurationDto(String etat, String reste) {

    public static ConfigurationDto depuis(Simulateur.Configuration c) {
        return new ConfigurationDto("q" + c.etat(), c.reste());
    }
}
