package com.tp.reconnaisseur.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** {@code mot} peut être vide ou absent : c'est alors le mot vide ε. */
public record ReconnaitreRequest(
        @NotNull(message = "L'alphabet est obligatoire.") List<String> alphabet,
        @NotNull(message = "L'expression régulière est obligatoire.") String regex,
        String mot) {
}
