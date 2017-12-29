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
public enum OutputType {

	
	ONLY_RESULTING_INTERVAL(1),
	ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL(2);	
	
	private final int outputType;

	/*
	 * This constructor is private.
	 * Legal to declare a non-private constructor, but not legal
	 * to use such a constructor outside the enum.
	 * Can never use "new" with any enum, even inside the enum
	 * class itself.
	 */
	private OutputType(int outputType) {
		this.outputType = outputType;
	}

	public int getOutputType() {
		return outputType;
	}

	public static OutputType convertStringtoEnum(String outputType) {

		if(Commons.ONLY_RESULTING_INTERVAL.equals(outputType)){
			return ONLY_RESULTING_INTERVAL;
		}else if(Commons.ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL.equals(outputType)){
			return ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL;
		}else
			return null;
	}

	public String convertEnumtoString() {

		if(this.equals(OutputType.ONLY_RESULTING_INTERVAL))
			return Commons.ONLY_RESULTING_INTERVAL;
		else if(this.equals(OutputType.ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL))
			return Commons.ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL;
		else
			return null;
	}
	


	public boolean isONLY_RESULTING_INTERVAL() {
		return this == ONLY_RESULTING_INTERVAL;
	}
	
	
	
	public boolean isALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL() {
		return this == ALL_OVERLAPPING_INTERVALS_AND_RESULTING_INTERVAL;
	}

}
