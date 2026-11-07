package com.tp.reconnaisseur.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ValiderRequest(
        @NotNull(message = "L'alphabet est obligatoire.") List<String> alphabet,
        @NotNull(message = "L'expression régulière est obligatoire.") String regex) {
}
