package fr.umontpellier.iut.trainsJavaFX.mecanique;

import fr.umontpellier.iut.trainsJavaFX.IJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.cartes.*;
import fr.umontpellier.iut.trainsJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.trainsJavaFX.mecanique.etatsJoueur.debuttour.DebutTour;
import fr.umontpellier.iut.trainsJavaFX.mecanique.etatsJoueur.initialisation.InitialisationPositionDepart;
import fr.umontpellier.iut.trainsJavaFX.mecanique.plateau.Tuile;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur implements IJoueur {
    private final Jeu jeu;
    private final String nom;
    private final IntegerProperty argent;
    private final IntegerProperty pointsRails;
    private final IntegerProperty nbJetonsRails;
    private final IntegerProperty score;
    private final ListeDeCartes main;
    private final ListeDeCartes pioche;
    private final ListeDeCartes defausse;
    private final ListeDeCartes cartesEnJeu;
    private final ListeDeCartes cartesRecues;
    private final ListeDeCartes cartesAChoisir;
    private final List<EffetTour> listeEffets;
    private final CouleurJoueur couleur;


    public Joueur(Jeu jeu, String nom, CouleurJoueur couleur) {
        this.jeu = jeu;
        this.nom = nom;
        this.couleur = couleur;
        argent = new SimpleIntegerProperty(0);
        score = new SimpleIntegerProperty(0);
        pointsRails = new SimpleIntegerProperty(0);
        nbJetonsRails = new SimpleIntegerProperty(20);
        main = new ListeDeCartes();
        defausse = new ListeDeCartes();
        pioche = new ListeDeCartes();
        cartesEnJeu = new ListeDeCartes();
        cartesRecues = new ListeDeCartes();
        cartesAChoisir = new ListeDeCartes();
        listeEffets = new ArrayList<>();

        // créer 7 Train omnibus (non disponibles dans la réserve)
        pioche.addAll(FabriqueListeDeCartes.creerListeDeCartes("Train omnibus", 7));
        // prendre 2 Pose de rails de la réserve
        for (int i = 0; i < 2; i++) {
            pioche.add(jeu.prendreDansLaReserve("Pose de rails"));
        }
        // prendre 1 Gare de la réserve
        pioche.add(jeu.prendreDansLaReserve("Gare"));
        pioche.melanger();
        piocherEnMain(5);
//        testCasPrecis(1);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public boolean hasEffet(EffetTour effet) {
        return listeEffets.contains(effet);
    }

    public int nbEffet(EffetTour effet) {
        return (int) listeEffets.stream().filter(e -> e.equals(effet)).count();
    }

    public CouleurJoueur getCouleur() {
        return couleur;
    }

    public int getArgent() {
        return argent.getValue();
    }

    public int getNbJetonsRails() {
        return nbJetonsRails.getValue();
    }

    public int getScore() {
        return score.getValue();
    }

    public void incrementerScore() {
        score.setValue(score.getValue() + 1);
    }

    public void incrementerScore(int i) {
        score.setValue(score.getValue() + i);
    }

    public ListeDeCartes getDefausse() {
        return defausse;
    }

    public ListeDeCartes getPioche() {
        return pioche;
    }

    public ListeDeCartes getCartesEnJeu() {
        return cartesEnJeu;
    }

    public ListeDeCartes getCartesRecues() {
        return cartesRecues;
    }

    public ListeDeCartes getMain() {
        return main;
    }

    public String getNom() {
        return nom;
    }

    public void ajouterMain(Carte carte) {
        main.add(carte);
    }

    public void mettreSurPioche(Carte carte) {
        pioche.add(0, carte);
    }

    public Carte recevoirFerraille() {
        if (!hasEffet(EffetTour.DEPOTOIR)) {
            return recevoir("Ferraille");
        }
        return null;
    }

    /**
     * Retire une carte dans la réserve et la place dans les cartes reçues du joueur
     * (s'il en reste)
     *
     * @param nomCarte nom de la carte à prendre dans la réserve
     * @return la carte retirée dans la réserve ou null si aucune carte disponible
     */
    public Carte recevoir(String nomCarte) {
        Carte c = jeu.prendreDansLaReserve(nomCarte);
        if (c != null) {
            cartesRecues.add(c);
        }
        return c;
    }

    /**
     * Place une carte dans les cartes reçues du joueur
     * Ne fait rien si la carte est null
     */
    public void recevoir(Carte carte) {
        if (carte != null) {
            cartesRecues.add(carte);
        }
    }

    /**
     * Retire une carte dans la réserve et la place dans la main du joueur
     * (s'il en reste)
     *
     * @param nomCarte nom de la carte à prendre dans la réserve
     * @return la carte retirée dans la réserve ou null si aucune carte disponible
     */
    public Carte recevoirEnMain(String nomCarte) {
        Carte c = jeu.prendreDansLaReserve(nomCarte);
        if (c != null) {
            main.add(c);
        }
        return c;
    }

    public ListeDeCartes toutesLesCartes() {
        ListeDeCartes toutesLesCartes = new ListeDeCartes();
        toutesLesCartes.addAll(main);
        toutesLesCartes.addAll(cartesEnJeu);
        toutesLesCartes.addAll(cartesRecues);
        toutesLesCartes.addAll(defausse);
        return toutesLesCartes;
    }

    /**
     * Prend et retourne la première carte de la pioche.
     * Si la pioche est vide, la méthode commence par mélanger toute la défausse
     * dans la pioche.
     *
     * @return la carte piochée ou {@code null} si aucune carte disponible
     */
    public Carte piocher() {
        if (pioche.isEmpty()) {
            pioche.addAll(defausse);
            defausse.clear();
            pioche.melanger();
        }
        if (pioche.isEmpty()) {
            return null;
        }
        return pioche.remove(0);
    }

    public ObservableList<Carte> piocher(int n) {
        ObservableList<Carte> cartes = FXCollections.observableArrayList();
        for (int i = 0; i < n; i++) {
            Carte c = piocher();
            if (c == null) {
                break;
            }
            cartes.add(c);
        }
        return cartes;
    }

    public void piocherEnMain() {
        main.add(piocher());
    }

    public void piocherEnMain(int n) {
        main.addAll(piocher(n));
    }

    public void defausser(Carte carte) {
        if (carte != null) {
            defausse.add(carte);
        }
    }

    public void defausser(Collection<Carte> cartes) {
        defausse.addAll(cartes);
    }

    public void choisirPositionDepart() {
        etatCourant = new InitialisationPositionDepart(this);
        etatCourant.choisirTuile();
    }

    public void jouerTour() {
        etatCourant = new DebutTour(this);
    }


    public Set<String> actionsRestantAJouer() {
        // Préparer la liste de choix possibles
        Set<String> choixPossibles = new HashSet<>();
        // Cartes jouables en main
        choixPossibles.addAll(main.stream()
                .filter(c -> c.peutEtreJouee(this))
                .map(Carte::getNom)
                .collect(Collectors.toSet()));
        // Si le joueur peut poser des rails
        if (pointsRails.getValue() > 0 && nbJetonsRails.getValue() > 0) {
            for (String index : getPositionsRailDisponibles()) {
                choixPossibles.add(index);
            }
        }
        // Cartes que le joueur peut acheter
        for (String carte : getCartesAchatPossibles()) {
            choixPossibles.add("ACHAT:" + carte);
        }
        return choixPossibles;
    }

    public Collection<String> getRailsJouables() {
        Set<String> choixPossibles = new HashSet<>();
        // Si le joueur peut poser des rails
        if (pointsRails.getValue() > 0 /*&& nbJetonsRails.getValue() > 0*/) {
            for (String index : getPositionsRailDisponibles()) {
                choixPossibles.add(index);
            }
        }
        return choixPossibles;
    }

    public void recyclerFerraille() {
        List<Carte> ferrailles = main
                .stream()
                .filter(c -> c.hasType(TypeCarte.FERRAILLE))
                .toList();
        for (Carte c : ferrailles) {
            main.remove(c);
            jeu.remettreCarteDansLaReserve(c);
        }
    }

    public void construireRail(int index) {
        this.pointsRails.setValue(this.pointsRails.getValue() - 1);
        Tuile tuile = jeu.getTuile(index);
        tuile.onConstruitRail(this);
        incrementerScore(tuile.getNbPointsVictoire());
        changerArgent(this.argent.getValue() - tuile.getSurcout(this));
        tuile.ajouterRail(this);
    }

    public Carte acheterCarte(String nomCarte) {
        Carte carte = recevoir(nomCarte);
        changerArgent(argent.getValue() - carte.getCout());
        carte.onAchat(this);
        return carte;
    }

    public void placerCarteAcheteeSurDeck(Carte carte) {
        cartesRecues.remove(carte);
        pioche.add(0, carte);
    }

    public void jouerCarte(String nomCarte) {
        Carte carte = main.retirer(nomCarte);
        cartesEnJeu.add(carte);
        incrementerArgent(carte.getValeur());
        carte.jouer(this);
    }

    public List<String> getCartesAchatPossibles() {
        return jeu.getCartesDisponiblesEnReserve().stream()
                .filter(carte -> carte.peutEtreAchetee(this))
                .map(Carte::getNom)
                .toList();
    }

    void changerArgent(int nouvelleValeur) {
        argent.setValue(nouvelleValeur);
    }

    /**
     * Termine le tour du joueur
     * <p>
     * - Le compteur d'argent est remis à 0
     * - Les cartes en main, en jeu et gagnées sont défaussées
     * - Le joueur pioche 5 cartes en main
     */
    public void finaliserLeTour() {
        changerArgent(0);
        pointsRails.setValue(0);
        listeEffets.clear();
        // défausse la main et les cartes en jeu
        defausse.addAll(cartesEnJeu);
        cartesEnJeu.clear();
        defausse.addAll(cartesRecues);
        cartesRecues.clear();
        defausse.addAll(main);
        main.clear();

        // pioche 5 cartes en main
        piocherEnMain(5);
    }

    /**
     * @return le score total du joueur (score courant + points des cartes + points
     *         des villes et lieux éloignés)
     */
    public int getScoreTotal() {
        int scoreTotal = score.getValue();
        for (Carte c : toutesLesCartes()) {
            scoreTotal += c.getNbPointsVictoire();
        }
        for (Tuile tuile : jeu.getTuiles()) {
            if (tuile.hasRail(this)) {
                scoreTotal += tuile.getNbPointsVictoire();
            }
        }
        return scoreTotal;
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un dictionnaire de
     * valeurs sérialisables (qui sera converti en JSON pour l'envoyer à l'interface
     * graphique)
     */
    Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("nom", nom),
                Map.entry("couleur", couleur),
                Map.entry("scoreTotal", getScoreTotal()),
                Map.entry("argent", argent),
                Map.entry("rails", pointsRails),
                Map.entry("nbJetonsRails", nbJetonsRails),
                Map.entry("main", main.dataMap()),
                Map.entry("defausse", defausse.dataMap()),
                Map.entry("cartesEnJeu", cartesEnJeu.dataMap()),
                Map.entry("cartesRecues", cartesRecues.dataMap()),
                Map.entry("pioche", pioche.dataMap()),
                Map.entry("listeEffets", listeEffets.stream().map(EffetTour::toString).toList()),
                Map.entry("actif", jeu.getJoueurCourant() == this));
    }

    public void ajouterEffet(EffetTour effet) {
        listeEffets.add(effet);
    }

    public void incrementerRails() {
        if (nbJetonsRails.getValue() > 0)
            pointsRails.setValue(pointsRails.getValue() + 1);
    }

    public void incrementerArgent(int i) {
        changerArgent(argent.getValue() + i);
    }

    public void remettreCarteDansLaReserve(Carte c) {
        jeu.remettreCarteDansLaReserve(c);
    }

    public void ecarterCarte(Carte c) {
        jeu.ecarterCarte(c);
    }

    public List<Carte> getCartesDisponiblesEnReserve() {
        return jeu.getCartesDisponiblesEnReserve();
    }

    public Collection<String> getPositionsGareDisponibles() {
        return jeu.getPositionsGareDisponibles();
    }

    public Collection<String> getPositionsRailDisponibles() {
        return jeu.getPositionsRailDisponibles(this);
    }

    public void ajouterGare(int i) {
        jeu.ajouterGare(i);
    }

    public int getNbJetonsGare() {
        return jeu.getNbJetonsGare();
    }

    /**
     * Gestion des états du joueur courant
     */
    private EtatJoueur etatCourant;

    public void setEtatCourant(EtatJoueur etatCourant) {
        this.etatCourant = etatCourant;
    }

    public EtatJoueur getEtatCourant() {
        return etatCourant;
    }

    public void setCartesAChoisir(ListeDeCartes cartesAChoisir) {
        this.cartesAChoisir.clear();
        this.cartesAChoisir.addAll(cartesAChoisir);
    }

    public void reposerCartesChoisiesDansLOrdreInitial() {
        for (int i = 0; i < 4; i++) {
            pioche.add(i, cartesAChoisir.get(i));
        }
    }

    public void diminueNbjetonsRails() {
        if (nbJetonsRails.getValue() > 0)
            nbJetonsRails.setValue(this.nbJetonsRails.getValue() - 1);
    }

    /**
     * Publication des propriétés
     */
    @Override
    public ListeDeCartes mainProperty() {
        return main;
    }

    @Override
    public ListeDeCartes defausseProperty() {
        return defausse;
    }

    @Override
    public ListeDeCartes piocheProperty() {
        return pioche;
    }

    @Override
    public ListeDeCartes cartesEnJeuProperty() {
        return cartesEnJeu;
    }

    @Override
    public ListeDeCartes cartesRecuesProperty() {
        return cartesRecues;
    }

    @Override
    public ListeDeCartes cartesAChoisir() {
        return cartesAChoisir;
    }

    @Override
    public IntegerProperty argentProperty() {
        return argent;
    }

    @Override
    public IntegerProperty nbJetonsRailsProperty() {
        return nbJetonsRails;
    }

    @Override
    public IntegerProperty pointsRailsProperty() {
        return pointsRails;
    }

    @Override
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Gestionnaires des demandes du joueur
     */
    @Override
    public void uneCarteDeLaMainAEteChoisie(String carteEnMain) {
        getEtatCourant().carteEnMainChoisie(carteEnMain);
    }

    @Override
    public void uneCarteEnJeuAEteChoisie(String carteEnMain) {
        getEtatCourant().carteEnJeuChoisie(carteEnMain);
    }

    @Override
    public void laPiocheAEteChoisie() {
        getEtatCourant().piocheChoisie();
    }

    @Override
    public void laDefausseAEteChoisie() {
        getEtatCourant().defausser();
    }

    @Override
    public void recevoirArgentAEteChoisi() {
        getEtatCourant().recevoirArgent();
    }

    private void testCasPrecis(int nbCartesDeLaMainAEnlever) {
        for (int i = 0; i < nbCartesDeLaMainAEnlever; i++)
            main.remove(0);
/*      //          testCasPrecis(2);
        main.addAll(new CentreDeControle());
        pioche.add(0, new TrainExpress());
        pioche.add(0, new Appartement());*/

/*      //          testCasPrecis(1);
        main.addAll(new FeuDeSignalisation());*/

/*        //          testCasPrecis(1);
        main.addAll(new CentreDeRenseignements());*/

/*                //          testCasPrecis(2);
        main.addAll(new ParcDAttractions(), new TrainExpress());*/

/*        //          testCasPrecis(1);
        main.addAll(new Echangeur());*/

/*        // test fin du jeu
        nbJetonsRails.setValue(2);*/
    }


}
