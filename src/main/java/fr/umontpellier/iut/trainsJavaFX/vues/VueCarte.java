package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.Normalizer;

/**
 * Cette classe représente la vue d'une carte.
 * <p>
 * On y définit le listener à exécuter lorsque cette carte a été choisie par l'utilisateur
 */
public class VueCarte extends StackPane {
    private final ICarte carte;

    public static double LONGUEUR_INIT = VueDuJeu.LONGUEUR_ECRAN * 113.75 / 1280.0;
    public static double HAUTEUR_INIT = VueDuJeu.HAUTEUR_ECRAN * 151.25 / 800.0;

    public VueCarte(ICarte carte) {
        this.carte = carte;
        //Aspact ratoi 2:3

        //base 375x525
        //HBox.setHgrow(this, Priority.ALWAYS);
        setMinWidth(LONGUEUR_INIT);
        setMinHeight(HAUTEUR_INIT);
        setMaxWidth(LONGUEUR_INIT);
        setMaxHeight(HAUTEUR_INIT);


        //setScaleX(1.1);
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
        setMaxHeight(getMaxHeight()*scaleFactor);
        setMaxWidth(getMaxWidth()*scaleFactor);
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
            GestionJeu.getJeu().uneCarteDeLaReserveEstAchetee(carte.getNom());
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


    //BINDINGS RATIO

    public void createBindingsRatio() {
        DoubleProperty b = VueDuJeu.ratioResolutionFenetreProperty();
        minWidthProperty().bind(Bindings.when(b.greaterThan(0.4))
                        .then(b.multiply(getMinWidth()))
                        .otherwise(getMinWidth() * 0.4)
                );
        maxWidthProperty().bind(Bindings.when(b.greaterThan(0.4))
                .then(b.multiply(getMinWidth()))
                .otherwise(getMinWidth() * 0.4)
        );
        minHeightProperty().bind(Bindings.when(b.greaterThan(0.4))
                .then(b.multiply(getMinHeight()))
                .otherwise(getMinHeight() * 0.4)
        );
        maxHeightProperty().bind(Bindings.when(b.greaterThan(0.4))
                .then(b.multiply(getMinHeight()))
                .otherwise(getMinHeight() * 0.4)
        );
    }

    public void creerLabelPileReserve() {
        Label indicateur = new Label("0");
        indicateur.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        indicateur.setTextFill(Color.YELLOW);
        indicateur.textProperty().bind(GestionJeu.getJeu().getTaillesPilesReserveProperties().get(carte.getNom()).asString());
        getChildren().add(indicateur);
    }
}
