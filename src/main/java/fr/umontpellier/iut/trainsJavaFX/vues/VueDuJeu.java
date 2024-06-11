package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.StringJoiner;

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
    @FXML
    private Label instruction;
    @FXML
    private Label nomJoueurCourant;
    @FXML
    private Button boutonPasser;
    @FXML
    private ImageView logoArgent;
    @FXML
    private ImageView logoPointsRails;
    @FXML
    private ImageView logoJetonsRails;
    @FXML
    private Label labelNbArgent;
    @FXML
    private Label labelNbJetonsRails;
    @FXML
    private Label labelNbPointsRails;


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
        ajouterLogos();

        creerCartes();
        creerListeners();


    }

    private void creerListeners() {

        boutonPasser.setOnMouseClicked(actionPasserParDefaut);

        jeu.joueurCourantProperty().addListener((Observable, ancien, nouveau) -> {
            conteneurMainBottom.getChildren().clear();
            creerCartes();
        });

        for (IJoueur joueur : jeu.getJoueurs()) {
            joueur.mainProperty().addListener((ListChangeListener<Carte>) change -> {
                while (change.next()) {
                    for (Carte carte : change.getRemoved()) {
                        VueCarte c = trouverBoutonCartes(carte);
                        if (c!=null) {
                            conteneurMainBottom.getChildren().remove(c);
                        }
                    }
                }
            });
        }

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

        logoJetonsRails.imageProperty().bind(new ObjectBinding<>() {
            {
                super.bind(j);
            }
            @Override
            protected Image computeValue() {
                String couleur = "";
                switch (j.getValue().getCouleur()) {
                    case BLEU -> couleur = "blue";
                    case VERT -> couleur = "green";
                    case ROUGE -> couleur = "red";
                    case JAUNE -> couleur = "yellow";
                }
                return new Image("images/icons/cube_".concat(couleur).concat(".png"));
            }
        });

        labelNbPointsRails.textProperty().bind(j.getValue().pointsRailsProperty().asString());
        labelNbArgent.textProperty().bind(j.getValue().argentProperty().asString());
        labelNbJetonsRails.textProperty().bind(j.getValue().nbJetonsRailsProperty().asString());
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent) -> {
        System.out.println("Passer a été demandé");
        getJeu().passerAEteChoisi();
    };

    private void creerCartes() {
        ListeDeCartes mainJoueurCourrant = jeu.joueurCourantProperty().getValue().mainProperty();
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

    public void ajouterLogos() {
        logoPointsRails.setImage(new Image("images/icons/rail.png"));
        logoArgent.setImage(new Image("images/icons/coins.png"));
    }

}
