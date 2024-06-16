package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
    private static VueCarte zoneAffichageCarteDevoilee;


//    private static IntegerProperty nbCartesDevoilesProperty;

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
            switch (carte.getNom()) {
                case "Centre de renseignements" ->  devoilerCartesAChoisir(true);
                case "Feu de signalisation" -> devoilerCartesAChoisir(false);
                case "Centre de contrôle" -> {
                    actionCentreDeControle();

                }

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

    private final EventHandler<MouseEvent> handlerCarteAChoisir = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().uneCarteAChoisirChoisie(carte.getNom());
            zoneAffichageCarteDevoilee.getChildren().remove(trouverVueCartePratique(carte));
            System.out.println("La carte à choisir a été choisie : " + carte.getNom());
        }
    };

    private final EventHandler<MouseEvent> handlerCarteAChoisirSupression = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            GestionJeu.getJeu().uneCarteAChoisirChoisie(carte.getNom());
            zoneAffichageCarteDevoilee.getChildren().remove(trouverVueCartePratique(carte));
            resetCartesDevoilees();
            System.out.println("La carte à choisir a été choisie : " + carte.getNom());
        }
    };

    private VueCarte trouverVueCartePratique(ICarte carte) {
        for (Node n : zoneAffichageCarteDevoilee.getChildren()) {
            if (((VueCarte) n).getCarte().getNom().equals(carte.getNom())) {
                return (VueCarte) n;
            }
        }
        return null;
    }

    //GETTERS ===============================================

    public EventHandler<MouseEvent> getHandlerCartesMain() {
        return handlerCartesMain;
    }

    public EventHandler<MouseEvent> getHandlerCartesReserve() { return handlerCartesReserve; }

    public EventHandler<MouseEvent> getHandlerCarteEnJeu() {
        return handlerCarteEnJeu;
    }
    public EventHandler<MouseEvent> getHandlerCarteAChoisir() {
        return handlerCarteAChoisir;
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
            if (viewOrderOnHover != -99)
                setViewOrder(viewOrderOnHover);
            creerApercuZoomCarte();
            zoneAffichage.setViewOrder(-1);
        });
        setOnMouseExited((event) -> {
            setScaleX(1);
            setScaleY(1);
            if (viewOrderOnHover != -99)
                setViewOrder(0);
            zoneAffichage.setBackground(null);
            zoneAffichage.setViewOrder(99);
        });
    }



    //BINDINGS RATIO

    public void createBindingsRatio(double minRatio) {
        DoubleProperty b = VueDuJeu.ratioResolutionFenetreProperty();
        minWidthProperty().bind(Bindings.when(b.greaterThan(minRatio))
                        .then(b.multiply(getMinWidth()))
                        .otherwise(getMinWidth() * minRatio)
                );
        maxWidthProperty().bind(Bindings.when(b.greaterThan(minRatio))
                .then(b.multiply(getMinWidth()))
                .otherwise(getMinWidth() * minRatio)
        );
        minHeightProperty().bind(Bindings.when(b.greaterThan(minRatio))
                .then(b.multiply(getMinHeight()))
                .otherwise(getMinHeight() * minRatio)
        );
        maxHeightProperty().bind(Bindings.when(b.greaterThan(minRatio))
                .then(b.multiply(getMinHeight()))
                .otherwise(getMinHeight() * minRatio)
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
        v.createBindingsRatio(0.4);
        v.setBackground(null);
        zoneAffichage = v;
        pane.getChildren().add(zoneAffichage);
        StackPane.setAlignment(zoneAffichage, Pos.TOP_LEFT);
        StackPane.setMargin(zoneAffichage, new Insets(10));
    }

    public static void creerZoneAffichageDevoilee(StackPane pane) {
        VueCarte v = new VueCarte(GestionJeu.getJeu().getReserve().get(0));
        v.scale(1);
        v.createBindingsRatio(0.4);
        v.setBackground(null);
        zoneAffichageCarteDevoilee = v;
        pane.getChildren().add(zoneAffichageCarteDevoilee);
        StackPane.setAlignment(zoneAffichageCarteDevoilee, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(zoneAffichageCarteDevoilee, new Insets(10));
        zoneAffichage.setViewOrder(99);
    }


    public static void actionCentreDeControle() {
        int count = 0;
        zoneAffichageCarteDevoilee.setViewOrder(-1);
        for (ICarte carte : GestionJeu.getJeu().joueurCourantProperty().get().cartesAChoisir()) {
            VueCarte v = new VueCarte(carte);
            v.createBindingsRatio(0.2);

            v.setCarteChoisieListener(v.handlerCarteAChoisirSupression);
            if (count < 9) {
                StackPane.setMargin(v, new Insets(0, zoneAffichageCarteDevoilee.getChildren().size() * v.getMaxWidth(), 10, 0));
                v.setViewOrder(1);
                v.setCarteHover(1, -99);
            } else {
                StackPane.setMargin(v, new Insets(0, (zoneAffichageCarteDevoilee.getChildren().size()-8) * v.getMaxWidth(), v.getMaxHeight()+30, 0));
                v.setViewOrder(2);
                v.setCarteHover(1, -99);
            }
            zoneAffichageCarteDevoilee.getChildren().add(v);
            count++;
        }
    }



//    private static void devoilerUneCartePioche() {
//        ListeDeCartes pioche = GestionJeu.getJeu().joueurCourantProperty().get().piocheProperty();
//        if (!pioche.isEmpty()) {
//            VueCarte v = new VueCarte(pioche.get(0));
//            v.createBindingsRatio();
//            v.setCarteHover(1.0, 0);
//            VueAutresJoueurs.addZoneCarteDevoilee(v);
//        }
//    }

    public void devoilerCartesAChoisir(boolean handlerAchoisir) {
        for (Carte carte : GestionJeu.getJeu().joueurCourantProperty().get().cartesAChoisir()) {
            VueCarte v = new VueCarte(carte);
            if (handlerAchoisir) {
                v.setCarteChoisieListener(v.handlerCarteAChoisir);
            }
            v.scale(1);
            v.createBindingsRatio(0.4);
            v.setCarteHover(1.0, 0);
            StackPane.setMargin(v, new Insets(0, 0, 10, zoneAffichageCarteDevoilee.getChildren().size() * v.getMaxWidth()));
            zoneAffichageCarteDevoilee.getChildren().add(v);
            zoneAffichageCarteDevoilee.setViewOrder(-1);
        }
    }

    public static void resetCartesDevoilees() {
        zoneAffichageCarteDevoilee.setViewOrder(99);
        zoneAffichageCarteDevoilee.getChildren().clear();
    }


    public static VueCarte getZoneAffichageCarteDevoilee() {
        return zoneAffichageCarteDevoilee;
    }

}
