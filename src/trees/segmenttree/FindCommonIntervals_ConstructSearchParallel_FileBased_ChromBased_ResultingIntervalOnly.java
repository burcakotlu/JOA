/**
 * 
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import enumtypes.IndexingLevelDecisionMode;
import enumtypes.SearchMethod;
import findcommonoverlaps.JointOverlapAnalysis;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;

/**
 * @author Burcak Otlu
 * @date Dec 14, 2017
 * @project JOA
 */
public class FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly extends RecursiveTask<TIntObjectMap<List<Interval>>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6154799825272728843L;
	
	
	private int presetValue;
	private IndexingLevelDecisionMode mode;	
	private int startFileIndex;
	private int endFileIndex;
	private String[] intervalSetsFileNames;
	private int numberofPercent;
	private SearchMethod searchMethod;
	
	//added for runtime and searchtime analysis later remove
	//private BufferedWriter bufferedWriter;
		
	public FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly(
			int presetValue,
			IndexingLevelDecisionMode mode, 
			int startFileIndex, 
			int endFileIndex, 
			String[] intervalSetsFileNames,
			int numberofPercent, 
			SearchMethod searchMethod) {
		
		super();
		this.presetValue = presetValue;
		this.mode = mode;
		this.startFileIndex = startFileIndex;
		this.endFileIndex = endFileIndex;
		this.intervalSetsFileNames = intervalSetsFileNames;
		this.numberofPercent = numberofPercent;
		this.searchMethod = searchMethod;
	}


	@Override
	protected TIntObjectMap<List<Interval>> compute() {
		
		int middleFileIndex;
		
		TIntObjectMap<List<Interval>> leftPart_chrNumber2OverlappingIntervalsListMap = null;
		TIntObjectMap<List<Interval>> rightPart_chrNumber2OverlappingIntervalsListMap = null;
		
		TIntObjectMap<List<Interval>> startFileIndex_chrNumber2IntervalListMap  = null;
		
		//Long timeStart,elapsedTime;
						
		if(startFileIndex==endFileIndex) {
						
			//Read file with startFileIndex
			//Fill chr2OverlappingIntervalsListMap using startFileIndex
			//Return it				
			startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);
			
			return startFileIndex_chrNumber2IntervalListMap;
			
		}else if((endFileIndex-startFileIndex)==1) {
										
				/**************************************************************************************************/
				//timeStart = System.nanoTime();	
			
				//Read file with startFileIndex
				//Fill chr2OverlappingIntervalsListMap using startFileIndex
				startFileIndex_chrNumber2IntervalListMap = SegmentTree.fillChrNumber2OverlappingIntervalsListMap(intervalSetsFileNames[startFileIndex]);
				
				//elapsedTime = System.nanoTime()-timeStart;			
				//write to bufferedWriter elapsedTime as read time
				//try {
				//	bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
				//} catch (IOException e) {
				//	e.printStackTrace();
				//}
				/**************************************************************************************************/
				
				/**************************************************************************************************/
				//timeStart = System.nanoTime();
				
				//Construct parallel chrom based indexed segment tree forest by reading the intervals in endFileIndex
				TIntObjectMap<TIntObjectMap<SegmentTreeNode>> endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap = JointOverlapAnalysis.constructIndexedSegmentTreeForestParallelInChromBased(
						mode,
						presetValue,
						intervalSetsFileNames[endFileIndex],
						numberofPercent);				
				//elapsedTime = System.nanoTime()-timeStart;			
//				//write to bufferedWriter elapsedTime as construction time
//				try {
//					bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				/**************************************************************************************************/
				
				
				/**************************************************************************************************/
				//timeStart = System.nanoTime();			
				//Find overlapping intervals Search parallel chrom based
				SegmentTree.searchInParallelChromosomeBased_ResultingIntervalOnly(
						presetValue,
						startFileIndex_chrNumber2IntervalListMap,
						endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap,
						searchMethod);
				//elapsedTime = System.nanoTime()-timeStart;		
												
//				//For now original code. But remove later.
//				try {							
//					//former it was
//					bufferedWriter.write(elapsedTime/Commons.ONE_MILLION_FLOAT + "\t");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				/**************************************************************************************************/
							
						
				//for less memory usage
				endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap = null;
				
				return startFileIndex_chrNumber2IntervalListMap;
							
			
		} else {
			
			middleFileIndex = (startFileIndex+endFileIndex)/2;
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly left = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly(
					presetValue,
					mode,
					startFileIndex,
					middleFileIndex,
					intervalSetsFileNames,
					numberofPercent,
					searchMethod);
			
			FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly right = new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly(
					presetValue,
					mode,
					middleFileIndex+1,
					endFileIndex,
					intervalSetsFileNames,
					numberofPercent,
					searchMethod);
						
			left.fork();
			rightPart_chrNumber2OverlappingIntervalsListMap = right.compute();
			leftPart_chrNumber2OverlappingIntervalsListMap  = left.join();
			
			return combine(
					mode,
					presetValue,
					leftPart_chrNumber2OverlappingIntervalsListMap,
					rightPart_chrNumber2OverlappingIntervalsListMap,
					numberofPercent,
					searchMethod);
		}	
		
		
	}
	

	public static TIntObjectMap<List<Interval>>  combine(
			IndexingLevelDecisionMode mode,
			int presetValue,
			TIntObjectMap<List<Interval>> leftPart_chrNumber2OverlappingIntervalsListMap,
			TIntObjectMap<List<Interval>> rightPart_chrNumber2OverlappingIntervalsListMap,
			int numberofPercent,
			SearchMethod searchMethod){
		
		//Step1 is already at hand
		//Use the interval in interval list of leftPart as input query interval
		//We have leftPart_chrNumber2IntervalListMap ready
		
		//Step2
		//Construct indexed segment tree forest using the interval in interval list of right part
		TIntObjectMap<TIntObjectMap<SegmentTreeNode>> endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap = constructIndexedSegmentTreeForestParallelInChromBased(
				mode,
				presetValue,
				rightPart_chrNumber2OverlappingIntervalsListMap,
				numberofPercent);
		
		
		//Step3
		//Find the overlaps update overlappingIntervalsListList
		//This time search with info is set true
		SegmentTree.searchInParallelChromosomeBased_ResultingIntervalOnly(
				presetValue,
				leftPart_chrNumber2OverlappingIntervalsListMap,
				endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap,
				searchMethod);
		
		
		//for less memory usage
		rightPart_chrNumber2OverlappingIntervalsListMap = null;
		endFileIndex_chrNumber2HashIndex2SegmentTreeNodeMapMap = null;

		
		//Step4
		return leftPart_chrNumber2OverlappingIntervalsListMap;
		
	}
	
	public static TIntObjectMap<TIntObjectMap<SegmentTreeNode>> constructIndexedSegmentTreeForestParallelInChromBased(
			IndexingLevelDecisionMode mode,
			int presetValue,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap,
			int numberofPercent){
				
			
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
			
			/********************************************************************/
			/*************Step3 Construct Segment Tree starts********************/
			/********************************************************************/
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap = null;
			TIntIntMap chrNumber2IndexingLevelMap = null;		
			
			//We calculated indexing level at Step3
			//Modified Version
			//Normally we assume that root has level 1 and level number gets higher as we go down at the tree.
			//However here since we construct the tree in a bottom up manner, leaf has level 1 and level number gets higher as we go up in the tree.
			//Therefore we must set indexing level number as highest level number (root's level number) - found indexing level number
			chrNumber2IndexingLevelMap = new TIntIntHashMap(25);
			chrNumber2SegmentTreeNodeMap = SegmentTree.constructSegmentTreeStep3InParallelInChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalListMap,chrNumber2IndexingLevelMap,numberofPercent);			
			/********************************************************************/
			/*************Step3 Construct Segment Tree ends**********************/
			/********************************************************************/

			//No change here		
			/********************************************************************/
			/**********Step4 Construct Indexed Segment Tree Forest starts********/
			/********************************************************************/
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap = SegmentTree.constructIndexedSegmentTreeForestStep4InParallelInChromBased(
					mode,
					chrNumber2SegmentTreeNodeMap,
					presetValue,
					chrNumber2IndexingLevelMap);		
			/********************************************************************/
			/**********Step4 Construct Indexed Segment Tree Forest ends**********/
			/********************************************************************/
			
			//for less memory usage
			chrNumber2IntervalListMap = null;
			chrNumber2SortedEndPointsArrayMap = null;

			return chrNumber2HashIndex2SegmentTreeNodeMapMap;	
									
	}
	

}
