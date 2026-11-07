package com.tp.reconnaisseur.moteur;

import java.util.*;

/** Simulation d'un AFD sur un mot, avec trace du chemin (configurations successives). */
public final class Simulateur {

    public record Configuration(int etat, String reste) {
    }

    public record Resultat(boolean accepte, List<Configuration> chemin,
                           List<Transition> transitions, String message) {
    }

    private Simulateur() {
    }

    public static Resultat reconnaitre(Automate afd, String mot) {
        String m = mot == null ? "" : mot;
        List<Configuration> chemin = new ArrayList<>();
        List<Transition> empruntees = new ArrayList<>();
        int etat = afd.initial();
        chemin.add(new Configuration(etat, m));

        for (int i = 0; i < m.length(); i++) {
            char c = m.charAt(i);
            Transition suivante = null;
            for (Transition t : afd.transitions()) {
                if (t.de() == etat && t.symbole() == c) {
                    suivante = t;
                    break;
                }
            }
            if (suivante == null) {
                return new Resultat(false, chemin, empruntees,
                        "Aucune transition depuis q" + etat + " sur « " + c
                                + " » : le mot n'appartient pas au langage.");
            }
            etat = suivante.vers();
            empruntees.add(suivante);
            chemin.add(new Configuration(etat, m.substring(i + 1)));
        }
        if (afd.finaux().contains(etat)) {
            return new Resultat(true, chemin, empruntees,
                    "q" + etat + " est un état final : le mot appartient au langage.");
        }
        return new Resultat(false, chemin, empruntees,
                "Le mot est entièrement lu mais q" + etat + " n'est pas un état final : le mot n'appartient pas au langage.");
    }
}
