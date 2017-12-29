/**
 * 
 */
package auxiliary;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;

/**
 * @author Burcak Otlu
 * @date Feb 6, 2017
 * @project Joa 
 *
 */
public class Sorting {
	
	public static TIntObjectMap<int[]> sort(TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints){
		
		TIntList unsortedList = null;
		int[] sortedArray = null;
		
		int chrNumber;
		
		TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap = new TIntObjectHashMap<int[]>(25);		
						
		for(TIntObjectIterator<TIntArrayList> itr=chrNumber2UnSortedEndPoints.iterator();itr.hasNext();){
			 
			itr.advance();
			 
			 chrNumber = itr.key();
			 unsortedList = (TIntList) itr.value();
			 
			 sortedArray = new int[unsortedList.size()];
			 unsortedList.toArray(sortedArray);
			 Arrays.parallelSort(sortedArray);
			 
			chrNumber2SortedEndPointsArrayMap.put(chrNumber, sortedArray);
			
			//for less memory usage
			unsortedList = null;
			
		}//End of for
		
		return chrNumber2SortedEndPointsArrayMap;
		
	}

}
