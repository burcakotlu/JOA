/**
 * 
 */
package trees.segmenttree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Oct 8, 2017
 * @project JOA
 */
public class FillParallelChromBased extends RecursiveAction{
	
	private static final long serialVersionUID = 6313014114034511943L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalListMap;
	private TIntObjectMap<List<List<Interval>>> chrNumber2IntervalListListMap;
	
	
	public static void fill(TIntArrayList  unsortedEndPoints,List<Interval> intervalList,List<List<Interval>> intervalListList) {
		
		for (Iterator<List<Interval>> itr=intervalListList.iterator();itr.hasNext();) {
			
			List<Interval> anIntervalList = itr.next();
			
					
			//Get the last interval
			Interval lastInterval = anIntervalList.get(anIntervalList.size()-1);
			String info = null;
						
			
			//Set the info of the last interval
			if (anIntervalList.size()-1>0) {
				
				info = "";
				
				for (int i=0; i<anIntervalList.size()-1;i++) {
						//Make end position exclusive plus 1
						info += anIntervalList.get(i).getLowerEndPoint() + "\t" + (anIntervalList.get(i).getHigherEndPoint()+1) +"\t";
				}
				
				//remove the last tab character
				if (info.endsWith("\t")) {
					info = info.substring(0, info.length()-1);
				}
					
								
				///order is important
				if (lastInterval.getInfo()==null){
					lastInterval.setInfo(info);
				}else {
					lastInterval.setInfo(info + "\t" + lastInterval.getInfo());
				}
												
			}
							
			
			unsortedEndPoints.add(lastInterval.getLowerEndPoint());
			unsortedEndPoints.add(lastInterval.getHigherEndPoint());
			
			//Add the last interval with info field set 
			intervalList.add(lastInterval);
						
		}
		
	}
	
	
	public FillParallelChromBased(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<int[]>  chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap,
			TIntObjectMap<List<List<Interval>>> chrNumber2IntervalListListMap) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalListMap = chrNumber2IntervalListMap;
		this.chrNumber2IntervalListListMap = chrNumber2IntervalListListMap;
		
	}
	
	@Override
	protected void compute() {
		
		
		if (lowerChrNumber == upperChrNumber){
						
			List<List<Interval>> intervalListList = chrNumber2IntervalListListMap.get(lowerChrNumber);
			int[] sortedArray = null;
			List<Interval> intervalList = null;
			
			if(intervalListList!=null) {
				
				TIntArrayList unsortedEndPoints = new TIntArrayList();
				intervalList = new ArrayList<Interval>(intervalListList.size());
				
				//Last interval with info field set is added			
				fill(unsortedEndPoints,intervalList,intervalListList);
				
				sortedArray = new int[unsortedEndPoints.size()];
				unsortedEndPoints.toArray(sortedArray);
				
				Arrays.parallelSort(sortedArray);								
										
				chrNumber2SortedEndPointsArrayMap.put(lowerChrNumber, sortedArray);
				chrNumber2IntervalListMap.put(lowerChrNumber, intervalList);
				
			}//End of if: This if is added Nov 1, 2017
						
			
		}
		else{
			
			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			FillParallelChromBased leftChromosomes = new FillParallelChromBased(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalListMap,
					chrNumber2IntervalListListMap);
					
			
			FillParallelChromBased rightChromosomes = new FillParallelChromBased(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalListMap,
					chrNumber2IntervalListListMap);
			
			
//			leftChromosomes.fork();
//			rightChromosomes.fork();
//			leftChromosomes.join();
//			rightChromosomes.join();
			
			invokeAll(leftChromosomes, rightChromosomes);
								
		}
		
	}




	
	
	
	
	
	
	

}
