/**
 * 
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
 * @date Oct 18, 2017
 * @project JOA
 */
public class FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree extends RecursiveTask<TIntObjectMap<List<List<Interval>>>>{

	private static final long serialVersionUID = 2777707268514608985L;
	
	private int startFileIndex;
	private int endFileIndex;
	private String[] intervalSetsFileNames;
	
	
	public FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree(
			int startFileIndex,
			int endFileIndex, 
			String[] intervalSetsFileNames) {
		
		super();
		this.startFileIndex = startFileIndex;
		this.endFileIndex = endFileIndex;
		this.intervalSetsFileNames = intervalSetsFileNames;
	}

	protected TIntObjectMap<List<List<Interval>>> compute() {
		
		int middleFileIndex;
		
		TIntObjectMap<List<List<Interval>>> leftPart_chrNumber2IntervalListListMap = null;
		TIntObjectMap<List<List<Interval>>> rightPart_chrNumber2IntervalListListMap = null;
		
		if(startFileIndex==endFileIndex) {
			
			//Read file with startFileIndex
			//Fill chr2OverlappingIntervalsListListMap using startFileIndex
			//chr2OverlappingIntervalsListListMap has two intervals in each list.
			//Both are the same intervals.
			//Return it		
						
			TIntObjectMap<List<List<Interval>>> startFileIndex_chrNumber2IntervalListListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListListMap(intervalSetsFileNames[startFileIndex]);

			return startFileIndex_chrNumber2IntervalListListMap;
			
		}else if((endFileIndex-startFileIndex)==1) {
						
			//Read file with startFileIndex
			//Fill chr2OverlappingIntervalsListListMap using startFileIndex
			TIntObjectMap<List<List<Interval>>> startFileIndex_chrNumber2IntervalListListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListListMap(intervalSetsFileNames[startFileIndex]);


			//Construct parallel chrom based segment tree by reading the intervals in endFileIndex
			TIntObjectMap<SegmentTreeNode> endFileIndex_chrNumber2SegmentTreeNodeMap = JointOverlapAnalysis.constructNormalSegmentTreeParallelInChromBased(
					intervalSetsFileNames[endFileIndex]);
			
			//Find overlapping intervals Search parallel chrom based
			//Now we have search with info
			//But in case of two files no need for search with info
			SegmentTree.searchInParallelChromosomeBased(
					startFileIndex_chrNumber2IntervalListListMap,
					endFileIndex_chrNumber2SegmentTreeNodeMap,
					Boolean.FALSE);

		
			return startFileIndex_chrNumber2IntervalListListMap;
			
		} else {
			
			
			middleFileIndex = (startFileIndex+endFileIndex)/2;
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree left = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree(
					startFileIndex,
					middleFileIndex,
					intervalSetsFileNames);
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree right = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree(
					middleFileIndex+1,
					endFileIndex,
					intervalSetsFileNames);
						
			left.fork();
			rightPart_chrNumber2IntervalListListMap = right.compute();
			leftPart_chrNumber2IntervalListListMap  = left.join();
			
			return combine(
					leftPart_chrNumber2IntervalListListMap,
					rightPart_chrNumber2IntervalListListMap);
		}		

	}
	
	public static TIntObjectMap<List<List<Interval>>> combine(
			TIntObjectMap<List<List<Interval>>> leftPart_chrNumber2IntervalListListMap,
			TIntObjectMap<List<List<Interval>>> rightPart_chrNumber2IntervalListListMap){
		
		//Step1 is already at hand
		//Use the last interval in each interval list in leftPart as input query interval
		//We have leftPart_chrNumber2IntervalListListMap ready
		
		//Step2
		//Construct segment tree using the last interval in each interval list in right part
		//Make the former intervals as String info
		TIntObjectMap<SegmentTreeNode> endFileIndex_chrNumber2SegmentTreeNodeMap = constructSegmentTreeParallelInChromBased(
				rightPart_chrNumber2IntervalListListMap);
				
		//Step3
		//Find the overlaps update overlappingIntervalsListList
		//This time search with info is set true
		SegmentTree.searchInParallelChromosomeBased(
				leftPart_chrNumber2IntervalListListMap,
				endFileIndex_chrNumber2SegmentTreeNodeMap,
				Boolean.TRUE);
		
		//Step4
		return leftPart_chrNumber2IntervalListListMap;
		
	}

	public static TIntObjectMap<SegmentTreeNode> constructSegmentTreeParallelInChromBased(
			TIntObjectMap<List<List<Interval>>> chrNumber2IntervalListListMap){
	
		//Nov 2,2017 to avoid rehash, initialize with number of chromosomes which is 24
		TIntObjectMap<List<Interval>> chrNumber2IntervalsMap = new TIntObjectHashMap<List<Interval>>(25);
		TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap  = new TIntObjectHashMap<int[]>(25);
		
		//Important
		//Info of interval is set 
		/********************************************************************/
		/*******************Step1 Fill End Points starts*********************/
		/********************************************************************/		
		SegmentTree.fillParallelChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalsMap,chrNumber2IntervalListListMap);		
		/********************************************************************/
		/*******************Step1 Fill End Points ends***********************/
		/********************************************************************/
							
		//Step2 is already handled in Step1
		//Step2 was sorting the end points.		
		
		/********************************************************************/
		/*************Step3 Construct Segment Tree starts********************/
		/********************************************************************/
		TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap  = SegmentTree.constructSegmentTreeStep3InParallelChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalsMap);
		/********************************************************************/
		/*************Step3 Construct Segment Tree ends**********************/
		/********************************************************************/

		return chrNumber2SegmentTreeNodeMap;	

	}


	


}
