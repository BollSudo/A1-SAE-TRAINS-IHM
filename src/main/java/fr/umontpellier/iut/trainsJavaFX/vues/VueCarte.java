package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.etatsJoueur.tournormal.AchatCarte;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.text.Normalizer;

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
        setMinHeight(150);
        setMinWidth(90);

        setScaleX(1.1);
        setOnMouseEntered((event) -> setScaleY(1.2));
        setOnMouseExited((event) -> setScaleY(1));

        Image image = new Image(nomCarteValide(carte.getNom()));
        this.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
    }

    private String nomCarteValide(String carte){
        String str = Normalizer.normalize(carte.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        //System.out.println("images/cartes/"+str.replace(" ", "_")+".jpg");
        return "images/cartes/"+str.replace(" ", "_")+".jpg";
    }

    public void scale(double scaleFactor) {
        setMinHeight(getMinHeight()*scaleFactor);
        setMinWidth(getMinWidth()*scaleFactor);
    }

    //HANDLERS ============================================================================

    EventHandler<MouseEvent> handlerCartesMain = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().joueurCourantProperty().get().uneCarteDeLaMainAEteChoisie(carte.getNom());
            System.out.println("La carte" + carte.getNom() + " a été choisie");
        }
    };

    EventHandler<MouseEvent> handlerCartesReserve = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            AchatCarte achatCarte = new AchatCarte((Joueur) GestionJeu.getJeu().joueurCourantProperty().get());
            achatCarte.carteEnReserveChoisie(carte.getNom());
            System.out.println("ACHAT:" + carte.getNom());
        }
    };

    //GETTERS ===============================================

    public EventHandler<MouseEvent> getHandlerCartesMain() {
        return handlerCartesMain;
    }

    public EventHandler<MouseEvent> getHandlerCartesReserve() { return handlerCartesReserve; }

    public ICarte getCarte() {
        return carte;
    }

    //SETTERS ================================================

    public void setCarteChoisieListener(EventHandler<MouseEvent> quandCarteEstChoisie) {
        setOnMouseClicked(quandCarteEstChoisie);
    }
}
