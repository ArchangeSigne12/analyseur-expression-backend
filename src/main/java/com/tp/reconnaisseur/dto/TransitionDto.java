package com.tp.reconnaisseur.dto;

import com.tp.reconnaisseur.moteur.Transition;

/** {@code symbole} vaut « ε » pour une ε-transition. */
public record TransitionDto(String de, String symbole, String vers) {

    public static TransitionDto depuis(Transition t) {
        return new TransitionDto("q" + t.de(), String.valueOf(t.symbole()), "q" + t.vers());
    }
}
