package fr.umontpellier.iut.trainsJavaFX.vues;

import fr.umontpellier.iut.trainsJavaFX.GestionJeu;
import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.Plateau;
import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.Tuile;
import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.TuileVille;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.util.List;

/**
 * Cette classe présente les tuiles sur le plateau.
 * <p>
 * On y définit le listener à exécuter lorsque qu'une tuile a été choisie par l'utilisateur
 * ainsi que les bindings qui mettront à jour le plateau après la pose d'un rail ou d'une gare
 */

public class VuePlateau extends Pane {

    @FXML
    private ImageView mapVille;
    @FXML
    private Group tuiles;

    private DonneesPlateau plateau;

    private Scale scaleTuile;

    public VuePlateau () {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/plateau.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }        scaleTuile = new Scale();
        tuiles.getTransforms().add(scaleTuile);
    }

    public void creerBindings() {
        setDonneesPlateau(GestionJeu.getJeu().getPlateau());
        mapVille.setImage(plateau.getImageVille());
        definirScaleTransformation();
        bindRedimensionEtCentragePlateau();
        construirePlateau();
    }

    public void definirScaleTransformation() {
        Region conteneurParent = (Region) getParent();
        mapVille.fitWidthProperty().addListener((obs, oldVal, newVal) -> definirScalefacteur(conteneurParent.getWidth(), conteneurParent.getHeight()));
        mapVille.fitHeightProperty().addListener((obs, oldVal, newVal) -> definirScalefacteur(conteneurParent.getWidth(), conteneurParent.getHeight()));
    }

    private void definirScalefacteur(double largeurScene, double hauteurScene) {
        double scaleFactor = Math.min(largeurScene / plateau.getLargeurInitialePlateau(), hauteurScene / plateau.getHauteurInitialePlateau());
        scaleTuile.setX(scaleFactor);
        scaleTuile.setY(scaleFactor);
    }

    private void bindRedimensionEtCentragePlateau() {
        mapVille.fitWidthProperty().bind(((Region) getParent()).widthProperty());
        mapVille.fitHeightProperty().bind(((Region) getParent()).heightProperty());
        mapVille.layoutXProperty().bind(new DoubleBinding() { // Pour maintenir le plateau au centre
            {
                super.bind(widthProperty(),heightProperty());
            }
            @Override
            protected double computeValue() {
                double imageViewWidth = mapVille.getLayoutBounds().getWidth();
                return (getWidth() - imageViewWidth) / 2;
            }
        });
        tuiles.translateXProperty().bind(new DoubleBinding() {
            {super.bind(mapVille.boundsInParentProperty());}
            @Override
            protected double computeValue() {
                return mapVille.getBoundsInParent().getMinX();
            }
        });
    }

    private void construirePlateau() {
        int numTuile = 0;
        for (int bloc2Lignes = 0; bloc2Lignes < 4; bloc2Lignes++) {
            numTuile = creerLigneTuile(plateau.getDepartX(), plateau.getDepartY() + plateau.getDepY() * 3 * bloc2Lignes * 2, 10, numTuile);
            numTuile = creerLigneTuile(plateau.getDepartX() + plateau.getDepX(), plateau.getDepartY() + plateau.getDepY() * 3 * (bloc2Lignes * 2 + 1), 9, numTuile);
        }
    }

    private int creerLigneTuile(double decalX, double y, int nbTuilesparLigne, int numTuile) {
        for (int i = 0; i < nbTuilesparLigne; i++) {
            ajoutTuile(numTuile, decalX + plateau.getDepX() * 2 * i + 1, y);
            numTuile++;
        }
        return numTuile;
    }

    private void ajoutTuile(int numTuile, double x, double y) {
        //  A partir du sommet en haut à gauche, coordonnées x et y
        Group tuilePlateau = new Group();
        tuilePlateau.setId(String.valueOf(numTuile));
        SVGPath hexagone = new SVGPath();
        hexagone.setContent(
                "M" + x + "," + y + " " +
                        "L" + (x + plateau.getDepX()) + "," + (y - plateau.getDepY()) + " " +
                        (x + 2 * plateau.getDepX()) + "," + y + " " +
                        (x + 2 * plateau.getDepX()) + "," + (y + 2 * plateau.getDepY()) + " " +
                        (x + plateau.getDepX()) + "," + (y + 3 * plateau.getDepY()) + " " +
                        x + "," + (y + 2 * plateau.getDepY()) + " z"
        );
        tuilePlateau.getChildren().add(hexagone);

        int numJoueur = 0;
        List<? extends IJoueur> lesJoueurs = GestionJeu.getJeu().getJoueurs();
        Tuile tuileJeu = GestionJeu.getJeu().getTuiles().get(numTuile);
        for (IJoueur j : lesJoueurs) {
            Circle pionJoueur = creerPionJoueur(numJoueur, x + plateau.getDepX(), y + plateau.getDepY());
            numJoueur++;
            tuilePlateau.getChildren().add(pionJoueur);
            ajouteRail(tuileJeu, (Joueur) j, pionJoueur);
        }

        if (tuileJeu instanceof TuileVille) {
            creerGares(x, y, tuilePlateau, (TuileVille) tuileJeu);
        }
        tuiles.getChildren().add(tuilePlateau);
        tuilePlateau.setOnMouseClicked(mouseEvent -> choixTuile(mouseEvent));
    }

    private void creerGares(double x, double y, Group tuilePlateau, TuileVille tuileJeu) {
        Rectangle gare = new Rectangle(x + 2 + plateau.getDepX() * .5,y + 1.8 * plateau.getDepY(), plateau.getDepX(), plateau.getDepY()*.7);
        gare.setFill(Color.TRANSPARENT);
        tuilePlateau.getChildren().addAll(gare);
        ajouteGare(tuileJeu, gare);
    }

    private static void ajouteGare(TuileVille tuileVille, Rectangle gare) {
        tuileVille.nbGaresPoseesProperty().addListener((observableValue, number, t1) -> {
            if (tuileVille.getNbGares() != 0) {
                Image image = new Image("images/icons/gare" + tuileVille.getNbGares() + ".png");
                gare.setFill(new ImagePattern(image));
            }
        });
    }

    private void ajouteRail(Tuile t, Joueur j, Circle pionJoueur) {
        t.getRails().addListener((SetChangeListener<IJoueur>) change -> {
            if (t.hasRail(j)) {
                ajouteRailATuile(t, j, pionJoueur);
            }
        });
    }

    private Circle creerPionJoueur(int numPion, double centerX, double centerY) {
        double posPion = DonneesGraphiques.posPion;
        double radius = DonneesGraphiques.rayonPion;
        switch (numPion) {
            case 0 -> {
                centerX -= posPion;
                centerY -= posPion;
            }
            case 1 -> {
                centerX += posPion;
                centerY -= posPion;
            }
            case 2 -> {
                centerX += posPion;
                centerY += posPion * .55;
            }
            case 3 -> {
                centerX -= posPion;
                centerY += posPion * .55;
            }
        }
        Circle cerclePion = new Circle(centerX, centerY, radius);
        cerclePion.setFill(Color.TRANSPARENT);
        return cerclePion;
    }

    public void setDonneesPlateau(Plateau plateau) {
        switch (plateau) {
            case OSAKA -> this.plateau = DonneesPlateauBuilder.PLATEAU_OSAKA;
            case TOKYO -> this.plateau = DonneesPlateauBuilder.PLATEAU_TOKYO;
        }
    }

    private void ajouteRailATuile(Tuile t, Joueur j, Circle c) {
        System.out.println("Un rail a été posé par le joueur " + j.getNom());
    }

    private void choixTuile(MouseEvent event) {
        System.out.println("Une tuile a été choisie");
    }

}