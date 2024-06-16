package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private static VueCarte zoneAffichage;

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
        Image image = new Image(nomCarteValide(carte.getNom()));
        setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
    }

    public String nomCarteValide(String carte){
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

    private final EventHandler<MouseEvent> handlerCartesMain = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().joueurCourantProperty().get().uneCarteDeLaMainAEteChoisie(carte.getNom());
            System.out.println("La carte" + carte.getNom() + " a été choisie");
            if (carte.getNom().equals("Feu de signalisation")) {
                devoilerCartePioche();
            }
        }
    };

    private final EventHandler<MouseEvent> handlerCartesReserve = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().uneCarteDeLaReserveEstAchetee(carte.getNom());
            System.out.println("ACHAT: " + carte.getNom());
        }
    };

    private final EventHandler<MouseEvent> handlerCarteEnJeu = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().joueurCourantProperty().get().uneCarteEnJeuAEteChoisie(carte.getNom());
            System.out.println("La carte en JEU choisie : " + carte.getNom());
        }
    };



    //GETTERS ===============================================

    public EventHandler<MouseEvent> getHandlerCartesMain() {
        return handlerCartesMain;
    }

    public EventHandler<MouseEvent> getHandlerCartesReserve() { return handlerCartesReserve; }

    public EventHandler<MouseEvent> getHandlerCarteEnJeu() {
        return handlerCarteEnJeu;
    }

    public ICarte getCarte() {
        return carte;
    }

    //SETTERS ================================================

    public void setCarteChoisieListener(EventHandler<MouseEvent> quandCarteEstChoisie) {
        setOnMouseClicked(quandCarteEstChoisie);
    }

    public void setCarteHover(double scaleZoom, int viewOrderOnHover) {
        setOnMouseEntered((event) -> {
            setScaleX(scaleZoom);
            setScaleY(scaleZoom);
            setViewOrder(viewOrderOnHover);
            creerApercuZoomCarte();
            zoneAffichage.setViewOrder(-1);
        });
        setOnMouseExited((event) -> {
            setScaleX(1);
            setScaleY(1);
            setViewOrder(0);
            zoneAffichage.setBackground(null);
            zoneAffichage.setViewOrder(99);
        });
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

    private void creerApercuZoomCarte() {
        zoneAffichage.setBackground(new Background(
                new BackgroundImage(new Image(nomCarteValide(carte.getNom())),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize(1, 1, true, true, false,false))));
    }

    public static void creerZoneAffichageZoom(StackPane pane) {
        VueCarte v = new VueCarte(GestionJeu.getJeu().getReserve().get(0));
        v.scale(2.5);
        v.createBindingsRatio();
        v.setBackground(null);
        zoneAffichage = v;
        pane.getChildren().add(zoneAffichage);
        StackPane.setAlignment(zoneAffichage, Pos.TOP_LEFT);
        StackPane.setMargin(zoneAffichage, new Insets(10));
    }

    private static void devoilerCartePioche() {
        ListeDeCartes pioche = GestionJeu.getJeu().joueurCourantProperty().get().piocheProperty();
        if (!pioche.isEmpty()) {
            VueCarte v = new VueCarte(pioche.get(0));
            v.createBindingsRatio();
            v.setCarteHover(1.0, 0);
            VueAutresJoueurs.addZoneCarteDevoilee(v);
        }
    }

}
