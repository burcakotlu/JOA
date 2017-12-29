/**
 * 
 */
package enumtypes;

import common.Commons;

/**
 * @author burcak 
 * @date Dec 11, 2017
 * @project JOA
 */
public enum SearchMethod {
	
	USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED(1),
	NOT_SET(2);
	
	
	private final int searchMethod;

	/*
	 * This constructor is private.
	 * Legal to declare a non-private constructor, but not legal
	 * to use such a constructor outside the enum.
	 * Can never use "new" with any enum, even inside the enum
	 * class itself.
	 */
	private SearchMethod(int searchMethod) {

		this.searchMethod = searchMethod;
	}

	public int getSearchMethod() {

		return searchMethod;
	}

	public static SearchMethod convertStringtoEnum(String searchMethod) {

		if(Commons.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED.equals(searchMethod)){
			return USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED;
		}else if(Commons.NOT_SET.equals(searchMethod)){
			return NOT_SET;
		}else
			return null;
	}

	public String convertEnumtoString() {

		if(this.equals(SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED))
			return Commons.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED;
		else if(this.equals(SearchMethod.NOT_SET))
			return Commons.NOT_SET;
		else
			return null;
	}
	


	public boolean isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED() {
		return this == USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED;
	}
	
	
	
	public boolean isNOT_SET() {
		return this == NOT_SET;
	}

	
	

}
