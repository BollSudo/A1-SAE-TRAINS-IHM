package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

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

    @FXML
    private Label instruction;
    @FXML
    private Label nomJoueurCourant;
    @FXML
    private Button boutonPasser;

    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        plateau = new VuePlateau();
        vueJoueurCourant = new VueJoueurCourant(jeu.joueurCourantProperty().get());
        conteneurReserve = new GridPane();
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

}
