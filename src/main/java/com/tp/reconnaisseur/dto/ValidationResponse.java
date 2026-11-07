package com.tp.reconnaisseur.dto;

import com.tp.reconnaisseur.moteur.ErreurValidation;
import java.util.List;

public record ValidationResponse(boolean valide, List<ErreurValidation> erreurs) {
}
