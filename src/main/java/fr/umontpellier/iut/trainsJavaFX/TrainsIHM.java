package fr.umontpellier.iut.trainsJavaFX;

import fr.umontpellier.iut.trainsJavaFX.mecanique.Jeu;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.FabriqueListeDeCartes;
import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.Plateau;
import fr.umontpellier.iut.trainsJavaFX.vues.DonneesGraphiques;
import fr.umontpellier.iut.trainsJavaFX.vues.VueChoixJoueurs;
import fr.umontpellier.iut.trainsJavaFX.vues.VueDuJeu;
import fr.umontpellier.iut.trainsJavaFX.vues.VueResultats;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TrainsIHM extends Application {
    private VueChoixJoueurs vueChoixJoueurs;
    private Stage primaryStage;
    private Jeu jeu;
    private final boolean avecVueChoixJoueurs = true;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setFullScreen(true);
        debuterJeu();
    }

    private void debuterJeu() {
        if (avecVueChoixJoueurs) {
            vueChoixJoueurs = new VueChoixJoueurs();
            vueChoixJoueurs.setNomsDesJoueursDefinisListener(quandLesNomsJoueursSontDefinis);
            vueChoixJoueurs.show();
        } else {
            demarrerPartie();
        }
    }

    public void demarrerPartie() {
       String[] nomsJoueurs;
       Plateau plateau = Plateau.OSAKA;
       if (avecVueChoixJoueurs) {
            nomsJoueurs = vueChoixJoueurs.getNomsJoueurs().toArray(new String[0]);
            plateau = vueChoixJoueurs.getPlateau();
       } else {
            nomsJoueurs = new String[]{"John", "Paul", "George", "Ringo"};
       }
        // Tirer aléatoirement 8 cartes préparation
        List<String> cartesPreparation = new ArrayList<>(FabriqueListeDeCartes.getNomsCartesPreparation());
        Collections.shuffle(cartesPreparation);
        String[] nomsCartes = cartesPreparation.subList(0, 8).toArray(new String[0]);
        jeu = new Jeu(nomsJoueurs, nomsCartes, plateau);
        GestionJeu.setJeu(jeu);
        VueDuJeu vueDuJeu = new VueDuJeu(jeu);
        vueDuJeu.getLogoHome().setOnMouseClicked((mouseEvent -> afficherConfirmationHome()));

        Scene scene = new Scene(vueDuJeu, Screen.getPrimary().getBounds().getWidth() * DonneesGraphiques.pourcentageEcran, Screen.getPrimary().getBounds().getHeight() * DonneesGraphiques.pourcentageEcran); // la scene doit être créée avant de mettre en place les bindings
        vueDuJeu.creerBindings();
        jeu.run(); // le jeu doit être démarré après que les bindings ont été mis en place

        jeu.finDePartieProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {afficherResultatsJeu();}
        }));

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode()== KeyCode.F12) {
                    primaryStage.setFullScreen(true);
                }
            }
        });

        primaryStage.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 2.5);
        primaryStage.setMinHeight(Screen.getPrimary().getBounds().getHeight() / 2.5);
        primaryStage.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
        primaryStage.setMaxHeight(Screen.getPrimary().getBounds().getHeight());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Trains");
        primaryStage.getIcons().add(new Image("images/icons/favicon.jpg"));
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            this.arreterJeu();
            event.consume();
        });
        primaryStage.show();
    }

    private final ListChangeListener<String> quandLesNomsJoueursSontDefinis = change -> {
        if (!vueChoixJoueurs.getNomsJoueurs().isEmpty())
            demarrerPartie();
    };

    public void arreterJeu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("A l'arrêt !");
        alert.setContentText("On arrête de jouer ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void afficherResultatsJeu() {
        VueResultats vueResultats = new VueResultats(this);
        getPrimaryStage().setScene(new Scene(vueResultats));
    }

    public void afficherConfirmationHome() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("RETURN TO HOME PAGE");
        alert.setContentText("Revenir à la page d'accueil ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            primaryStage.hide();
            debuterJeu();
        }
    }



    public Jeu getJeu() {
        return jeu;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}