package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.TrainsIHM;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class VueResultats extends Pane {

    private TrainsIHM ihm;

    @FXML
    private ImageView imageVictoire;
    @FXML
    private ImageView image2;
    @FXML
    private ImageView image3;
    @FXML
    private ImageView imageD;
    @FXML
    private Label texteVictoire;
    @FXML
    private Label texte2;
    @FXML
    private Label texte3;
    @FXML
    private Label texteD;
    @FXML
    private Label nomGagnant;
    @FXML
    private Label nom2;
    @FXML
    private Label nom3;
    @FXML
    private Label nomD;
    @FXML
    private HBox ligne1;
    @FXML
    private HBox ligne2;
    @FXML
    private HBox ligne3;
    @FXML
    private HBox ligneD;
    @FXML
    private Label labelTitre;


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
        nomGagnant.setText("  "+getTableauScore().get(0).getNom());
        texteVictoire.setText("  "+getTableauScore().get(0).getScoreTotal()+"  ");
        nomGagnant.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
        texteVictoire.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");

        if (getTableauScore().size()>2){
            nom2.setText("  "+getTableauScore().get(1).getNom());
            texte2.setText("  "+getTableauScore().get(1).getScoreTotal()+"  ");
            image2.setImage(new Image("images/icons/pos2.png"));
            nom2.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
            texte2.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
            if(getTableauScore().size()>3){
                nom3.setText("  "+getTableauScore().get(2).getNom());
                texte3.setText("  "+getTableauScore().get(2).getScoreTotal()+"  ");
                image3.setImage(new Image("images/icons/pos3.png"));
                nom3.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
                texte3.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
            }else {
                ligne3.visibleProperty().set(false);
            }
        }
        else {
            ligne2.visibleProperty().set(false);
            ligne3.visibleProperty().set(false);
        }
        nomD.setText("  "+getTableauScore().get(ihm.getJeu().getJoueurs().size()-1).getNom());
        texteD.setText("  "+getTableauScore().get(ihm.getJeu().getJoueurs().size()-1).getScoreTotal()+"  ");
        imageD.setImage(new Image("images/icons/posD.png"));
        nomD.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
        texteD.styleProperty().set("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15");
        ligne1.styleProperty().set("-fx-background-color: rgba(255,255,255,0.2)");
        ligne2.styleProperty().set("-fx-background-color: rgba(255,255,255,0.2)");
        ligne3.styleProperty().set("-fx-background-color: rgba(255,255,255,0.2)");
        ligneD.styleProperty().set("-fx-background-color: rgba(255,255,255,0.2)");
        labelTitre.setStyle("-fx-background-color: rgba(255,134,36,0.4); -fx-border-width: 3; -fx-border-color: #ff8800");
    }

    private ArrayList<IJoueur> getTableauScore(){
        PriorityQueue<IJoueur> pq = new PriorityQueue<>(new Comparator<IJoueur>() {
            @Override
            public int compare(IJoueur o1, IJoueur o2) {
                return o2.getScoreTotal() - o1.getScoreTotal();
            }
        });
        pq.addAll(GestionJeu.getJeu().getJoueurs());
        ArrayList<IJoueur> tableauScore = new ArrayList<>();
        while (!pq.isEmpty()){
            tableauScore.add(pq.poll());
        }
        return tableauScore;
    }

    

}
