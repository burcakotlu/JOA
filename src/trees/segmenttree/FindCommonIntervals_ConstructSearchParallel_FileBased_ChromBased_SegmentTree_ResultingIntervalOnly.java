/**
 * 
 */
package trees.segmenttree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import common.Commons;
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
	private BufferedWriter bufferedWriter;
	
	public FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			int startFileIndex, 
			int endFileIndex, 
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter) {
		
		super();
		this.startFileIndex = startFileIndex;
		this.endFileIndex = endFileIndex;
		this.intervalSetsFileNames = intervalSetsFileNames;
		this.bufferedWriter = bufferedWriter;
	}




	@Override
	protected TIntObjectMap<List<Interval>> compute() {
		
		int middleFileIndex;		
		TIntObjectMap<List<Interval>> leftPart_chrNumber2IntervalListMap = null;
		TIntObjectMap<List<Interval>> rightPart_chrNumber2IntervalListMap = null;		
		Long timeStart,elapsedTime;
		
		TIntObjectMap<List<Interval>> startFileIndex_chrNumber2IntervalListMap = null;
		
		
		if(startFileIndex==endFileIndex) {
			
			startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);
			return startFileIndex_chrNumber2IntervalListMap;
			
		}else if((endFileIndex-startFileIndex)==1) {
			
			timeStart = System.nanoTime();														
			//Read file with startFileIndex
			//Fill chr2OverlappingIntervalsListListMap using startFileIndex
			startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);
			elapsedTime = System.nanoTime()-timeStart;			
			//write to bufferedWriter elapsedTime as read time
			try {
				bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
			} catch (IOException e) {
				e.printStackTrace();
			}

			timeStart = System.nanoTime();														
			//Construct parallel chrom based segment tree by reading the intervals in endFileIndex
			TIntObjectMap<SegmentTreeNode> endFileIndex_chrNumber2SegmentTreeNodeMap = JointOverlapAnalysis.constructNormalSegmentTreeParallelInChromBased(
					intervalSetsFileNames[endFileIndex]);
			elapsedTime = System.nanoTime()-timeStart;			
			//write to bufferedWriter elapsedTime as construct time
			try {
				bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			timeStart = System.nanoTime();														
			//Find overlapping intervals Search parallel chrom based
			//Now we have search with info
			//But in case of two files no need for search with info
			SegmentTree.searchInParallelChromosomeBased_ResultingIntervalOnly(
					startFileIndex_chrNumber2IntervalListMap,
					endFileIndex_chrNumber2SegmentTreeNodeMap);
			elapsedTime = System.nanoTime()-timeStart;		
			
			
			//For now original. Later on remove.
			try {						
				//former it was
				bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
			} catch (IOException e) {
				e.printStackTrace();
			}

			
			return startFileIndex_chrNumber2IntervalListMap;
			
		} else {
			
			
			middleFileIndex = (startFileIndex+endFileIndex)/2;
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly left = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
					startFileIndex,
					middleFileIndex,
					intervalSetsFileNames,
					bufferedWriter);
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly right = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
					middleFileIndex+1,
					endFileIndex,
					intervalSetsFileNames,
					bufferedWriter);
						
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
			//Construct segment tree using each interval ın the list in right part
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
