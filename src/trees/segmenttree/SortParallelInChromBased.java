/**
 * 
 */
package trees.segmenttree;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

/**
 * @author Burcak Otlu 
 * @date Dec 17, 2017
 * @project JOA
 */
public class SortParallelInChromBased  extends RecursiveAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2138943680567349767L;
	
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints;
	private TIntObjectMap<int[]>  chrNumber2SortedEndPointsArrayMap;
	
	

	@Override
	protected void compute() {
		if (lowerChrNumber == upperChrNumber){
						
			TIntArrayList unsortedList = chrNumber2UnSortedEndPoints.get(lowerChrNumber);
			
			if (unsortedList!=null) {
				
				int[] sortedArray = new int[unsortedList.size()];
				unsortedList.toArray(sortedArray);
				Arrays.parallelSort(sortedArray);
				 
				chrNumber2SortedEndPointsArrayMap.put(lowerChrNumber, sortedArray);
				
				//For less memory usage
				unsortedList = null;
			}
			
			
		}else {
			
			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			SortParallelInChromBased leftChromosomes = new SortParallelInChromBased(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2UnSortedEndPoints,
					chrNumber2SortedEndPointsArrayMap);
					
			
			SortParallelInChromBased rightChromosomes = new SortParallelInChromBased(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2UnSortedEndPoints,
					chrNumber2SortedEndPointsArrayMap);			
			
			invokeAll(leftChromosomes, rightChromosomes);
								
			
		}
			
	}



	public SortParallelInChromBased(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints,
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2UnSortedEndPoints = chrNumber2UnSortedEndPoints;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
	}
	

}
