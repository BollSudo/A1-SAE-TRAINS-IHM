package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;

import java.io.IOException;

/**
 * Cette classe correspond à la fenêtre principale de l'application.
 *
 * Elle est initialisée avec une référence sur la partie en cours (Jeu).
 * <p>
 * On y définit les bindings sur les éléments internes qui peuvent changer
 * (le joueur courant, ses cartes en main, son score, ...)
 * ainsi que les listeners à exécuter lorsque ces éléments changent
 */
public class VueDuJeu extends GridPane {

    private final IJeu jeu;
    private VuePlateau plateau;
    private GridPane conteneurReserve;

    private VueJoueurCourant vueJoueurCourant;
    private Pane conteneurInstruction;


    public static final double HAUTEUR_ECRAN = Screen.getPrimary().getBounds().getHeight();
    public static final double LONGUEUR_ECRAN = Screen.getPrimary().getBounds().getWidth();

    private static DoubleProperty ratioLongueurFenetre;
    private static DoubleProperty ratioHauteurFenetre;
    private static DoubleProperty ratioResolutionFenetre;

    @FXML
    private Label instruction;
    @FXML
    private Label nomJoueurCourant;
    @FXML
    private Button boutonPasser;

    public VueDuJeu(IJeu jeu) {
        createRatio();
        this.jeu = jeu;
        plateau = new VuePlateau();
        vueJoueurCourant = new VueJoueurCourant(jeu.joueurCourantProperty().get());
        conteneurReserve = new GridPane();
//        ColumnConstraints colUne = new ColumnConstraints();
//        ColumnConstraints colDeux = new ColumnConstraints();
//        colUne.setPercentWidth(70);
//        colDeux.setPercentWidth(30);
//        getColumnConstraints().addAll(colUne,colDeux);



        VBox conteneurPlateau = new VBox();
        conteneurPlateau.setStyle("-fx-background-color: #722222");
        conteneurPlateau.getChildren().add(plateau);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/top.fxml"));
            loader.setController(this);
            conteneurInstruction = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        add(conteneurInstruction, 0, 0, 2, 1);
        add(conteneurPlateau, 0, 1, 1, 2);
        add(vueJoueurCourant.getInfoJoueurCourant(), 1, 2);
        add(vueJoueurCourant, 0, 3, 2, 1);
        add(conteneurReserve, 1, 1);

        creerReserve();
        creerListeners();
    }


    private void creerListeners() {

        //boutonPasser.setOnMouseClicked(actionPasserParDefaut);

    }

    public void creerBindings() {
        ObjectProperty<IJoueur> j = jeu.joueurCourantProperty();
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
        instruction.textProperty().bind(jeu.instructionProperty());
        vueJoueurCourant.createBindings();

        conteneurReserve.maxWidthProperty().bind(Bindings.when(ratioResolutionFenetreProperty().greaterThan(0.4))
                .then(ratioResolutionFenetreProperty().multiply(conteneurReserve.getMaxWidth()))
                        .otherwise(getMaxWidth() * 0.4)
                );
        vueJoueurCourant.getInfoJoueurCourant().prefWidthProperty().bind(conteneurReserve.maxWidthProperty());

        nomJoueurCourant.textProperty().bind(new StringBinding() {
            {
                super.bind(j);
            }
            @Override
            protected String computeValue() {
                return j.getValue().getNom();
            }
        });

        nomJoueurCourant.styleProperty().bind(new StringBinding() {
            {
                super.bind(j);
            }
            @Override
            protected String computeValue() {
                return "-fx-text-fill: ".concat(CouleursJoueurs.couleursBackgroundJoueur.get(j.getValue().getCouleur()));
            }
        });
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent) -> {
        System.out.println("Passer a été demandé");
        getJeu().passerAEteChoisi();
    };




    private void creerReserve() {

        int i = 0;
        int j = 0;
        for (Carte c : jeu.getReserve()) {
            if (c!=null && !c.getNom().equals("Ferraille")) {
                VueCarte carte = new VueCarte(c);
                carte.setCarteChoisieListener(carte.getHandlerCartesReserve());
                carte.scale(0.7);
                carte.createBindingsRatio();
                conteneurReserve.add(carte, i, j);
                if (i<5) {
                    i++;
                } else {
                    i=0;
                    j++;
                }
            }
        }
    }

    public void createRatio() {
        ratioHauteurFenetre = new SimpleDoubleProperty();
        ratioLongueurFenetre = new SimpleDoubleProperty();
        ratioResolutionFenetre = new SimpleDoubleProperty();
        ratioLongueurFenetre.bind(widthProperty().divide(LONGUEUR_ECRAN));
        ratioHauteurFenetre.bind(heightProperty().divide(HAUTEUR_ECRAN));
        ratioResolutionFenetre.bind(Bindings.divide(widthProperty().multiply(heightProperty()),LONGUEUR_ECRAN * HAUTEUR_ECRAN));


        ratioHauteurFenetre.addListener(((observableValue, number, t1) ->
                System.out.println("HAUTEUR : " + t1)));
        ratioLongueurFenetre.addListener(((observableValue, number, t1) ->
                System.out.println("LONGUEUR : " + t1)));
        ratioResolutionFenetre.addListener(((observableValue, number, t1) ->
                System.out.println("RESOLUTION : " + t1)));
    }

    public double getRatioLongueurFenetre() {
        return ratioLongueurFenetre.get();
    }

    public DoubleProperty ratioLongueurFenetreProperty() {
        return ratioLongueurFenetre;
    }

    public double getRatioHauteurFenetre() {
        return ratioHauteurFenetre.get();
    }

    public DoubleProperty ratioHauteurFenetreProperty() {
        return ratioHauteurFenetre;
    }

    public double getRatioResolutionFenetre() {
        return ratioResolutionFenetre.get();
    }

    public static DoubleProperty ratioResolutionFenetreProperty() {
        return ratioResolutionFenetre;
    }
}
