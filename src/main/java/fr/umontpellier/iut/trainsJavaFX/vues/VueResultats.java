package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.TrainsIHM;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.io.IOException;

public class VueResultats extends Pane {

    private TrainsIHM ihm;

    @FXML
    private ImageView imageVictoire;
    @FXML
    private Label texteVictoire;
    @FXML
    private Label nomGagnant;

    public VueResultats(TrainsIHM ihm) {
        this.ihm = ihm;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/fin.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        fin = new Popup();
//        fin.getContent().add(new VueFinDePartie(getTableauScore().get(0)));


        Image bg = new Image("images/fond/victoire.png");//images/fond/victoire.png
        this.setBackground(new Background(new BackgroundImage(bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false,false))));
        imageVictoire.setImage(new Image("images/icons/trophee.png"));//images/icons/stars.png
        nomGagnant.setText("OE");
        nomGagnant.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
        texteVictoire.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
    }

    

}
