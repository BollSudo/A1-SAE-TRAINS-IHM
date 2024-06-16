package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.ICarte;
import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

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
    private Popup fin;
    private GridPane conteneurReserve;
    private GridPane fenetre = this;
    private VueJoueurCourant vueJoueurCourant;

    private VueAutresJoueurs vueAutresJoueurs;
    private Pane conteneurInstruction;


    public static final double HAUTEUR_ECRAN = Screen.getPrimary().getBounds().getHeight();
    public static final double LONGUEUR_ECRAN = Screen.getPrimary().getBounds().getWidth();
    private static DoubleProperty ratioResolutionFenetre;

    @FXML
    private Label instruction;
    @FXML
    private Label nomJoueurCourant;
    @FXML
    private Button boutonPasser;

    public VueDuJeu(IJeu jeu) {
        Image image  = new Image("images/fond/fond.png");
        this.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
        createRatio();
        this.jeu = jeu;
        plateau = new VuePlateau();
        vueJoueurCourant = new VueJoueurCourant(jeu.joueurCourantProperty().get());
        vueAutresJoueurs = new VueAutresJoueurs();
        conteneurReserve = new GridPane();
        conteneurReserve.setStyle("-fx-background-color: rgb(255,255,255,0.4)");
        ColumnConstraints colUne = new ColumnConstraints();
        colUne.setMinWidth(0.08681 * LONGUEUR_ECRAN);
        // ColumnConstraints colDeux = new ColumnConstraints();
        // colUne.setPercentWidth(70);
        // colDeux.setPercentWidth(30);
        getColumnConstraints().addAll(colUne);

        RowConstraints rowDeux = new RowConstraints();
        rowDeux.setMinHeight(0.1 * HAUTEUR_ECRAN);
        getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), rowDeux);

        fin = new Popup();
        fin.getContent().add(new VueFinDePartie(getTableauScore().get(0)));

        VBox conteneurPlateau = new VBox();
        conteneurPlateau.setStyle("-fx-background-color: rgba(255,255,255,0.4)");
        conteneurPlateau.getChildren().add(plateau);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/top.fxml"));
            loader.setController(this);
            conteneurInstruction = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(conteneurInstruction, 0, 0, 3, 1);
        add(conteneurPlateau, 1, 1, 1, 2);
        add(vueJoueurCourant.getInfoJoueurCourant(), 2, 2);
        add(vueJoueurCourant, 0, 3, 3, 1);
        add(conteneurReserve, 2, 1);
        add(vueAutresJoueurs, 0, 1, 1, 2);

        creerReserve();
        creerListeners();
    }


    private ArrayList<String> getTableauScore(){
        PriorityQueue<IJoueur> pq = new PriorityQueue<>(new Comparator<IJoueur>() {
            @Override
            public int compare(IJoueur o1, IJoueur o2) {
                return o1.getScoreTotal() - o2.getScoreTotal();
            }
        });
        pq.addAll(GestionJeu.getJeu().getJoueurs());
        ArrayList<String> tableauScore = new ArrayList<>();
        while (!pq.isEmpty()){
            tableauScore.add(pq.poll().getNom());
        }
        return tableauScore;
    }

    private void creerListeners() {
        getJeu().finDePartieProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                fin.show(fenetre, 1, 1);
            }
        });
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
        vueJoueurCourant.getConteneurCartesRecues().minWidthProperty().bind(Bindings.when(ratioResolutionFenetreProperty().greaterThan(0.4))
                .then(ratioResolutionFenetre.multiply(VueCarte.LONGUEUR_INIT))
                .otherwise(VueCarte.LONGUEUR_INIT * 0.4));

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
            if (c!=null) {
                VueCarte carte = new VueCarte(c);
                carte.setCarteChoisieListener(carte.getHandlerCartesReserve());
                carte.scale(0.7);
                carte.createBindingsRatio();
                carte.creerLabelPileReserve();
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
        ratioResolutionFenetre = new SimpleDoubleProperty();
        ratioResolutionFenetre.bind(Bindings.divide(widthProperty().multiply(heightProperty()),LONGUEUR_ECRAN * HAUTEUR_ECRAN));

//        ratioResolutionFenetre.addListener(((observableValue, number, t1) ->
//                System.out.println("RESOLUTION : " + t1)));
    }

    public static DoubleProperty ratioResolutionFenetreProperty() {
        return ratioResolutionFenetre;
    }
}
