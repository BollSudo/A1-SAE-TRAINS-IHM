package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.IJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.Carte;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private VueAutresJoueurs vueAutresJoueurs;
    private Pane conteneurInstruction;
    private ImageView boutonArgent;
    private static StackPane conteneurPlateau;


    public static final double HAUTEUR_ECRAN = Screen.getPrimary().getBounds().getHeight();
    public static final double LONGUEUR_ECRAN = Screen.getPrimary().getBounds().getWidth();
    private static DoubleProperty ratioResolutionFenetre;


    @FXML
    private Label instruction;
    @FXML
    private Label nomJoueurCourant;
    @FXML
    private ImageView logoHome;
    @FXML
    private ImageView logoInfo;


    public VueDuJeu(IJeu jeu) {
        createRatio();
        this.jeu = jeu;
        plateau = new VuePlateau();
        vueJoueurCourant = new VueJoueurCourant(jeu.joueurCourantProperty().get());
        vueAutresJoueurs = new VueAutresJoueurs();
        conteneurReserve = new GridPane();
        conteneurPlateau = new StackPane();
        VueCarte.creerZoneAffichageZoom(conteneurPlateau);
        VueCarte.creerZoneAffichageDevoilee(conteneurPlateau);
        conteneurPlateau.getChildren().add(plateau);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/top.fxml"));
            loader.setController(this);
            conteneurInstruction = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(conteneurInstruction, 0, 0, 3, 1);
        add(new VBox(conteneurPlateau), 1, 1, 1, 2);
        add(vueJoueurCourant.getInfoJoueurCourant(), 2, 2);
        add(vueJoueurCourant, 0, 3, 3, 1);
        add(conteneurReserve, 2, 1);
        add(vueAutresJoueurs, 0, 1, 1, 2);

        setStyle();
        setConstraints();
        creerReserve();
        creerListeners();
    }

    private void setStyle() {
        Image image  = new Image("images/fond/fond.png");
        setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
        conteneurReserve.setStyle("-fx-background-color: rgba(255,255,255,0.71)");
        conteneurPlateau.setStyle("-fx-background-color: #212528");
    }
    private void setConstraints() {
        ColumnConstraints colUne = new ColumnConstraints();
        colUne.setMinWidth(0.08681 * LONGUEUR_ECRAN);
        getColumnConstraints().addAll(colUne);
        RowConstraints rowDeux = new RowConstraints();
        rowDeux.setMinHeight(0.1 * HAUTEUR_ECRAN);
        getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), rowDeux);
    }
    private void creerListeners() {
        logoInfo.setOnMouseClicked(mouseEvent -> afficherInformation());
        setHoverEffect(logoHome);
        setHoverEffect(logoInfo);
        setHoverEffect(boutonArgent);
        boutonArgent.setOnMouseClicked(mouseEvent -> {
            jeu.joueurCourantProperty().getValue().recevoirArgentAEteChoisi();
            System.out.println("Recevoir argent a été choisi");
        });
        vueJoueurCourant.getLabelNbArgentBig().setOnMouseEntered(mouseEvent -> {
            boutonArgent.setScaleX(1.2);
            boutonArgent.setScaleY(1.2);
        });
    }
    public void afficherInformation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFO");
        alert.setContentText("Projet réalisé par A. DESCHANEL & J. RENAUD - Tchuuu Tchuuu");
        alert.showAndWait();
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

        vueJoueurCourant.getNomJoueurCourant().textProperty().bind(nomJoueurCourant.textProperty());
        vueJoueurCourant.getNomJoueurCourant().styleProperty().bind(nomJoueurCourant.styleProperty());

        boutonArgent.fitWidthProperty().bind(Bindings.when(ratioResolutionFenetreProperty().
                greaterThan(0.4)).then(ratioResolutionFenetreProperty().multiply(75)).
                        otherwise(0.4 * 75));
        boutonArgent.fitHeightProperty().bind(Bindings.when(ratioResolutionFenetreProperty().
                        greaterThan(0.4)).then(ratioResolutionFenetreProperty().multiply(75)).
                otherwise(0.4 * 75));
    }

    private void creerReserve() {
        int i = 0;
        int j = 0;
        for (Carte c : jeu.getReserve()) {
            if (c!=null) {
                VueCarte carte = new VueCarte(c);
                carte.setCarteChoisieListener(carte.getHandlerCartesReserve());
                carte.setCarteHover(1.2, -1);
                carte.scale(0.7);
                carte.createBindingsRatio(0.4);
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
        conteneurReserve.add(creerBoutonArgent() ,i+1, j);
    }
    private StackPane creerBoutonArgent() {
        StackPane p = new StackPane();
        boutonArgent = new ImageView();
        boutonArgent.setFitWidth(50);
        boutonArgent.setFitHeight(50);
        boutonArgent.setImage(new Image("images/boutons/coins.png"));

        p.getChildren().addAll(boutonArgent, vueJoueurCourant.getLabelNbArgentBig());
        return p;
    }
    public void createRatio() {
        ratioResolutionFenetre = new SimpleDoubleProperty();
        ratioResolutionFenetre.bind(Bindings.divide(widthProperty().multiply(heightProperty()),LONGUEUR_ECRAN * HAUTEUR_ECRAN));
//        ratioResolutionFenetre.addListener(((observableValue, number, t1) ->
//                System.out.println("RESOLUTION : " + t1)));
    }

    //GETTERS
    public IJeu getJeu() {
        return jeu;
    }
    public ImageView getLogoHome() {
        return logoHome;
    }

    //STATIC
    public static DoubleProperty ratioResolutionFenetreProperty() {
        return ratioResolutionFenetre;
    }
    public static void setHoverEffect(Node node) {
        node.setOnMouseEntered((mouseEvent -> {
            node.setScaleX(1.2);
            node.setScaleY(1.2);
        }));
        node.setOnMouseExited((mouseEvent -> {
            node.setScaleX(1);
            node.setScaleY(1);
        }));
    }
}
