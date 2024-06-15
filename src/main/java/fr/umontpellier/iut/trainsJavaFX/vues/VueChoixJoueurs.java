package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.Plateau;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe correspond à une nouvelle fenêtre permettant de choisir le nombre et les noms des joueurs de la partie.
 * <p>
 * Sa présentation graphique peut automatiquement être actualisée chaque fois que le nombre de joueurs change.
 * Lorsque l'utilisateur a fini de saisir les noms de joueurs, il demandera à démarrer la partie.
 */
public class VueChoixJoueurs extends Stage {

    private final ObservableList<String> nomsJoueurs;
    private Plateau plateauChoisi;

    private List<StringProperty> nomsTemp;
    private IntegerProperty nombreJoueursProperty;

    @FXML
    private Button boutonOsaka;
    @FXML
    private Button boutonTokyo;
    @FXML
    private HBox conteneurBoutons;
    @FXML
    private VBox conteneurInputs;
    @FXML
    private Button boutonStart;
    @FXML
    private Label errorMessage;

    private String styleBoutonVert;
    private String styleBoutonGris;


    public VueChoixJoueurs() {
        nomsJoueurs = FXCollections.observableArrayList();
        nomsTemp = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            nomsTemp.add(new SimpleStringProperty());
        }
        nombreJoueursProperty = new SimpleIntegerProperty(2);
        plateauChoisi = Plateau.OSAKA;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/accueil.fxml"));
            loader.setController(this);
            setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        styleBoutonVert = boutonOsaka.getStyle();
        styleBoutonGris = boutonTokyo.getStyle();

        resetEffectBoutonsNbJoueurs();
        conteneurBoutons.getChildren().get(0).setEffect(null);
        creerListeInputs();
        createListeners();
    }

    public List<String> getNomsJoueurs() {
        return nomsJoueurs;
    }

    /**
     * Définit l'action à exécuter lorsque la liste des participants est correctement initialisée
     */
    public void setNomsDesJoueursDefinisListener(ListChangeListener<String> quandLesNomsDesJoueursSontDefinis) {
        nomsJoueurs.addListener(quandLesNomsDesJoueursSontDefinis);
    }

    /**
     * Vérifie que tous les noms des participants sont renseignés
     * et affecte la liste définitive des participants
     */
    protected void setListeDesNomsDeJoueurs() {
        ArrayList<String> tempNamesList = new ArrayList<>();
        for (int i = 1; i <= getNombreDeJoueurs() ; i++) {
            String name = getJoueurParNumero(i);
            if (name == null || name.equals("")) {
                tempNamesList.clear();
                break;
            }
            else
                tempNamesList.add(name);
        }
        if (!tempNamesList.isEmpty()) {
            hide();
            nomsJoueurs.clear();
            nomsJoueurs.addAll(tempNamesList);
        } else {
            errorMessage.setText("Veuillez renseigner tout les noms des joueurs");
        }
    }

    /**
     * Retourne le nombre de participants à la partie que l'utilisateur a renseigné
     */
    protected int getNombreDeJoueurs() {
        return nombreJoueursProperty.get();
    }

    /**
     * Retourne le nom que l'utilisateur a renseigné pour le ième participant à la partie
     * @param playerNumber : le numéro du participant
     */
    protected String getJoueurParNumero(int playerNumber) {
        if (playerNumber > 0 && playerNumber <= getNombreDeJoueurs()) {
            return nomsTemp.get(playerNumber-1).getValue();
        }
        else return null;
    }

    public Plateau getPlateau() {
        return plateauChoisi;
    }



    private void createListeners() {
        boutonOsaka.setOnAction((event) -> {
            plateauChoisi = Plateau.OSAKA;
            boutonOsaka.setStyle(styleBoutonVert);
            boutonTokyo.setStyle(styleBoutonGris);
        });
        boutonTokyo.setOnAction((event) ->  {
            plateauChoisi = Plateau.TOKYO;
            boutonTokyo.setStyle(styleBoutonVert);
            boutonOsaka.setStyle(styleBoutonGris);
        });
        setEffetZoom(boutonOsaka, 1.1);
        setEffetZoom(boutonTokyo, 1.1);
        setEffetZoom(boutonStart, 1.1);
        boutonStart.setOnAction((event -> setListeDesNomsDeJoueurs()));

        for (Node bouton : conteneurBoutons.getChildren()) {
            bouton.setOnMouseClicked(mouseEvent -> {
                switch (bouton.getId()) {
                    case "boutonDeux" -> nombreJoueursProperty.set(2);
                    case "boutonTrois" -> nombreJoueursProperty.set(3);
                    case "boutonQuatre" -> nombreJoueursProperty.set(4);
                }
                resetEffectBoutonsNbJoueurs();
                bouton.setEffect(null);
            });
            setEffetZoom(bouton, 1.1);
        }
        nombreJoueursProperty.addListener((change) -> creerListeInputs());
    }


    private void setEffetZoom(Node node, double scaleOfZoom) {
        node.setOnMouseEntered((mouseEvent -> {
            node.setScaleX(scaleOfZoom);
            node.setScaleY(scaleOfZoom);
        }));
        node.setOnMouseExited((mouseEvent -> {
            node.setScaleX(1);
            node.setScaleY(1);
        }));
    }


    private void resetEffectBoutonsNbJoueurs() {
        for (Node bouton : conteneurBoutons.getChildren()) {
            bouton.setEffect(new Lighting());
        }
    }

    private void creerListeInputs() {
        conteneurInputs.getChildren().clear();
        for (int i = 1; i < getNombreDeJoueurs()+1; i++) {
            conteneurInputs.getChildren().add(creerInputPratique(i));
        }
    }

    private HBox creerInputPratique(int numeroJoueur) {
        HBox input = new HBox();
        Label lib = new Label("Player " + numeroJoueur + " : ");
        TextField t = new TextField();
        String placeholder = placeholderNames.get(numeroJoueur-1);
        t.setPromptText(placeholder);
        input.setAlignment(Pos.CENTER);
        input.setSpacing(10);
        input.getChildren().addAll(lib, t);
        nomsTemp.get(numeroJoueur-1).bind(t.textProperty());
        t.textProperty().addListener(((observableValue, s, t1) -> {
            if (t1.length() > 7) {
                t.setText(s);
            }
        }));
        return input;
    }

    private List<String> placeholderNames = new ArrayList<>(List.of("Rick","Astley","Morgan","Freeman"));}
