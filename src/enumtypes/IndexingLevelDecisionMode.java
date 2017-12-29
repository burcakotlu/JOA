/**
 * 
 */
package enumtypes;

import common.Commons;

/**
 * @author Burcak Otlu
 * @date Apr 25, 2017
 * @project Joa 
 *
 */
public enum IndexingLevelDecisionMode {
	
	DEFAULT_SEVENTY_FIVE_PERCENTAGE(1),
	AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE(2),
	DURING_SEGMENT_TREE_CONSTRUCTION(3);
	
	private final int indexingLevelDecisionMode;

	/*
	 * This constructor is private.
	 * Legal to declare a non-private constructor, but not legal
	 * to use such a constructor outside the enum.
	 * Can never use "new" with any enum, even inside the enum
	 * class itself.
	 */
	private IndexingLevelDecisionMode(int indexingLevelDecisionMode) {

		this.indexingLevelDecisionMode = indexingLevelDecisionMode;
	}

	public int getIndexingLevelDecisionMode() {

		return indexingLevelDecisionMode;
	}

	public static IndexingLevelDecisionMode convertStringtoEnum(String indexingLevelDecisionMode) {

		if(Commons.DEFAULT_SEVENTY_FIVE_PERCENTAGE.equals(indexingLevelDecisionMode)){
			return DEFAULT_SEVENTY_FIVE_PERCENTAGE;
		}else if(Commons.AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE.equals(indexingLevelDecisionMode)){
			return AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE;
		}else if(Commons.DURING_SEGMENT_TREE_CONSTRUCTION.equals(indexingLevelDecisionMode)){
			return DURING_SEGMENT_TREE_CONSTRUCTION;
		}else
			return null;
	}

	public String convertEnumtoString() {

		if(this.equals(IndexingLevelDecisionMode.DEFAULT_SEVENTY_FIVE_PERCENTAGE))
			return Commons.DEFAULT_SEVENTY_FIVE_PERCENTAGE;
		else if(this.equals(IndexingLevelDecisionMode.AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE))
			return Commons.AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE;
		else if(this.equals(IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION))
			return Commons.DURING_SEGMENT_TREE_CONSTRUCTION;
		else
			return null;
	}
	


	public boolean isDEFAULT_SEVENTY_FIVE_PERCENTAGE() {
		return this == DEFAULT_SEVENTY_FIVE_PERCENTAGE;
	}
	
	
	
	public boolean isAFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE() {
		return this == AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE;
	}

	
	public boolean isDURING_SEGMENT_TREE_CONSTRUCTION() {
		return this == DURING_SEGMENT_TREE_CONSTRUCTION;
	}
	

}


