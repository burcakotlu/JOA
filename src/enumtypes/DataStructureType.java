/**
 * 
 */
package enumtypes;

import common.Commons;

/**
 * @author Burcak Otlu 
 * @date Dec 13, 2017
 * @project JOA
 */
public enum DataStructureType {
	
	INDEXED_SEGMENT_TREE_FOREST(1),
	INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE(2),
	SEGMENT_TREE(3);
	
	private final int dataStructureType;

	/*
	 * This constructor is private.
	 * Legal to declare a non-private constructor, but not legal
	 * to use such a constructor outside the enum.
	 * Can never use "new" with any enum, even inside the enum
	 * class itself.
	 */
	private DataStructureType(int dataStructureType) {
		this.dataStructureType = dataStructureType;
	}

	public int getdDataStructureType() {
		return dataStructureType;
	}

	public static DataStructureType convertStringtoEnum(String dataStructureType) {

		if(Commons.INDEXED_SEGMENT_TREE_FOREST.equals(dataStructureType)){
			return INDEXED_SEGMENT_TREE_FOREST;
		}else if(Commons.INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE.equals(dataStructureType)){
			return INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE;
		}else if(Commons.SEGMENT_TREE.equals(dataStructureType)){
			return SEGMENT_TREE;
		}else
			return null;
	}

	public String convertEnumtoString() {

		if(this.equals(DataStructureType.INDEXED_SEGMENT_TREE_FOREST))
			return Commons.INDEXED_SEGMENT_TREE_FOREST;
		else if(this.equals(DataStructureType.INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE))
			return Commons.INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE;
		else if(this.equals(DataStructureType.SEGMENT_TREE))
			return Commons.SEGMENT_TREE;
		else
			return null;
	}
	


	public boolean isINDEXED_SEGMENT_TREE_FOREST() {
		return this == INDEXED_SEGMENT_TREE_FOREST;
	}
	
	
	
	public boolean isINDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE() {
		return this == INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE;
	}

	public boolean isSEGMENT_TREE() {
		return this == SEGMENT_TREE;
	}
}
