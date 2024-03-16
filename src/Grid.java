import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class Grid {
	private final int nbCol;
	private final int nbLine;
	private final int nbMines;
	private final int nbCellules;
	private final Cellule[][] tabCellules;
	private int nbCelluleMarked;
	private int nbCelluleDiscovered;
	
	/*------------------------------------------------------------------\
		PREMIER CONSTRUCTEUR, (utilisé si on utilise pas la sauvegarde)
	\------------------------------------------------------------------*/
	Grid(int line, int col, int mines){
		this.nbCol = col;
		this.nbLine = line;
		this.nbMines = mines;
		this.nbCellules = line*col;
		nbCelluleMarked = 0;
		nbCelluleDiscovered = 0;
		tabCellules = new Cellule[line][col];

		generateGrid();
	}


	/*------------------------------------------------------------------\
		DEUXIEME CONSTRUCTEUR, (utilisé si on utilise la sauvegarde)
		La liste d'éléments est obtenue dans GamePlay avec la fonction
		load(), on recréer directement tabCellule avec les éléments de
		l'ancienne partie (pas besoin d'appeller generateGrid()) 
	\------------------------------------------------------------------*/
	Grid(List<String> element)
	{
		this.nbLine = Integer.parseInt(element.get(0));
		this.nbCol = Integer.parseInt(element.get(1));
		this.nbMines = Integer.parseInt(element.get(2));
		this.nbCellules = Integer.parseInt(element.get(3));
		this.nbCelluleMarked = Integer.parseInt(element.get(4));
		this.nbCelluleDiscovered = Integer.parseInt(element.get(5));
		tabCellules = new Cellule[nbLine][nbCol];

		//On recopie tout le tabCellules de l'ancienne partie
		int elem = 5;
		for(int i=0; i<nbLine; i++) {
			for(int j=0; j<nbCol; j++) {
				tabCellules[i][j] = 
				new Cellule(Boolean.parseBoolean(element.get(elem+1)), 
				Boolean.parseBoolean(element.get(elem+2)), 
				Boolean.parseBoolean(element.get(elem+3)),
				Integer.parseInt(element.get(elem+4)),
				Integer.parseInt(element.get(elem+5)),
				Integer.parseInt(element.get(elem+6)),
				element.get(elem+7));
				elem += 7;
			}
		}
	}


	/*------------------------------------------------------------------\
		GENERATION DE tabCellules : ici toutes les cellules sont
		initialisées et ajoutées dans un tableau
	\------------------------------------------------------------------*/	
	public void generateGrid() {
		for(int i=0; i<nbLine; i++) {
			for(int j=0; j<nbCol; j++) {
				tabCellules[i][j] = new Cellule(i,j);
			}
		}
		
		//Voisinage des cellules
		for(int i=0; i<nbLine; i++) {
			for(int j=0; j<nbCol; j++) {
				Cellule c = tabCellules[i][j];
				
				if(i > 0) {
					c.setVoisines(PositionVoisines.NORTH, tabCellules[i-1][j]);
				}
				
				if(i < nbLine - 1) {
					c.setVoisines(PositionVoisines.SOUTH, tabCellules[i+1][j]);
				}
				
				if(j > 0) {
					c.setVoisines(PositionVoisines.WEST, tabCellules[i][j-1]);
				}
				
				if(j < nbCol - 1) {
					c.setVoisines(PositionVoisines.EAST, tabCellules[i][j+1]);
				}
				
				if(i > 0 && j > 0) {
					c.setVoisines(PositionVoisines.NWEST, tabCellules[i-1][j-1]);
				}
				
				if(i > 0 && j < nbCol - 1) {
					c.setVoisines(PositionVoisines.NEAST, tabCellules[i-1][j+1]);
				}
				
				if(i < nbLine - 1 && j > 0) {
					c.setVoisines(PositionVoisines.SEAST, tabCellules[i+1][j-1]);
				}
				
				if(i < nbLine - 1 && j < nbCol - 1) {
					c.setVoisines(PositionVoisines.SWEST, tabCellules[i+1][j+1]);
				}
			}
		}
		
		//Placement des mines
		int minesToPlace = nbMines;
		while(minesToPlace > 0){
			int cordX = (int)(Math.random()*nbLine);
			int cordY = (int)(Math.random()*nbCol);
			
			if(tabCellules[cordX][cordY].getMine() == false) {
				tabCellules[cordX][cordY].setMine(true);
				minesToPlace--;
			}
		}
		
		//Compte les mines autour d'une cellule 
		for(int i=0; i<nbLine; i++) {
			for(int j=0; j<nbCol; j++) {
				int nbMineAutour = 0;
				
				EnumMap<PositionVoisines, Cellule> VOISINES; 
				VOISINES = tabCellules[i][j].getVoisines();
				
				for(Cellule c : VOISINES.values()) {
					if(c.getMine())
						nbMineAutour ++;
				}
				
				tabCellules[i][j].setNbMineAutour(nbMineAutour);
				
			}
		}
		
		
	}
	
	/*------------------------------------------------------------------\
				AFFICHAGE DE LA GRILLE DANS LE TERMINAL
	\------------------------------------------------------------------*/	
	public void printGrid() {

		System.out.print("  ");

		//Afichage numéro de colonnes
		for(int col=0; col<nbCol; col++) {
			if(col < 9)
				System.out.print("  ");
			else
				System.out.print(" ");
			
			System.out.print(col+1+" ");
		}
		
		System.out.println();
		System.out.println();
		
		//Ici les lignes
		for(int line=0; line<nbLine; line++) {
			
			if(line < 9)
				System.out.print(line+1+"  ");
			else
				System.out.print(line+1+" ");
			
			for(int col=0; col<nbCol; col++) {
				Cellule c = tabCellules[line][col];
				System.out.print(c.getMotif());
			}
			
			System.out.println();
			System.out.println();	
		}
	}
	
	/*------------------------------------------------------------------\
		UNCOVER : Permet de découvrir/marquer une cellule
		Appelle CelluleVideAdj (fonction récursive) pour dévoiler toutes
		les cellules autour d'une cellule vide.
	\------------------------------------------------------------------*/

	public Boolean uncover(int choix, int l, int c)
	{
		Boolean gameOver = false;
		if (choix == 2)
		{
			if (getTabCellules()[l][c].getMarked() == false)
			{
				getTabCellules()[l][c].setMarked(true);
			}
			else
			{
				getTabCellules()[l][c].setMarked(false);
			}
		}
		else 
		{
			if(getTabCellules()[l][c].getMine() == false){
				if (getTabCellules()[l][c].getNbMineAutour() == 0)
				{
					CelluleVideAdj(l,c);
				}
				else
				{
					nbCelluleDiscovered ++;
				}
			}

			else {
				gameOver = true;
			}

			getTabCellules()[l][c].setDiscovered(true);
		}
		return gameOver;
	}

	/*------------------------------------------------------------------\
		Lorsque l'utilisateur dévoile une cellule vide, on dévoile 
		toutes les cellules vides adjacentes et on dévoile toutes les
		cellules autour d'une cellule vide
	\------------------------------------------------------------------*/	

	public void CelluleVideAdj(int ligne, int col)
	{
		Cellule c = tabCellules[ligne][col];

		//On ne regarde pas les cellules déjà traitées
		if (c.getDiscovered() || c.getMarked())
		{
			return;
		}
		else
		{
			c.setDiscovered(true);
			nbCelluleDiscovered ++;
			//Le nombre de cellules découvertes est la condition de victoire
		}

		//Cas de la cellule vide
		if (c.getNbMineAutour() == 0)
		{
			for (Cellule voisine : c.getVoisines().values()){
				CelluleVideAdj(voisine.getX(), voisine.getY());
			}
		}
		else
		{
			return;
		}
	}

	/*------------------------------------------------------------------\
		SAVE : Pour sauvegarder dans un fichier texte une partie en
		cour, on récupère toutes les variables nécessaires pour le
		constructeur de Grid et de Cellule pour remplir à nouveau
		le tabCellule
	\------------------------------------------------------------------*/

	public void save(Path path) {
		List<String> ecrire = new ArrayList<>();

		//Première ligne contient les variables pour Grid
		String information = Integer.toString(nbLine) + "_" + Integer.toString(nbCol) +
		"_" + Integer.toString(nbMines) + "_" + Integer.toString(nbCellules) +
		"_" + Integer.toString(nbCelluleMarked) + "_" + Integer.toString(nbCelluleDiscovered);
		
		ecrire.add(information);
		
		//Ici on récupère le contenu des cellules avec la fonction getAll()
		for(int i = 0; i < nbLine; i++)
		{
			for (int j = 0; j < nbCol; j++)
			{
				ecrire.add(tabCellules[i][j].getAll());
			}
		}
		
		//On écrit le tout dans le fichier texte "save.txt"
		try {
			Files.write(path, ecrire, StandardCharsets.UTF_8);
		} catch (IOException e)
		{
			System.err.println("Erreur écriture fichier txt");
		}
	}

	/*------------------------------------------------------------------\
							GETTER ET SETTER
	\------------------------------------------------------------------*/	
	public int getNbCellules() {
		return nbCellules;
	}

	public int getNbMines() {
		return nbMines;
	}
	
	public Cellule[][] getTabCellules(){
		return tabCellules;
	}
	
	public int getNbLine() {
		return nbLine;
	}
	
	public int getNbCol() {
		return nbCol;
	}
	
	public int getNbCelMarked() {
		return nbCelluleMarked;
	}

	public int getNbCelDiscovered(){
		return nbCelluleDiscovered;
	}
	
	public void setNbCelMarked(int nbDiscovered) {
		this.nbCelluleMarked = nbDiscovered;
	}
}
