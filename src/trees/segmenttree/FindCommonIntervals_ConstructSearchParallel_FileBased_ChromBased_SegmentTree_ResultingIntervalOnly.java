/**
 *  This class is for SegmentTree outputs 1 intervals (only one resulting interval).
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import findcommonoverlaps.JointOverlapAnalysis;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Dec 15, 2017
 * @project JOA
 */
public class FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly extends RecursiveTask<TIntObjectMap<List<Interval>>>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2784530103778823880L;
	
	
	private int startFileIndex;
	private int endFileIndex;
	private String[] intervalSetsFileNames;
	
	//For analysis remove later
	//private BufferedWriter bufferedWriter;
	
	public FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			int startFileIndex, 
			int endFileIndex, 
			String[] intervalSetsFileNames) {
		
		super();
		this.startFileIndex = startFileIndex;
		this.endFileIndex = endFileIndex;
		this.intervalSetsFileNames = intervalSetsFileNames;

	}




	@Override
	protected TIntObjectMap<List<Interval>> compute() {
		
		int middleFileIndex;		
		TIntObjectMap<List<Interval>> leftPart_chrNumber2IntervalListMap = null;
		TIntObjectMap<List<Interval>> rightPart_chrNumber2IntervalListMap = null;		

//		//for debugging
//		Long timeStart;
//		Long readTime;
//		Long constructTime;
//		Long searchTime;
		
		TIntObjectMap<List<Interval>> startFileIndex_chrNumber2IntervalListMap = null;
		
		
		if(startFileIndex==endFileIndex) {
						
			startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);
						
			return startFileIndex_chrNumber2IntervalListMap;
			
		}else if((endFileIndex-startFileIndex)==1) {
			
			/**************************************************************************************************/																					
			//Read file with startFileIndex
			//Fill chr2OverlappingIntervalsListListMap using startFileIndex
			startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);			
			/**************************************************************************************************/				
			
			/**************************************************************************************************/								
			//Construct parallel chrom based segment tree by reading the intervals in endFileIndex
			TIntObjectMap<SegmentTreeNode> endFileIndex_chrNumber2SegmentTreeNodeMap = JointOverlapAnalysis.constructNormalSegmentTreeParallelInChromBased(
					intervalSetsFileNames[endFileIndex]);			
			/**************************************************************************************************/				
			
			/**************************************************************************************************/							
			//Find overlapping intervals Search parallel chrom based
			//Now we have search with info
			//But in case of two files no need for search with info
			SegmentTree.searchInParallelChromosomeBased_ResultingIntervalOnly(
					startFileIndex_chrNumber2IntervalListMap,
					endFileIndex_chrNumber2SegmentTreeNodeMap);			
			/**************************************************************************************************/				

			return startFileIndex_chrNumber2IntervalListMap;
			
		} else {
			
			
			middleFileIndex = (startFileIndex+endFileIndex)/2;
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly left = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
					startFileIndex,
					middleFileIndex,
					intervalSetsFileNames);
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly right = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
					middleFileIndex+1,
					endFileIndex,
					intervalSetsFileNames);
						
			left.fork();
			rightPart_chrNumber2IntervalListMap = right.compute();
			leftPart_chrNumber2IntervalListMap  = left.join();
			
			return combine(leftPart_chrNumber2IntervalListMap,rightPart_chrNumber2IntervalListMap);
			
			}
		}
		
		public static TIntObjectMap<List<Interval>> combine(TIntObjectMap<List<Interval>> leftPart_chrNumber2IntervalListMap, TIntObjectMap<List<Interval>> rightPart_chrNumber2IntervalListMap){
			
			//Step1 is already at hand
			//Use the last interval in each interval list in leftPart as input query interval
			//We have leftPart_chrNumber2IntervalListListMap ready

			//Step2
			//Construct segment tree using each interval in the list in right part
			TIntObjectMap<SegmentTreeNode> endFileIndex_chrNumber2SegmentTreeNodeMap = constructSegmentTreeParallelInChromBased(rightPart_chrNumber2IntervalListMap);
					
			//Step3
			//Find the overlaps update overlappingIntervalsListList
			//This time search with info is set true
			SegmentTree.searchInParallelChromosomeBased_ResultingIntervalOnly(
					leftPart_chrNumber2IntervalListMap,
					endFileIndex_chrNumber2SegmentTreeNodeMap);
			
			//Step4
			return leftPart_chrNumber2IntervalListMap;
			
		}

		public static TIntObjectMap<SegmentTreeNode> constructSegmentTreeParallelInChromBased(
				TIntObjectMap<List<Interval>> chrNumber2IntervalListMap){
		
			//Nov 2,2017 to avoid rehash, initialize with number of chromosomes which is 25
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap  = new TIntObjectHashMap<int[]>(25);
			
			/********************************************************************/
			/*******************Step1 Fill End Points starts*********************/
			/********************************************************************/		
			SegmentTree.fillParallelChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalListMap);		
			/********************************************************************/
			/*******************Step1 Fill End Points ends***********************/
			/********************************************************************/
								
			//Step2 is already handled in Step1
			//Step2 was sorting the end points.		
			
			/********************************************************************/
			/*************Step3 Construct Segment Tree starts********************/
			/********************************************************************/
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap  = SegmentTree.constructSegmentTreeStep3InParallelChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalListMap);
			/********************************************************************/
			/*************Step3 Construct Segment Tree ends**********************/
			/********************************************************************/

			return chrNumber2SegmentTreeNodeMap;	

		}

	
	
	
}
