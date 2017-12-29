/**
 * 
 */
package enumtypes;

import common.Commons;

/**
 * @author Burcak Otlu
 * @date May 4, 2017
 * @project Joa 
 *
 */
public enum ChildType {
	
	LEFT_CHILD(1),
	RIGHT_CHILD(2);
	
	private final int childType;

	/*
	 * This constructor is private.
	 * Legal to declare a non-private constructor, but not legal
	 * to use such a constructor outside the enum.
	 * Can never use "new" with any enum, even inside the enum
	 * class itself.
	 */
	private ChildType(int childType) {

		this.childType = childType;
	}

	public int getChildType() {

		return childType;
	}

	public static ChildType convertStringtoEnum(String childType) {

		if(Commons.LEFT_CHILD.equals(childType)){
			return LEFT_CHILD;
		}else if(Commons.RIGHT_CHILD.equals(childType)){
			return RIGHT_CHILD;
		}else
			return null;
	}

	public String convertEnumtoString() {

		if(this.equals(ChildType.LEFT_CHILD))
			return Commons.LEFT_CHILD;
		else if(this.equals(ChildType.RIGHT_CHILD))
			return Commons.RIGHT_CHILD;
		else
			return null;
	}
	


	public boolean isLeftChild() {
		return this == LEFT_CHILD;
	}
	
		
	public boolean isRightChild() {
		return this == RIGHT_CHILD;
	}

}
