package com.tp.reconnaisseur.service;

import com.tp.reconnaisseur.dto.*;
import com.tp.reconnaisseur.exception.SaisieInvalideException;
import com.tp.reconnaisseur.moteur.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Orchestre le moteur : validation → postfixe → Thompson → AFD → minimisation → simulation. */
@Service
public class AutomateService {

    public ValidationResponse valider(List<String> alphabet, String regex) {
        List<ErreurValidation> erreurs = erreurs(alphabet, regex);
        return new ValidationResponse(erreurs.isEmpty(), erreurs);
    }

    public AutomateResponse construire(List<String> alphabet, String regex) {
        verifier(erreurs(alphabet, regex));
        Etapes e = calculer(alphabet, regex);
        return new AutomateResponse(
                e.postfixe.stream().map(String::valueOf).collect(Collectors.joining()),
                AutomateDto.depuis(e.afn), AutomateDto.depuis(e.afd), AutomateDto.depuis(e.minimal));
    }

    public ReconnaissanceResponse reconnaitre(List<String> alphabet, String regex, String mot) {
        String m = mot == null ? "" : mot;
        List<ErreurValidation> erreurs = erreurs(alphabet, regex);
        erreurs.addAll(ValidateurExpression.validerMot(ValidateurExpression.symbolesValides(alphabet), m));
        verifier(erreurs);

        Etapes e = calculer(alphabet, regex);
        Simulateur.Resultat r = Simulateur.reconnaitre(e.minimal, m);
        return new ReconnaissanceResponse(
                r.accepte(), r.message(), AutomateDto.depuis(e.minimal),
                r.chemin().stream().map(ConfigurationDto::depuis).toList(),
                r.transitions().stream().map(TransitionDto::depuis).toList());
    }

    // ---- interne ----

    private record Etapes(List<Character> postfixe, Automate afn, Automate afd, Automate minimal) {
    }

    private Etapes calculer(List<String> alphabet, String regex) {
        Set<Character> sigma = ValidateurExpression.symbolesValides(alphabet);
        List<Character> postfixe = ConvertisseurPostfixe.versPostfixe(regex);
        Automate afn = ConstructeurThompson.construire(postfixe);
        Automate afd = ConstructeurAfd.depuis(afn, sigma);
        Automate minimal = Minimiseur.minimiser(afd, sigma);
        return new Etapes(postfixe, afn, afd, minimal);
    }

    private List<ErreurValidation> erreurs(List<String> alphabet, String regex) {
        List<ErreurValidation> erreurs = new ArrayList<>(ValidateurExpression.validerAlphabet(alphabet));
        erreurs.addAll(ValidateurExpression.validerExpression(ValidateurExpression.symbolesValides(alphabet), regex));
        return erreurs;
    }

    private void verifier(List<ErreurValidation> erreurs) {
        if (!erreurs.isEmpty()) {
            throw new SaisieInvalideException(erreurs);
        }
    }
}
