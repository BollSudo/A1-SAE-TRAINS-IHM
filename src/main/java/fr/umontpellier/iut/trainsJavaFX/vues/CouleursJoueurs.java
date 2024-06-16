package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.CouleurJoueur;

import java.util.Map;

public class CouleursJoueurs {
    public static Map<CouleurJoueur, String> couleursBackgroundJoueur = Map.of(
            CouleurJoueur.JAUNE, "#FED440",
            CouleurJoueur.ROUGE, "#bc2d2d",
            CouleurJoueur.BLEU, "#4093B6",
            CouleurJoueur.VERT, "#2dbc30"
    );

    public static String getNomCouleurPratique(IJoueur joueur) {
        switch (joueur.getCouleur()) {
            case BLEU -> {return "blue";}
            case VERT -> {return "green";}
            case ROUGE -> {return "red";}
            case JAUNE -> {return "yellow";}
            default -> {return "";}
        }
    }

}