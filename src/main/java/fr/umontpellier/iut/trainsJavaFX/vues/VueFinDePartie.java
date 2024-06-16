package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.io.IOException;

public class VueFinDePartie extends Pane {
    @FXML
    private ImageView imageVictoire;
    @FXML
    private Label texteVictoire;
    @FXML
    private Label nomGagnant;


    public VueFinDePartie(String nom){
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/fin.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image bg = new Image("images/fond/victoire.png");//images/fond/victoire.png
        this.setBackground(new Background(new BackgroundImage(bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
        imageVictoire.setImage(new Image("images/icons/trophee.png"));//images/icons/stars.png
        nomGagnant.setText(nom);
        nomGagnant.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
        texteVictoire.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
    }

}
