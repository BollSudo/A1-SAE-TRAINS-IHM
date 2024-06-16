package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe présente les éléments des joueurs autres que le joueur courant,
 * en cachant ceux que le joueur courant n'a pas à connaitre.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueAutresJoueurs extends VBox {

    public class VueUnAutreJoueur extends Pane {

        @FXML
        private Label scoreJoueur;
        @FXML
        private Label labelJetonsJoueur;
        @FXML
        private Label nomJoueur;
        @FXML
        private ImageView logoJetonsJoueur;
        IJoueur joueur;

        public VueUnAutreJoueur(IJoueur joueur) {
            this.joueur = joueur;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/left.fxml"));
                loader.setController(this);
                getChildren().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            nomJoueur.setText(joueur.getNom());
            logoJetonsJoueur.setImage(new Image("images/icons/cube_".concat(CouleursJoueurs.getNomCouleurPratique(joueur)).concat(".png")));
            creerListeners();
            createBindings();
        }

        public void createBindings() {
            scoreJoueur.textProperty().bind(joueur.scoreProperty().asString());
            labelJetonsJoueur.textProperty().bind(joueur.nbJetonsRailsProperty().asString());
        }
    }

    List<VueUnAutreJoueur> infosAutresJoueurs;
    VueUnAutreJoueur infoJoueurCourant;
    HBox infoTurnOrder;
    public VueAutresJoueurs() {
        setStyle("-fx-background-color: rgb(255,255,255, 0.4)");
        setAlignment(Pos.TOP_CENTER);
        infosAutresJoueurs = new ArrayList<>();
        for (IJoueur joueur : GestionJeu.getJeu().getJoueurs()) {
            if (joueur != GestionJeu.getJeu().joueurCourantProperty().get()) {
                infosAutresJoueurs.add(new VueUnAutreJoueur(joueur));
            } else {
                infoJoueurCourant = new VueUnAutreJoueur(joueur);
            }
        }
        infoTurnOrder = new HBox(new Label("Next Player ->"));
    }

    private void afficher() {
        getChildren().clear();
        getChildren().add(infoTurnOrder);
        infosAutresJoueurs.add(infoJoueurCourant);
        for (VueUnAutreJoueur node : infosAutresJoueurs) {
            if (node.joueur != GestionJeu.getJeu().joueurCourantProperty().get()) {
                getChildren().add(node);
            } else {
                infoJoueurCourant = node;
            }
        }
        infosAutresJoueurs.remove(infoJoueurCourant);
    }

    private void creerListeners() {
        GestionJeu.getJeu().joueurCourantProperty().addListener((obs, ancien, nouveau) -> {
            afficher();
        });
    }

}
