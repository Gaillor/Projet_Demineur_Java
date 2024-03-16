import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

enum Difficulte{
	FACILE,
	MOYEN, 
	DIFFICILE, 
	LEGENDE;
	}

public class GamePlay {
	private boolean gameOver;
	private boolean gameWin;
	private boolean quit;
	private EnumMap<Difficulte, Integer> DIFFICULTE_MAP;
	private List<String> element = new ArrayList<>();
	

	/*------------------------------------------------------------------\
								CONSTRUCTEUR
	\------------------------------------------------------------------*/
	GamePlay(){
		this.gameOver = false;
		this.gameWin = false;
		this.quit = false;
		
		//La difficulté est choisie par l'utilisateur
		this.DIFFICULTE_MAP = new EnumMap<>(Difficulte.class);
		
		//Pourcentage de mines selon la difficulte
		this.DIFFICULTE_MAP.put(Difficulte.FACILE, 10);
		this.DIFFICULTE_MAP.put(Difficulte.MOYEN, 30);
		this.DIFFICULTE_MAP.put(Difficulte.DIFFICILE, 45);
		this.DIFFICULTE_MAP.put(Difficulte.LEGENDE, 68);
	}
	
	/*------------------------------------------------------------------\
		NOMBRE DE BOMBES : On utilisera ce nombre pour générer la grille
	\------------------------------------------------------------------*/	
	int getBombes(String niveau, int a, int b) {
		Integer Bombes = this.DIFFICULTE_MAP.get(Difficulte.valueOf(niveau.toUpperCase()));
		int bombes = Bombes.intValue();
		bombes = (int) (a*b*(bombes/100.0));
		
		return bombes;
	}
	
	
	public void jouer() {
		int line;
		int col;
		int bombes;
		int cpt = 0; 
		int l;
		int c;
		int bonneCasesMarked = 0;
		int bonneCasesDiscovered = 0;
		String niveau;
		int sauvegarder;
		int choix;

		Grid grille = null;
        Scanner scanner = new Scanner(System.in);
		
		//Boucle de jeu
        while(!gameWin && !gameOver && !quit) {

			/*------------------------------------------------------------------\
				INNITIALISATION DE LA GRILLE / AFFICHAGE + CHOIX DE 
				L'UTILISATEUR
			\------------------------------------------------------------------*/	

			if(cpt == 0) {
				clearTerminal();
				System.out.print("\t---------   Démineur   ---------\n");

				Path path = Paths.get("save.txt");
				if (Files.exists(path))
				{
					System.out.print("Voulez-vous reprendre à votre dernière sauvegarde ? (1-Oui/2-Non) \n");
					System.out.print("Veuillez rentrer l'entier correspondant à votre réponse : ");
					sauvegarder = scanner.nextInt();
					scanner.nextLine();
				}
				else
				{
					sauvegarder = 2;
				}
				if (sauvegarder == 1)
				{
					//load récupère tout les élements sauvegardés 
					//dans une liste de String
					load(path);
					grille = new Grid(element);
				}
				else 
				{
					//Niveau + dimension
					System.out.print("\tEntrez niveau de jeu\n");
					System.out.print("\tFacile/Moyen/Difficile/Legende: ");
					niveau = scanner.nextLine();
					System.out.println();

					System.out.print("\tEntrez la largeur de la grille: ");
					col = scanner.nextInt();

					System.out.print("\tEntrez la hauteur de la grille: ");
					line = scanner.nextInt();
				
					//Initialisation de nombre de bombes
					bombes = getBombes(niveau,line,col);

					//Si problème
					if(line <= 0 || col <= 0 || bombes < 0) {
						System.out.println("Problème d'initialisation de la grille");
						scanner.close();
						return;
					}
				
					//Initialisation de la nouvelle grille
					grille = new Grid(line, col,bombes);
					System.out.println();
				}
	
				cpt = 1;
			}
			/*--------------------------------------------------------------------------------------*/
			/*   	Le problème d'affichage en DOUBLE se trouve ici, en gros le prochain			*/
			/* 		scanner.nextLine() prend directement comme saisie, l'affichage des numéros		*/
			/* 		de colonnes de la grille au prochain passage dans la boucle. Donc il n'attend	*/
			/* 		pas la saisie de l'utilisateur puisqu'il considère que ces numéros ont déjà		*/
			/* 		été saisis. Cela crée donc le décalage d'affichage en 2 fois à chaque passage	*/
			/*--------------------------------------------------------------------------------------*/
			clearTerminal();
			grille.printGrid();
			System.out.print("1(quitter) - 2(Marquer) - 3(Decouvrir) - 4(quitter et sauvegarder)\n");
			System.out.print("Veuillez rentrer l'entier correspondant à votre réponse : ");
/* ----	*/	
/* ICI	*/	choix = scanner.nextInt();
/* ----	*/	
			//L'utilisateur marque/dévoile une case
			if (choix == 2 || choix == 3){
				System.out.print("\tNumero de ligne: ");
				l = scanner.nextInt();
				l = l-1;
				
				System.out.print("\tNumero de colonne: ");
				c = scanner.nextInt();
				c = c-1;
				
				//uncover retourne si l'utilisateur à découvert une bombe
				//cette fonction a un effet de bord sur la grille
				gameOver = grille.uncover(choix, l, c);
				
				if (gameOver) // =BOMBE
				{
					clearTerminal();
					grille.printGrid();
					System.out.println("\tBOOOOOM");
					System.out.println("\tVous avez perdu");
				}
			}

			else if(choix == 1)
			{
				quit = true;
				System.out.println("\n\tVous quittez la partie.\n");
			}
				
			else if (choix == 4)
			{
				Path saveFile = Paths.get("save.txt");
				grille.save(saveFile);
				quit = true;
				System.out.println("\n\tVous sauvegardez et quittez la partie.\n");
			}

			//Condition de victoire
			if(grille.getNbCellules() - grille.getNbCelDiscovered() == grille.getNbMines()) {
				clearTerminal();
				grille.printGrid();	
				System.out.println("\tToutes les cases vides ont été découvertes");
				System.out.println("\tVous avez gagné!");
				gameWin = true;
			}
			
		}
		
		scanner.close();
	}

	/*------------------------------------------------------------------\
		FONCTION POUR CLEAR LE TERMINAL (sous Linux uniquement)
	\------------------------------------------------------------------*/
	private void clearTerminal()
	{
		try {
			new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}


	/*------------------------------------------------------------------\
		FONCTION QUI LIT LE FICHIER TEXTE "save.txt", sépare tout les 
		éléments séparés du caractère "_" et les range dans une liste
		de String. La première ligne est pour le contructeur Grid(), le
		reste est pour toutes les cellules.
	\------------------------------------------------------------------*/
	public void load(Path path) {
		String[] lignes;
		try
		{
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			for (String line : lines)
			{
				lignes = line.split("_");
				for (String elements : lignes)
				{
					element.add(elements);
				}
			}

		}
		catch (IOException e)
		{
			System.err.println("Erreur lecture fichier");
		}
	}
}
