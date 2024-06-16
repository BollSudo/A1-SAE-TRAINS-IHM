package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    VBox infoTurnOrder;
    private static HBox zoneCarteDevoilee;

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
        Label nomVille = new Label(GestionJeu.getJeu().getNomVille());
        nomVille.setFont(Font.font("Oswald", FontWeight.BOLD, 20));
        infoTurnOrder = new VBox(new HBox(nomVille), new HBox(new Label("Next Player ->")));
        infoTurnOrder.setPadding(new Insets(0, 0, 0, 5));
        zoneCarteDevoilee = new HBox();
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

        zoneCarteDevoilee.setAlignment(Pos.BOTTOM_CENTER);
        zoneCarteDevoilee.setFillHeight(true);
        zoneCarteDevoilee.setPadding(new Insets(0, 0, 10, 0));
        VBox.setVgrow(zoneCarteDevoilee, Priority.ALWAYS);
        getChildren().add(zoneCarteDevoilee);
    }

    private void creerListeners() {
        GestionJeu.getJeu().joueurCourantProperty().addListener((obs, ancien, nouveau) -> {
            afficher();
        });

    }

    public static void addZoneCarteDevoilee(StackPane pane) {
        zoneCarteDevoilee.getChildren().add(pane);
    }

}
