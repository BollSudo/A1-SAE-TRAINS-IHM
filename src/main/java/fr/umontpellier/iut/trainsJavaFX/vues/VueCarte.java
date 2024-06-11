package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.util.Random;

/**
 * Cette classe représente la vue d'une carte.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueCarte extends StackPane {

    private final ICarte carte;


    public VueCarte(ICarte carte) {
        this.carte = carte;
        //Aspact ratoi 2:3

        //HBox.setHgrow(this, Priority.ALWAYS);
        setPrefHeight(100);
        setPrefWidth(90);

        setScaleX(1.1);
        setOnMouseEntered((event) -> setScaleY(1.2));
        setOnMouseExited((event) -> setScaleY(1));

        setOnMouseClicked(((mouseEvent) -> {
            GestionJeu.getJeu().joueurCourantProperty().get().uneCarteDeLaMainAEteChoisie(carte.getNom());
            System.out.println("La carte" + carte.getNom() + " a été choisie");
        }));
        int test = new Random().nextInt(100000, 999999);
        setStyle("-fx-background-color:#"+Integer.toString(test));
    }

    public void setCarteChoisieListener(EventHandler<MouseEvent> quandCarteEstChoisie) {
        setOnMouseClicked(quandCarteEstChoisie);
    }

    public ICarte getCarte() {
        return carte;
    }

}
