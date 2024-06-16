package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.ListeDeCartes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.*;
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
    private Label labelNbCartesPioche;
    @FXML
    private Label labelNbCartesDefausse;
    @FXML
    private Label labelCartesMain;
    @FXML
    private Label labelCartesRecues;
    @FXML
    private ImageView logoCartesMain;
    @FXML
    private ImageView logoCartesRecues;
    @FXML
    private ImageView logoJetonsRails;
    @FXML
    private ImageView imageDefausse;
    @FXML
    private ImageView imageDeck;
    @FXML
    private Button boutonPasser;
    @FXML
    private VBox infoJoueurCourant;
    @FXML
    private FlowPane conteneurCartesEnJeu;
    @FXML
    private Pane conteneurCartesRecues;
    @FXML
    private Label nomJoueurCourant;


    private ObservableList<Carte> cartesDevoilees;


    public VueJoueurCourant(IJoueur joueur) {

        joueurCourant = joueur;
        this.setStyle("-fx-background-color: rgba(227,186,142,0.8)");
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
            e.printStackTrace();
        }

        conteneurCartesEnJeu.setPrefWrapLength(VueDuJeu.LONGUEUR_ECRAN * 0.375);
        conteneurMainBottom.setPrefWidth(1000);
        desactiverBoutonsDeckDefausse();
        creerCartes();
        creerListeners();
        createBindings();


        //LISTENERS LISTES DE CARTES DES JOUEURS
        for (IJoueur j : GestionJeu.getJeu().getJoueurs()) {
            j.mainProperty().addListener((ListChangeListener<Carte>) change -> {
                while (change.next()) {
                    for (Carte carte : change.getAddedSubList()) {
                        VueCarte c = new VueCarte(carte);
                        c.setCarteChoisieListener(c.getHandlerCartesMain());
                        c.setCarteHover(1.2, -1);
                        c.createBindingsRatio(0.4);
                        conteneurMainBottom.getChildren().add(c);
                    }
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
                        //speciale:
                        switch (carte.getNom()) {
                            case "Feu de signalisation" -> {
                                activerBoutonsDeckDefausse();
                            }
                            case "Centre de renseignements" -> {
                                boutonPasser.setOnMouseClicked(actionPasserCentreDeRenseignement);
                                boutonPasser.disableProperty().bind(new ObjectBinding<Boolean>() {
                                    {
                                        super.bind(VueCarte.getZoneAffichageCarteDevoilee().getChildren());
                                    }
                                    @Override
                                    protected Boolean computeValue() {
                                        return VueCarte.getZoneAffichageCarteDevoilee().getChildren().size() < 4
                                                && !VueCarte.getZoneAffichageCarteDevoilee().getChildren().isEmpty();
                                    }
                                });
                            }
                        }
                        //creation de la vueCarte
                        VueCarte c = new VueCarte(carte);
                        c.scale(0.7);
                        c.createBindingsRatio(0.4);
                        c.setCarteHover(1.0, 0);
                        c.setCarteChoisieListener(c.getHandlerCarteEnJeu());
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
                    int translateXFactor = 7;
                    for (Carte carte : change.getAddedSubList()) {
                        VueCarte c = new VueCarte(carte);
                        c.setCarteHover(1.0, 0);
                        c.setTranslateX(j.cartesRecuesProperty().size()*translateXFactor);
                        c.createBindingsRatio(0.4);
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

        labelNbCartesPioche.textProperty().bind(new StringBinding() {
            {
                super.bind(joueurCourant.piocheProperty());
            }
            @Override
            protected String computeValue() {
                return String.valueOf(joueurCourant.piocheProperty().size());
            }
        });
        labelNbCartesDefausse.textProperty().bind(new StringBinding() {
            {
                super.bind(joueurCourant.defausseProperty());
            }
            @Override
            protected String computeValue() {
                return String.valueOf(joueurCourant.defausseProperty().size());
            }
        });

        creerBindingsImagesCartesRatio(imageDeck, 0.5);
        creerBindingsImagesCartesRatio(imageDefausse, 0.5);
    }


    private void creerCartes() {
        ListeDeCartes mainJoueurCourrant = joueurCourant.mainProperty();
        for (int i = 0; i < mainJoueurCourrant.size(); i++) {
            VueCarte carte = new VueCarte(mainJoueurCourrant.get(i));
            carte.setCarteChoisieListener(carte.getHandlerCartesMain());
            carte.setCarteHover(1.2, -1);
            carte.createBindingsRatio(0.4);
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
                VueCarte.resetCartesDevoilees();
                logoJetonsRails.setImage(new Image("images/icons/cube_".concat(CouleursJoueurs.getNomCouleurPratique(joueurCourant)).concat(".png")));
            }
        });

        boutonPasser.setOnMouseClicked(actionPasserParDefaut);
        //boutonPasser.setOnMouseClicked(mouseEvent -> GestionJeu.getJeu().finDePartieProperty().set(true));
        logoCartesMain.setOnMouseEntered((mouseEvent -> labelCartesMain.setVisible(true)));
        logoCartesMain.setOnMouseExited((mouseEvent -> labelCartesMain.setVisible(false)));
        logoCartesRecues.setOnMouseEntered((mouseEvent -> labelCartesRecues.setVisible(true)));
        logoCartesRecues.setOnMouseExited((mouseEvent -> labelCartesRecues.setVisible(false)));

        imageDefausse.setOnMouseClicked(mouseEvent -> {
            joueurCourant.laDefausseAEteChoisie();
            VueCarte.resetCartesDevoilees();
            System.out.println("La défausse a été choisie");
            desactiverBoutonsDeckDefausse();
        });
        imageDeck.setOnMouseClicked(mouseEvent -> {
            joueurCourant.laPiocheAEteChoisie();
            VueCarte.resetCartesDevoilees();
            System.out.println("La pioche a été choisie");
            desactiverBoutonsDeckDefausse();
        });
    }

    private void desactiverBoutonsDeckDefausse() {
        imageDeck.setDisable(true);
        imageDeck.setEffect(new Lighting());
        imageDefausse.setDisable(true);
        imageDefausse.setEffect(new Lighting());
    }

    private void activerBoutonsDeckDefausse() {
        imageDeck.setDisable(false);
        imageDeck.setEffect(null);
        imageDefausse.setDisable(false);
        imageDefausse.setEffect(null);
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent) -> {
        System.out.println("Passer a été demandé");
        GestionJeu.getJeu().passerAEteChoisi();
    };

    EventHandler<? super MouseEvent> actionPasserCentreDeRenseignement= (mouseEvent) -> {
        System.out.println("Passer a été demandé");
        GestionJeu.getJeu().passerAEteChoisi();
        VueCarte.resetCartesDevoilees();
        boutonPasser.disableProperty().unbind();
        boutonPasser.setOnMouseClicked(actionPasserParDefaut);

    };

    public Pane getInfoJoueurCourant() {
        return infoJoueurCourant;
    }

    public Pane getConteneurCartesRecues() {return conteneurCartesRecues;}

    private void creerBindingsImagesCartesRatio(ImageView imageView, double minRatio) {
        imageView.fitWidthProperty().bind(Bindings.when(VueDuJeu.ratioResolutionFenetreProperty().greaterThan(minRatio))
                .then(VueDuJeu.ratioResolutionFenetreProperty().multiply(VueCarte.LONGUEUR_INIT).multiply(1.2))
                .otherwise(VueCarte.LONGUEUR_INIT * minRatio * 1.2)
        );
        imageView.fitHeightProperty().bind(Bindings.when(VueDuJeu.ratioResolutionFenetreProperty().greaterThan(minRatio))
                .then(VueDuJeu.ratioResolutionFenetreProperty().multiply(VueCarte.HAUTEUR_INIT).multiply(1.2))
                .otherwise(VueCarte.HAUTEUR_INIT * minRatio * 1.2)
        );
    }

    public Label getNomJoueurCourant() {
        return nomJoueurCourant;
    }
}
