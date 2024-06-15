package fr.umontpellier.iut.trainsJavaFX;

import fr.umontpellier.iut.trainsJavaFX.mecanique.CouleurJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.property.IntegerProperty;

public interface IJoueur {

    ListeDeCartes mainProperty();
    ListeDeCartes defausseProperty();
    ListeDeCartes piocheProperty();
    ListeDeCartes cartesEnJeuProperty();
    ListeDeCartes cartesRecuesProperty();
    ListeDeCartes cartesAChoisir();
    IntegerProperty argentProperty();
    IntegerProperty nbJetonsRailsProperty();
    IntegerProperty pointsRailsProperty();
    IntegerProperty scoreProperty();

    String getNom();
    CouleurJoueur getCouleur();
    int getScoreTotal();

    void uneCarteDeLaMainAEteChoisie(String carteEnMain);
    void uneCarteEnJeuAEteChoisie(String carteEnMain);
    void laPiocheAEteChoisie();
    void laDefausseAEteChoisie();
    void recevoirArgentAEteChoisi();

}