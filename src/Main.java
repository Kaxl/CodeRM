import Alteration.Alteration;
import CodeRM.ReedMuller;
import Utilities.ParsePGM;

import java.io.*;
import java.util.*;
import java.math.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // permet de prendre les entrées pour le menu
        // soit du clavier, d'un fichier ou de la ligne de commande
        Scanner in;
        switch (args.length) {
            case 0:
                in = new Scanner(System.in);
                break;
            case 1:
                in = new Scanner(new File(args[0]));
                break;
            default:
                String source = args[0];
                for (int i = 1; i < args.length; i++) source += " " + args[i];
                in = new Scanner(source);
        }

        // les impressions des menus sont envoyées sur le canal d'erreur
        // pour les différencier des sorties de l'application
        // lesquelles sont envoyées sur la sortie standard

        // choix des paramètres
        System.err.println("Mot en clair de (r+1)-bits à encoder sur (2^r)-bits.");
        System.err.println("Choisir la valeur de r: ");
        int r = in.nextInt();
        System.err.println("\nLe seuil de bruit est la probabilité d'inverser un bit.");
        System.err.println("Choisir un seuil de bruit (nombre entre 0.0 et 1.0): ");
        double seuil = in.nextDouble();


        ReedMuller rm = new ReedMuller(r);

        // traiter un mot ou une image
        System.err.println("\nMenu initial");
        System.err.println("0: Quitter");
        System.err.println("1: Traiter un mot");
        System.err.println("2: Traiter une image");
        int mode = in.nextInt();

        BigInteger mot = new BigInteger("0");
        String buffer = "";

        // opération à effectuer sur le mot ou l'image
        String menu = "\nMenu opérations\n"
            + "0: Quitter\n"
            + "1: Encoder\n"
            + "2: Décoder\n"
            + "3: Bruiter\n"
            + "4: Débruiter\n"
            + "5: Réinitialiser\n"
            + "Opération choisie:";
        int choix = 5;
        if (mode == 1) {
            do {
                switch (choix) {
                    case 1:
                        // Encodage d'un mot.
                        mot = rm.encode(mot);
                        break;
                    case 2:
                        // Décodage d'un mot.
                        mot = rm.decode(mot);
                        break;
                    case 3:
                        // Bruitage d'un mot.
                        mot = Alteration.alter(mot, seuil);
                        break;
                    case 4:
                        // Débruitage d'un mot.
                        mot = rm.unalter(mot);
                        break;
                    case 5:
                        System.err.println("\nEntrer un mot (en décimal)");
                        mot = new BigInteger(in.next());
                        break;
                }
                if (choix != 5) {
                    System.err.println("Valeur du mot courant (en décimal):");
                    System.out.println(mot);
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        } else if (mode == 2) {
            do {
                String fileName = "";
                switch (choix) {
                    case 1:
                        // Encodage de l'image.
                        buffer = rm.encode(buffer);
                        break;
                    case 2:
                        // Décodage de l'image.
                        buffer = rm.decode(buffer);
                        break;
                    case 3:
                        // Bruitage de l'image.
                        buffer = Alteration.alter(buffer, seuil);
                        break;
                    case 4:
                        // Débruitage de l'image.
                        buffer = rm.unalter(buffer);
                        break;
                    case 5:
                        System.err.println("Nom du fichier de l'image à charger (format pgm):");
                        fileName = in.next();
                        // Lecture de l'image et stockage dans le buffer.
                        buffer = ParsePGM.read(fileName);
                        break;
                }
                if (choix != 5) {
                    System.err.println("Nom du fichier où sauver l'image courante (format pgm):");
                    fileName = in.next();
                    // Sauve l'image courante (le buffer) au format pgm le supprime avant.
                    ParsePGM.writeString(fileName, buffer);
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        }
    }
}

//Encoder => Décoder ou Bruiter ou Réinitialiser
//Décoder => Encoder ou Réinitialiser
//Bruiter => Débruiter ou Bruiter ou Réinitialiser
//Débruiter => Bruiter ou Décoder ou Réinitialiser









