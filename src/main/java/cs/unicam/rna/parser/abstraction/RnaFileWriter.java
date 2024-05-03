package cs.unicam.rna.parser.abstraction;

import cs.unicam.rna.parser.model.RnaFileData;

/**
 * classe con la responsabilità di salvare dei dati 
 * su un dato file, da creare o sovrascrivere
 */
public interface RnaFileWriter {
	
	/**
	 * Metodo per la scrittura di un nuovo file contenente le informazioni
	 * @param molecules infomrazioni da scrivere
	 * @param path file in cui scriverle
	 * @return true se la scrittura del file è andata a buon fine, false, altrimenti
	 */
	public boolean writeAndSave(RnaFileData molecules, String path);
	
}
