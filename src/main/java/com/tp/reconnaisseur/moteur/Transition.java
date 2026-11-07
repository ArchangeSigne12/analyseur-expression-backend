package com.tp.reconnaisseur.moteur;

/** Transition d'un automate. Une ε-transition utilise le symbole {@link #EPSILON}. */
public record Transition(int de, char symbole, int vers) {
    public static final char EPSILON = 'ε';

    public boolean estEpsilon() {
        return symbole == EPSILON;
    }
}
