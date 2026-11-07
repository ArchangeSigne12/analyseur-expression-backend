package com.tp.reconnaisseur.moteur;

import java.util.*;

/** Validation de l'alphabet, de l'expression régulière et du mot. Positions 1-based. */
public final class ValidateurExpression {

    /** Caractères réservés : ils ne peuvent pas faire partie de l'alphabet. */
    public static final String OPERATEURS = "|*+.()";

    private enum Precedent { DEBUT, OUVRANTE, BINAIRE, OPERANDE }

    private ValidateurExpression() {
    }

    private static boolean reserve(char c) {
        return OPERATEURS.indexOf(c) >= 0 || c == Transition.EPSILON || Character.isWhitespace(c);
    }

    public static List<ErreurValidation> validerAlphabet(List<String> alphabet) {
        List<ErreurValidation> erreurs = new ArrayList<>();
        if (alphabet == null || alphabet.isEmpty()) {
            erreurs.add(new ErreurValidation("alphabet", "L'alphabet est vide.", null));
            return erreurs;
        }
        Set<String> vus = new HashSet<>();
        for (String s : alphabet) {
            if (s == null || s.length() != 1) {
                erreurs.add(new ErreurValidation("alphabet",
                        "« " + s + " » n'est pas un symbole unique (un seul caractère attendu).", null));
            } else if (reserve(s.charAt(0))) {
                erreurs.add(new ErreurValidation("alphabet",
                        "Le caractère « " + s + " » est réservé et ne peut pas faire partie de l'alphabet.", null));
            } else if (!vus.add(s)) {
                erreurs.add(new ErreurValidation("alphabet", "Le symbole « " + s + " » est répété.", null));
            }
        }
        return erreurs;
    }

    /** Symboles utilisables (un caractère, non réservé) extraits de la saisie. */
    public static Set<Character> symbolesValides(List<String> alphabet) {
        Set<Character> ensemble = new TreeSet<>();
        if (alphabet != null) {
            for (String s : alphabet) {
                if (s != null && s.length() == 1 && !reserve(s.charAt(0))) {
                    ensemble.add(s.charAt(0));
                }
            }
        }
        return ensemble;
    }

    public static List<ErreurValidation> validerExpression(Set<Character> alphabet, String regex) {
        List<ErreurValidation> erreurs = new ArrayList<>();
        if (regex == null || regex.isEmpty()) {
            erreurs.add(new ErreurValidation("regex", "L'expression régulière est vide.", null));
            return erreurs;
        }
        Deque<Integer> ouvrantes = new ArrayDeque<>();
        Precedent prec = Precedent.DEBUT;
        int posBinaire = 0;
        char opBinaire = ' ';

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            int pos = i + 1;
            if (Character.isWhitespace(c)) {
                erreurs.add(erreur("Les espaces ne sont pas autorisés (position " + pos + ").", pos));
                continue;
            }
            switch (c) {
                case '(' -> {
                    ouvrantes.push(pos);
                    prec = Precedent.OUVRANTE;
                }
                case ')' -> {
                    if (ouvrantes.isEmpty()) {
                        erreurs.add(erreur("La parenthèse fermante en position " + pos
                                + " n'a pas de parenthèse ouvrante.", pos));
                    } else {
                        ouvrantes.pop();
                        if (prec == Precedent.OUVRANTE) {
                            erreurs.add(erreur("Les parenthèses fermées en position " + pos + " sont vides.", pos));
                        } else if (prec == Precedent.BINAIRE) {
                            erreurs.add(erreur("L'opérateur « " + opBinaire + " » en position " + posBinaire
                                    + " n'a pas d'opérande à droite.", posBinaire));
                        }
                    }
                    prec = Precedent.OPERANDE;
                }
                case '|', '.' -> {
                    if (prec != Precedent.OPERANDE) {
                        erreurs.add(erreur("L'opérateur « " + c + " » en position " + pos
                                + " n'a pas d'opérande à gauche.", pos));
                    }
                    prec = Precedent.BINAIRE;
                    posBinaire = pos;
                    opBinaire = c;
                }
                case '*', '+' -> {
                    if (prec != Precedent.OPERANDE) {
                        erreurs.add(erreur("L'opérateur « " + c + " » en position " + pos
                                + " n'a pas d'opérande à gauche.", pos));
                    }
                    prec = Precedent.OPERANDE;
                }
                default -> {
                    if (!alphabet.contains(c)) {
                        erreurs.add(erreur("Le symbole « " + c + " » n'appartient pas à l'alphabet (position "
                                + pos + ").", pos));
                    }
                    prec = Precedent.OPERANDE;
                }
            }
        }
        if (prec == Precedent.BINAIRE) {
            erreurs.add(erreur("L'opérateur « " + opBinaire + " » en position " + posBinaire
                    + " n'a pas d'opérande à droite.", posBinaire));
        }
        for (int pos : ouvrantes) {
            erreurs.add(erreur("La parenthèse ouverte en position " + pos + " n'est jamais fermée.", pos));
        }
        erreurs.sort(Comparator.comparing(ErreurValidation::position));
        return erreurs;
    }

    public static List<ErreurValidation> validerMot(Set<Character> alphabet, String mot) {
        List<ErreurValidation> erreurs = new ArrayList<>();
        if (mot == null) {
            return erreurs;
        }
        for (int i = 0; i < mot.length(); i++) {
            if (!alphabet.contains(mot.charAt(i))) {
                erreurs.add(new ErreurValidation("mot", "Le symbole « " + mot.charAt(i)
                        + " » du mot n'appartient pas à l'alphabet (position " + (i + 1) + ").", i + 1));
            }
        }
        return erreurs;
    }

    private static ErreurValidation erreur(String message, int position) {
        return new ErreurValidation("regex", message, position);
    }
}
