package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Random;

/**
 * Cette classe correspond à la fenêtre principale de l'application.
 *
 * Elle est initialisée avec une référence sur la partie en cours (Jeu).
 * <p>
 * On y définit les bindings sur les éléments internes qui peuvent changer
 * (le joueur courant, ses cartes en main, son score, ...)
 * ainsi que les listeners à exécuter lorsque ces éléments changent
 */
public class VueDuJeu extends BorderPane {

    private final IJeu jeu;
    private VuePlateau plateau;

    @FXML
    private HBox conteneurMainBottom;


    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        plateau = new VuePlateau();
        Pane p = new Pane();
        setLeft(p);
        p.getChildren().add(plateau);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
            loader.setController(this);
            HBox conteneurMain = loader.load();
            setBottom(conteneurMain);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        creerCartes();

        jeu.joueurCourantProperty().addListener((Observable, ancien, nouveau) -> {
            conteneurMainBottom.getChildren().clear();
            creerCartes();
        });


        for (IJoueur joueur : jeu.getJoueurs()) {
            joueur.mainProperty().addListener(new ListChangeListener<Carte>() {
                @Override
                public void onChanged(Change<? extends Carte> change) {
                    while (change.next()) {
                        for (Carte carte : change.getRemoved()) {
                            VueCarte c = trouverBoutonCartes(carte);
                            if (c!=null) {
                                conteneurMainBottom.getChildren().remove(c);
                            }
                        }
                    }
                }
            });
        }






    }

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        plateau.creerBindings();
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> System.out.println("Passer a été demandé"));

    private void creerCartes() {
        ListeDeCartes mainJoueurCourrant = jeu.joueurCourantProperty().get().mainProperty();
        for (int i = 0; i < mainJoueurCourrant.size(); i++) {
            VueCarte carte = new VueCarte(mainJoueurCourrant.get(i));
            conteneurMainBottom.getChildren().add(carte);
        }
    }

    public VueCarte trouverBoutonCartes(Carte carteAtrouver) {
        for (Node node : conteneurMainBottom.getChildren()) {
            VueCarte c = (VueCarte) node;
            if (carteAtrouver.getNom().equals((c.getCarte().getNom()))) {
                return c;
            }
        }
        return null;
    }

}
