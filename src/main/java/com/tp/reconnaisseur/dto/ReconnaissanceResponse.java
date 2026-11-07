package com.tp.reconnaisseur.dto;

import java.util.List;

public record ReconnaissanceResponse(boolean accepte, String message, AutomateDto afd,
                                     List<ConfigurationDto> chemin,
                                     List<TransitionDto> transitionsEmpruntees) {
}
