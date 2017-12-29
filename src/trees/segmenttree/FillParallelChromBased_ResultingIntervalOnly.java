/**
 * 
 */
package trees.segmenttree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Dec 14, 2017
 * @project JOA
 */
public class FillParallelChromBased_ResultingIntervalOnly extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3699944525340852659L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalListMap;
	
	
	public static void fill(TIntArrayList  unsortedEndPoints,List<Interval> intervalList) {
		
		Interval interval = null;
		
		for (Iterator<Interval> itr=intervalList.iterator();itr.hasNext();) {
			
			interval = itr.next();
			
			unsortedEndPoints.add(interval.getLowerEndPoint());
			unsortedEndPoints.add(interval.getHigherEndPoint());
			
		}//End of for
		
	}

	
	

	public FillParallelChromBased_ResultingIntervalOnly(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalListMap = chrNumber2IntervalListMap;
	}




	@Override
	protected void compute() {
		
		
		if (upperChrNumber==lowerChrNumber){
						
			List<Interval> intervalList = chrNumber2IntervalListMap.get(lowerChrNumber);
			int[] sortedArray = null;
			
			if(intervalList!=null) {
				
				TIntArrayList unsortedEndPoints = new TIntArrayList();
				
				fill(unsortedEndPoints,intervalList);
				
				sortedArray = new int[unsortedEndPoints.size()];
				unsortedEndPoints.toArray(sortedArray);
				
				Arrays.parallelSort(sortedArray);								
										
				chrNumber2SortedEndPointsArrayMap.put(lowerChrNumber, sortedArray);
				
			}//End of if
						
			
		}
		else{
			
			int middleChrNumber = (lowerChrNumber+upperChrNumber)/2;
			
			FillParallelChromBased_ResultingIntervalOnly leftChromosomes = new FillParallelChromBased_ResultingIntervalOnly(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalListMap);
					
			
			FillParallelChromBased_ResultingIntervalOnly rightChromosomes = new FillParallelChromBased_ResultingIntervalOnly(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalListMap);
			
			
			invokeAll(leftChromosomes, rightChromosomes);
								
		}
		
		
	}
	
	

}
