/**
 * 
 */
package trees.segmenttree;

/**
 * @author Burcak Otlu
 * @date Jan 31, 2017
 * @project Jef 
 *
 */
public class SearchStatistics {

	int chrNumber;
	
	int numberofHits;
	int numberofMisses;
	
	
	public int getChrNumber() {
		return chrNumber;
	}
	public void setChrNumber(int chrNumber) {
		this.chrNumber = chrNumber;
	}
	public int getNumberofHits() {
		return numberofHits;
	}
	public void setNumberofHits(int numberofHits) {
		this.numberofHits = numberofHits;
	}
	public int getNumberofMisses() {
		return numberofMisses;
	}
	public void setNumberofMisses(int numberofMisses) {
		this.numberofMisses = numberofMisses;
	}
	
	
	public SearchStatistics(int chrNumber, int numberofHits, int numberofMisses) {
		super();
		this.chrNumber = chrNumber;
		this.numberofHits = numberofHits;
		this.numberofMisses = numberofMisses;
	}
	
	
	
}
