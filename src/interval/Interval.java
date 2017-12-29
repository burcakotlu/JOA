/**
 * 
 */
package interval;

/**
 * @author Burcak Otlu
 * @date Jan 16, 2017
 * @project Jef 
 *
 */
public class Interval {
	
	int lowerEndPoint;
	int higherEndPoint;
	int sourceIntervalSetNumber;
	String info;

	
	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public int getSourceIntervalSetNumber() {
		return sourceIntervalSetNumber;
	}


	public void setSourceIntervalSetNumber(int sourceIntervalSetNumber) {
		this.sourceIntervalSetNumber = sourceIntervalSetNumber;
	}


	public int getLowerEndPoint() {
		return lowerEndPoint;
	}


	public void setLowerEndPoint(int lowerEndPoint) {
		this.lowerEndPoint = lowerEndPoint;
	}


	public int getHigherEndPoint() {
		return higherEndPoint;
	}


	public void setHigherEndPoint(int higherEndPoint) {
		this.higherEndPoint = higherEndPoint;
	}


	//Modified October 20, 2017
	public Interval(int lowerEndPoint, int higherEndPoint) {
		super();
		this.lowerEndPoint = lowerEndPoint;
		this.higherEndPoint = higherEndPoint;
		this.info = null;
	}
	
	
	public Interval(int lowerEndPoint, int higherEndPoint, String info) {
		super();
		this.lowerEndPoint = lowerEndPoint;
		this.higherEndPoint = higherEndPoint;
		this.info = info;
	}
	
	public Interval(int lowerEndPoint, int higherEndPoint, int sourceIntervalSetNumber) {
		super();
		this.lowerEndPoint = lowerEndPoint;
		this.higherEndPoint = higherEndPoint;
		this.sourceIntervalSetNumber = sourceIntervalSetNumber;
	}
	
	
	public Interval() {
		super();
	}
	
	
	
	

}
