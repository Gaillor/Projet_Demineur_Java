import java.util.*;

enum PositionVoisines{
	NORTH,
	SOUTH,
	EAST,
	WEST,
	NEAST,
	NWEST,
	SEAST,
	SWEST;
}

public class Cellule {
	private boolean isMine;
	private boolean isMarked;
	private boolean isDiscovered;
	private int x;
	private int y;
	private int nbMineAutour;
	private String motif;
	private EnumMap<PositionVoisines, Cellule> POSITION_VOISINES_MAP;
	
	/*------------------------------------------------------------------\
		PREMIER CONSTRUCTEUR : Utilisé pour le début d'une partie
	\------------------------------------------------------------------*/
	Cellule(int x, int y){
		this.isMine = false;
		this.isMarked = false;
		this.isDiscovered = false;
		this.x = x;
		this.y = y;
		this.motif = "░ ";
		this.POSITION_VOISINES_MAP = new EnumMap<>(PositionVoisines.class);		
	}


	/*------------------------------------------------------------------\
		DEUXIEME CONSTRUCTEUR : Utilisé pour la sauvegarde.
	\------------------------------------------------------------------*/
	Cellule(Boolean isMine, Boolean isMarked, Boolean isDiscovered, int x, int y, int nbMineAutour, String motif)
	{
		this.isMine = isMine;
		this.isMarked = isMarked;
		this.isDiscovered = isDiscovered;
		this.x = x;
		this.y = y;
		this.nbMineAutour = nbMineAutour;
		this.motif = motif;
		this.POSITION_VOISINES_MAP = new EnumMap<>(PositionVoisines.class);
	}
	
	
	/*------------------------------------------------------------------\
							GETTER ET SETTER
	\------------------------------------------------------------------*/	
	public boolean getMine() {
		return isMine;
	}
	
	public void setMine(boolean mine) {
		this.isMine = mine;
	}
	
	public boolean getMarked() {
		return isMarked;
	}
	
	public void setMarked(boolean mark) {
		this.isMarked = mark;
	}	
	
	public boolean getDiscovered() {
		return isDiscovered;
	}
	
	public void setDiscovered(boolean discovered) {
		this.isDiscovered = discovered;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x1) {
		this.x = x1;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y1) {
		this.y = y1;
	}
	
	public String getMotif() {
		if(!isDiscovered) {
			if(isMarked)
				motif = "  🚩"; //Marque
			else
				motif = " ☲☲ ";//Motif de base
		}
		
		else {
			if(isMine) {//BOMBE
				motif = "  ✹ ";
			}
			
			else { 
				//Cellule vide et aucune bombe dans une cellule voisine
				if(getNbMineAutour() == 0){	
					motif = "    ";
				}

				//On affiche le nombre de bombes autour de la cellule vide
				else if(getNbMineAutour() > 0) {
					Integer a = getNbMineAutour();
					motif = "  "+a.toString()+" "; 
				}
			}
		}
		
		return motif;
	}
	
	public int getNbMineAutour() {
		return nbMineAutour;
	}
	
	public void setNbMineAutour(int nbMine) {
		this.nbMineAutour = nbMine;
	}
	
	public EnumMap<PositionVoisines, Cellule> getVoisines() {
		return POSITION_VOISINES_MAP;
	}
	
	public void setVoisines(PositionVoisines p, Cellule cellule) {
		POSITION_VOISINES_MAP.put(p, cellule);
	}
	
	//Pour la sauvegarde
	public String getAll()
	{
		String variables = Boolean.toString(isMine) + "_" + Boolean.toString(isMarked)  +  
		"_" + Boolean.toString(isDiscovered) + "_" + Integer.toString(x) + "_" +
		Integer.toString(y) + "_" + Integer.toString(nbMineAutour) + "_" +
		motif + "_" ;

		return variables;
	}
}
