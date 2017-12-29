/**
 * 
 */
package trees.segmenttree;

/**
 * @author Burcak Otlu
 * @date Jan 19, 2017
 * @project Jef 
 *
 */
public class SegmentTreeLevelFeatures {
	
	int levelNumber;
	int numberofIntervalsAtThisLevel;
	int numberofIntervalsUpToThisLevel;
	int numberofSegmentsAtThisLevel;
	
	
	public int getLevelNumber() {
		return levelNumber;
	}
	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}
	
	
	
	public int getNumberofIntervalsAtThisLevel() {
		return numberofIntervalsAtThisLevel;
	}
	public void setNumberofIntervalsAtThisLevel(int numberofIntervalsAtThisLevel) {
		this.numberofIntervalsAtThisLevel = numberofIntervalsAtThisLevel;
	}
	public int getNumberofIntervalsUpToThisLevel() {
		return numberofIntervalsUpToThisLevel;
	}
	public void setNumberofIntervalsUpToThisLevel(int numberofIntervalsUpToThisLevel) {
		this.numberofIntervalsUpToThisLevel = numberofIntervalsUpToThisLevel;
	}
	public int getNumberofSegmentsAtThisLevel() {
		return numberofSegmentsAtThisLevel;
	}
	public void setNumberofSegmentsAtThisLevel(int numberofSegmentsAtThisLevel) {
		this.numberofSegmentsAtThisLevel = numberofSegmentsAtThisLevel;
	}
	public SegmentTreeLevelFeatures() {
		super();
	}
	
	
	public SegmentTreeLevelFeatures(int levelNumber, int numberofIntervalsAtThisLevel, int numberofIntervalsUpToThisLevel, int numberofSegmentsAtThisLevel) {
		super();
		this.levelNumber = levelNumber;
		this.numberofIntervalsAtThisLevel = numberofIntervalsAtThisLevel;
		this.numberofIntervalsUpToThisLevel = numberofIntervalsUpToThisLevel;
		this.numberofSegmentsAtThisLevel = numberofSegmentsAtThisLevel;
	}
	
	
}
