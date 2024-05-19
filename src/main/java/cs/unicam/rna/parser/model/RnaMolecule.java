package cs.unicam.rna.parser.model;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cs.unicam.rna.parser.exception.RnaParsingException;

/**
 * classe che contiene tutti i dati relativi ad un rna
 * @author Marvin Sincini - Università di Informatica di Camerino - matricola 118311
 */
public class RnaMolecule {
	
	private int moleculeId;
	private Map<Integer, RnaRibonucleotide> chain;
	private Map<Integer, List<Integer>> pairs;
	private Map<Integer, List<Integer>> tertiaryPairs;
	private int maxReference = 0;
	
	public RnaMolecule(int moleculeId) {
		this.moleculeId = moleculeId;
		this.chain = new HashMap<>();
		this.pairs = new HashMap<>();
		this.tertiaryPairs = new HashMap<>();
	}

	public int getMoleculeId() {
		return this.moleculeId;
	}

	public int getMaxReference(){
		return maxReference;
	}
	
	public void addRibonucleotide(char c) throws RnaParsingException {
		int pos = getLength() + 1;
		try {
			RnaBase base = RnaBase.getBase(c);
			RnaRibonucleotide obj = new RnaRibonucleotide(this.moleculeId, pos, base);
			this.chain.put(pos, obj);
		}catch(Exception e) {
			throwException(pos);
		}
	}
	
	public void addPair(int first, int second) throws RnaParsingException {
		if( first < 1 )
			throwException(first);
		if( first == second || second < 1 )
			throwException(second);
		addPairToMap(first, second, false);
		addPairToMap(second, first, false);
	}
	public void addTertiaryPair(int first, int second) throws RnaParsingException {
		if( first < 1 )
			throwException(first);
		if( first == second || second < 1 )
			throwException(second);
			addPairToMap(first, second, true);
			addPairToMap(second, first, true);
	}

	public void addPairToMap(int first, int second, boolean tertiary) {
		List<Integer> list = tertiary ? tertiaryPairs.get(first) : pairs.get(first);
		if(list == null) {
			list = new ArrayList<>();
			if(tertiary)
				tertiaryPairs.put(first, list);
			else pairs.put(first, list);
		}
		list.add(second);
	}
	
	public int getLength() {
		return this.chain.size();
	}
	
	public String getSequence() {
		String out = "";
		final int len = getLength();
		for(int i = 1; i <= len; i++) {
			out += this.chain.get(i);
		}
		return out;
	}
	
	public Map<Integer, Integer> getPairMap(){
		Map<Integer, Integer> map = new HashMap<>();
		for(Entry<Integer, List<Integer>> pair : pairs.entrySet()) {
			for(Integer i : pair.getValue()) {
				Integer x = map.get(pair.getKey());
				if(x == null) {
					map.put(pair.getKey(), i);
				} else {
					if(map.get(i) == null)
						map.put(i, pair.getKey());
				}
			}
		}
		return map;
	}

	/**
	 * Questo metodo ritorna una mappa semplificata della struttura secondaria
	 * con tutti i collegamenti reciproci, indispensabile per formati che
	 * non permettono strutture complesse
	 * @return mappa semplificata della struttura secondaria
	 */
	public Map<Integer, Integer> getSimplifiedPairMap(){
		Map<Integer, Integer> map = new HashMap<>();
		for(Entry<Integer, List<Integer>> pair : pairs.entrySet()) {
			if(!map.containsKey(pair.getKey())){
				for(int i = 0; i < pair.getValue().size(); i++){
					if(!map.containsKey(pair.getValue().get(i))){
						map.put(pair.getKey(), pair.getValue().get(i));
						map.put(pair.getValue().get(i), pair.getKey());
						break;
					}
				}
			}
		}
		return map;
	}

	public boolean haveTertiaryData(){
		return !this.tertiaryPairs.isEmpty();
	}

	public List<Entry<Integer, Integer>> getTertiaryStructure(){
		List<Entry<Integer, Integer>> list = new ArrayList<>();
		if(!haveTertiaryData())
			return list;
		for(Entry<Integer,List<Integer>> data : tertiaryPairs.entrySet()){
			for(Integer i : data.getValue()){
				list.add(new AbstractMap.SimpleEntry<Integer, Integer>(data.getKey(), i));
			}
		}
		return new ArrayList<>(list.stream()
								.map(x -> x.getKey() < x.getValue() ? x : 
								new AbstractMap.SimpleEntry<Integer, Integer>(x.getValue(), x.getKey()))
								.distinct().toList()
							);
	}
	
	private void throwException(int pos) throws RnaParsingException {
		throw new RnaParsingException(this.moleculeId, pos);
	}
	
}
