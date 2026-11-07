package com.tp.reconnaisseur.moteur;

import java.util.*;

/**
 * Minimisation d'un AFD partiel par raffinement de partition (algorithme de Moore).
 * Un état puits implicite est utilisé pendant le calcul puis supprimé du résultat.
 */
public final class Minimiseur {

    private Minimiseur() {
    }

    public static Automate minimiser(Automate afd, Set<Character> alphabet) {
        int n = afd.nbEtats();
        int puits = n; // état puits implicite
        List<Character> symboles = new ArrayList<>(new TreeSet<>(alphabet));

        int[][] delta = new int[n + 1][symboles.size()];
        for (int[] ligne : delta) {
            Arrays.fill(ligne, puits);
        }
        for (Transition t : afd.transitions()) {
            delta[t.de()][symboles.indexOf(t.symbole())] = t.vers();
        }

        int[] classe = new int[n + 1];
        for (int e = 0; e < n; e++) {
            classe[e] = afd.finaux().contains(e) ? 1 : 0;
        }
        classe[puits] = 0;

        int nbClasses = comptage(classe);
        while (true) {
            Map<List<Integer>, Integer> signatures = new HashMap<>();
            int[] nouvelle = new int[n + 1];
            for (int e = 0; e <= n; e++) {
                List<Integer> sig = new ArrayList<>();
                sig.add(classe[e]);
                for (int k = 0; k < symboles.size(); k++) {
                    sig.add(classe[delta[e][k]]);
                }
                nouvelle[e] = signatures.computeIfAbsent(sig, s -> signatures.size());
            }
            int nb = signatures.size();
            classe = nouvelle;
            if (nb == nbClasses) {
                break;
            }
            nbClasses = nb;
        }

        int classePuits = classe[puits];
        if (classe[afd.initial()] == classePuits) {
            // langage vide : un seul état, non final
            return new Automate(1, 0, new TreeSet<>(), new ArrayList<>());
        }

        Map<Integer, Integer> numero = new HashMap<>();
        for (int e = 0; e < n; e++) {
            if (classe[e] != classePuits && !numero.containsKey(classe[e])) {
                numero.put(classe[e], numero.size());
            }
        }
        Set<Transition> transitions = new LinkedHashSet<>();
        Set<Integer> finaux = new TreeSet<>();
        for (int e = 0; e < n; e++) {
            if (classe[e] == classePuits) {
                continue;
            }
            int de = numero.get(classe[e]);
            if (afd.finaux().contains(e)) {
                finaux.add(de);
            }
            for (int k = 0; k < symboles.size(); k++) {
                int cible = delta[e][k];
                if (classe[cible] != classePuits) {
                    transitions.add(new Transition(de, symboles.get(k), numero.get(classe[cible])));
                }
            }
        }
        return new Automate(numero.size(), numero.get(classe[afd.initial()]), finaux,
                new ArrayList<>(transitions)).renumeroteParParcours();
    }

    private static int comptage(int[] classe) {
        return (int) Arrays.stream(classe).distinct().count();
    }
}
