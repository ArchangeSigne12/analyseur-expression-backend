package com.tp.reconnaisseur.dto;

import com.tp.reconnaisseur.moteur.Automate;
import java.util.List;
import java.util.stream.IntStream;

public record AutomateDto(List<String> etats, String initial, List<String> finaux,
                          List<TransitionDto> transitions) {

    public static AutomateDto depuis(Automate a) {
        List<String> etats = IntStream.range(0, a.nbEtats()).mapToObj(i -> "q" + i).toList();
        List<String> finaux = a.finaux().stream().sorted().map(i -> "q" + i).toList();
        List<TransitionDto> transitions = a.transitions().stream().map(TransitionDto::depuis).toList();
        return new AutomateDto(etats, "q" + a.initial(), finaux, transitions);
    }
}
