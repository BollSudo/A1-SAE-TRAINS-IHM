package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

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
        setMinHeight(100);
        setMinWidth(90);

        setScaleX(1.1);
        setOnMouseEntered((event) -> setScaleY(1.2));
        setOnMouseExited((event) -> setScaleY(1));

        setOnMouseClicked(((mouseEvent) -> {
            GestionJeu.getJeu().joueurCourantProperty().get().uneCarteDeLaMainAEteChoisie(carte.getNom());
            System.out.println("La carte" + carte.getNom() + " a été choisie");
        }));
        Image image = new Image(nomCarteValide(carte.getNom()));
        this.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));


    }

    public void setCarteChoisieListener(EventHandler<MouseEvent> quandCarteEstChoisie) {
        setOnMouseClicked(quandCarteEstChoisie);
    }

    public ICarte getCarte() {
        return carte;
    }

    private String nomCarteValide(String carte){
        System.out.println("images/cartes/"+carte.toLowerCase().replace(" ", "_")+".jpg");
        return "images/cartes/"+carte.toLowerCase().replace(" ", "_")+".jpg";
    }
}
