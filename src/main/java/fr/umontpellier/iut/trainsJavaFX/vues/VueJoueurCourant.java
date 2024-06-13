package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 * <p>
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends HBox {

    IJoueur joueurCourant;

    ObjectProperty<IJoueur> joueurCourantProperty;

    @FXML
    private HBox conteneurMainBottom;
    @FXML
    private Label labelNbArgent;
    @FXML
    private Label labelNbJetonsRails;
    @FXML
    private Label labelNbPointsRails;
    @FXML
    private ImageView logoJetonsRails;
    @FXML
    private ImageView imageDefausse;
    @FXML
    private ImageView imageDeck;
    @FXML
    private Button boutonPasser;
    @FXML
    private Pane infoJoueurCourant;
    @FXML
    private Pane conteneurCartesEnJeu;
    @FXML
    private Pane conteneurCartesRecues;

    public VueJoueurCourant(IJoueur joueur) {
        joueurCourant = joueur;
        joueurCourantProperty = new SimpleObjectProperty<>(joueur);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
            FXMLLoader loader2 = new FXMLLoader(getClass().getClassLoader().getResource("fxml/info.fxml"));
            loader.setController(this);
            HBox conteneurMain = loader.load();
            loader2.setController(this);
            loader2.load();
            getChildren().add(conteneurMain);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        conteneurMainBottom.setPrefWidth(1000);
        creerCartes();
        creerListeners();
        createBindings();


        //LISTENERS LISTES DE CARTES DES JOUEURS
        for (IJoueur j : GestionJeu.getJeu().getJoueurs()) {
            j.mainProperty().addListener((ListChangeListener<Carte>) change -> {
                while (change.next()) {
                    for (Carte carte : change.getRemoved()) {
                        VueCarte c = trouverBoutonCartes(carte, conteneurMainBottom);
                        if (c!=null) {
                            conteneurMainBottom.getChildren().remove(c);
                        }
                    }
                }
            });

            j.cartesEnJeuProperty().addListener((ListChangeListener<Carte>) change -> {
                while (change.next()) {
                    for (Carte carte : change.getAddedSubList()) {
                        VueCarte c = new VueCarte(carte);
                        c.scale(0.8);
                        c.scale(0.8);
                        c.setDisable(true);
                        conteneurCartesEnJeu.getChildren().add(c);
                    }
                    for (Carte carte : change.getRemoved()) {
                        VueCarte c = trouverBoutonCartes(carte, conteneurCartesEnJeu);
                        if (c!=null) {
                            conteneurCartesEnJeu.getChildren().remove(c);
                        }
                    }
                }
            });

            j.cartesRecuesProperty().addListener((ListChangeListener<Carte>) change -> {
                while (change.next()) {
                    int translateXFactor = 5;
                    for (Carte carte : change.getAddedSubList()) {
                        VueCarte c = new VueCarte(carte);
                        c.scale(0.8);
                        c.scale(0.8);
                        c.setDisable(true);
                        c.setTranslateX(j.cartesRecuesProperty().size()*translateXFactor);
                        conteneurCartesRecues.getChildren().add(c);
                    }
                    for (Carte carte : change.getRemoved()) {
                        VueCarte c = trouverBoutonCartes(carte, conteneurCartesRecues);
                        if (c!=null) {
                            conteneurCartesRecues.getChildren().remove(c);
                        }
                    }
                }
            });

        }
    }

    public void createBindings() {
        labelNbPointsRails.textProperty().bind(joueurCourant.pointsRailsProperty().asString());
        labelNbArgent.textProperty().bind(joueurCourant.argentProperty().asString());
        labelNbJetonsRails.textProperty().bind(joueurCourant.nbJetonsRailsProperty().asString());
    }


    private void creerCartes() {
        ListeDeCartes mainJoueurCourrant = joueurCourant.mainProperty();
        for (int i = 0; i < mainJoueurCourrant.size(); i++) {
            VueCarte carte = new VueCarte(mainJoueurCourrant.get(i));
            carte.setCarteChoisieListener(carte.getHandlerCartesMain());
            conteneurMainBottom.getChildren().add(carte);
        }
    }


    public VueCarte trouverBoutonCartes(Carte carteAtrouver, Pane n) {
        for (Node node : n.getChildren()) {
            VueCarte c = (VueCarte) node;
            if (carteAtrouver.getNom().equals((c.getCarte().getNom()))) {
                return c;
            }
        }
        return null;
    }

    private void creerListeners() {
        SimpleObjectProperty<IJoueur> p = new SimpleObjectProperty<>(joueurCourant);
        GestionJeu.getJeu().joueurCourantProperty().addListener(new ChangeListener<IJoueur>() {
            @Override
            public void changed(ObservableValue<? extends IJoueur> observableValue, IJoueur joueur, IJoueur t1) {
                joueurCourant = t1;
                conteneurMainBottom.getChildren().clear();
                creerCartes();
                createBindings();
                String couleur = "";
                switch (joueurCourant.getCouleur()) {
                    case BLEU -> couleur = "blue";
                    case VERT -> couleur = "green";
                    case ROUGE -> couleur = "red";
                    case JAUNE -> couleur = "yellow";
                }
                logoJetonsRails.setImage(new Image("images/icons/cube_".concat(couleur).concat(".png")));
            }
        });

        boutonPasser.setOnMouseClicked(actionPasserParDefaut);
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent) -> {
        System.out.println("Passer a été demandé");
        GestionJeu.getJeu().passerAEteChoisi();
    };

    public Pane getInfoJoueurCourant() {
        return infoJoueurCourant;
    }
}
