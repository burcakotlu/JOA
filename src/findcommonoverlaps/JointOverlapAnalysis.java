/**
 * 
 */
package findcommonoverlaps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import auxiliary.FileOperations;
import common.Commons;
import enumtypes.ChromosomeName;
import enumtypes.DataStructureType;
import enumtypes.IndexingLevelDecisionMode;
import enumtypes.OutputType;
import enumtypes.SearchMethod;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;
import preset.OfflinePresetValueDecision;
import trees.segmenttree.SegmentTree;
import trees.segmenttree.SegmentTreeNode;

/**
 * @author Burcak Otlu
 * @date May 10, 2017
 * @project JOA
 * 
 * 
 * Indexed Segment Tree Forest for Joint Overlap Analysis of N Interval Sets 
 * JOA
 *
 */
public class JointOverlapAnalysis {
	

	//7 FEB 2017
	//Construct segment tree in parallel for each chromosome
	public static TIntObjectMap<SegmentTreeNode> constructNormalSegmentTreeParallelInChromBased(
			String intervalSetInputFileName){
		
				
		TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints = new TIntObjectHashMap<TIntArrayList>();
		TIntObjectMap<List<Interval>> chrNumber2IntervalsMap = new TIntObjectHashMap<List<Interval>>();
		
		/********************************************************************/
		/*******************Step1 Fill End Points starts*********************/
		/********************************************************************/
		SegmentTree.fill(chrNumber2UnSortedEndPoints,chrNumber2IntervalsMap,intervalSetInputFileName);
		/********************************************************************/
		/*******************Step1 Fill End Points ends***********************/
		/********************************************************************/
		
				
//		/********************************************************************/
//		/*************Step2 Sort End Points in ascending order starts********/
//		/********************************************************************/
//		TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap = Sorting.sort(chrNumber2UnSortedEndPoints);
//		/********************************************************************/
//		/*************Step2 Sort End Points in ascending order ends**********/
//		/********************************************************************/

		/********************************************************************/
		/******** New Step2 Sort End Points in chrom based starts ***********/
		/********************************************************************/		
		//DEC 17, 2017 starts
		TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap = new TIntObjectHashMap<int[]>(25);				
		SegmentTree.sortParallelInChromBased(chrNumber2UnSortedEndPoints,chrNumber2SortedEndPointsArrayMap);
		//DEC 17, 2017 ends
		/********************************************************************/
		/******** New Step2 Sort End Points in chrom based ends *************/
		/********************************************************************/		
		
		
		/********************************************************************/
		/*************Step3 Construct Segment Tree starts********************/
		/********************************************************************/
		TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap = SegmentTree.constructSegmentTreeStep3InParallelChromBased(chrNumber2SortedEndPointsArrayMap,chrNumber2IntervalsMap);
		/********************************************************************/
		/*************Step3 Construct Segment Tree ends**********************/
		/********************************************************************/
		
		//For Less Memory Usage
		chrNumber2UnSortedEndPoints = null;
		chrNumber2IntervalsMap = null;
		chrNumber2SortedEndPointsArrayMap = null;
		
		return chrNumber2SegmentTreeNodeMap;	

	}
	//September 27, 2017 ends 
	
	
	
	//September 27, 2017 starts
	public static TIntObjectMap<TIntObjectMap<SegmentTreeNode>> constructIndexedSegmentTreeForestParallelInChromBased(
			IndexingLevelDecisionMode mode,
			int presetValue,
			String intervalSetInputFileName,
			int numberofPercent){
		
		
		TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints = new TIntObjectHashMap<TIntArrayList>();
		TIntObjectMap<List<Interval>> chrNumber2IntervalsMap = new TIntObjectHashMap<List<Interval>>();
		
		/********************************************************************/
		/*******************Step1 Fill End Points starts*********************/
		/********************************************************************/
		SegmentTree.fill(chrNumber2UnSortedEndPoints,chrNumber2IntervalsMap,intervalSetInputFileName);
		//System.out.println("Fill the end points from " +  intervalSetInputFileName + " took: " + (float)(timeAfter - timeBefore) + " milliseconds " + (float)((timeAfter - timeBefore)/1000)  + " seconds");
		/********************************************************************/
		/*******************Step1 Fill End Points ends***********************/
		/********************************************************************/
				

		/********************************************************************/
		/*************Step2 Sort End Points in ascending order starts********/
		/********************************************************************/
		//TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap = Sorting.sort(chrNumber2UnSortedEndPoints);
		/********************************************************************/
		/*************Step2 Sort End Points in ascending order ends**********/
		/********************************************************************/
		
		/********************************************************************/
		/********** New Step2 Sort End Points in ascending order starts******/
		/********************************************************************/
		//DEC 17, 2017 starts
		TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap = new TIntObjectHashMap<int[]>(25);				
		SegmentTree.sortParallelInChromBased(chrNumber2UnSortedEndPoints,chrNumber2SortedEndPointsArrayMap);
		//DEC 17, 2017 ends
		/********************************************************************/
		/********** New Step2 Sort End Points in ascending order ends *******/
		/********************************************************************/
		
				
		
		/******************************************************************/
		/******* Combined Step3 and Step4 1st way starts ******************/
		/******************************************************************/
		TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap = SegmentTree.
				constructSegmentTreeAndIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
				chrNumber2SortedEndPointsArrayMap,
				chrNumber2IntervalsMap,
				numberofPercent,		
				mode,
				presetValue);
		/******************************************************************/
		/******* Combined Step3 and Step4 1st way ends ********************/
		/******************************************************************/
		
		//for less memory usage
		chrNumber2UnSortedEndPoints = null;
		chrNumber2SortedEndPointsArrayMap=null;
		chrNumber2IntervalsMap =null;
				
		return chrNumber2HashIndex2SegmentTreeNodeMapMap;	

	}	
	//September 27, 2017 ends
	
	

	
	//For Debugging
	public static void printNumberofOverlappingIntervalsPerChr(
			TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap){
		
		System.out.println("------------------------------------------------------------------------");
		for(TIntObjectIterator<List<Interval>> itr = chrNumber2OverlappingIntervalsListMap.iterator();itr.hasNext();){
			itr.advance();
			
			System.out.println("chr:" + itr.key() + " numberof overlapping intervals:" + itr.value().size());
		}
		System.out.println("------------------------------------------------------------------------");
		
	}
	
/**********************************************************************/	
//  sample code starts
//	OutputStream out = new BufferedOutputStream(System.out );
//	int bufSize = 1024 * 1024; //1MB
//	byte[] byteBuf = new byte[bufSize];
//	int currentOffset = 0;
	

//  byte[] outputLine=(ChromosomeName.convertInttoString(chrNumber) + "\t" + overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  overlappingIntervalList.get(j).getHigherEndPoint() + System.getProperty("line.separator")).getBytes();				
//	if ((currentOffset+outputLine.length)<byteBuf.length) {
//		
//		System.arraycopy(outputLine, 0,byteBuf, currentOffset,outputLine.length);
//	    currentOffset += outputLine.length;
//		
//	}else {
//		 out.write(byteBuf);
//		 currentOffset =0;
//	}
//  sample code ends
/**********************************************************************/	
	
	//DEC 15, 2017 starts
	public static void writeToStdOut(
			TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap){
		
		List<Interval> overlappingIntervalList = null;
		
		//BufferedWriter performed better than BufferedOutputStream
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
	       		
		        
		try {
			
			//Write in an order
			//chr1 --> 1, chr2 --> 2, ..., chrX --> 23, chrY --> 24, chrM --> 25
			for(int i=1; i<=25;i++) {
				
				overlappingIntervalList=chrNumber2OverlappingIntervalsListMap.get(i);
				
				if (overlappingIntervalList!=null) {
					
					for(int j=0; j<overlappingIntervalList.size(); j++){
						//To write in bed format plus 1
						out.write(ChromosomeName.convertInttoString(i) + "\t" + overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  (overlappingIntervalList.get(j).getHigherEndPoint()+1) + System.getProperty("line.separator"));											
					}//End of for each interval in intervalList
					
					
				}//End of IF
				
			}//End of for
	
			
			//close
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				

	}

	//DEC 15, 2017 ends
	
	//DEC 12, 2017 
	//Std Output
	public static void writeToStdOut(
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap, 
			int numberofIntervalSets,
			OutputType outputType){
		
		List<List<Interval>> overlappingIntervalListList = null;
		List<Interval> overlappingIntervalList = null;
		
		int j;
		
		//BufferedWriter performed better than BufferedOutputStream
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));	       		
	        
	try {
		
		//Write in an order
		//chr1 --> 1, chr2 --> 2, ..., chrX --> 23, chrY --> 24, chrM --> 25
		for(int chrNum=1; chrNum<=25;chrNum++) {
			
			overlappingIntervalListList =chrNumber2OverlappingIntervalsListListMap.get(chrNum);
			
			for(int i=0; i<overlappingIntervalListList.size(); i++){
				
				overlappingIntervalList = overlappingIntervalListList.get(i);
				
				
				//All overlapping intervals and resulting interval
				out.write(ChromosomeName.convertInttoString(chrNum) + "\t");
				
				for(j=0; j<overlappingIntervalList.size(); j++){

					//Order is important
					//First info
					if (overlappingIntervalList.get(j).getInfo()!=null)
						out.write(overlappingIntervalList.get(j).getInfo() + "\t");

					//To write in bed format endPoint plus 1
					out.write(overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  (overlappingIntervalList.get(j).getHigherEndPoint()+1) + "\t");
					
				}//End of for each Interval in intervalList
			
				out.write(System.getProperty("line.separator"));
				
										
			}//End of for each intervalList in intervalListList

		}//End of for each chromosome
		

//		for(TIntObjectIterator<List<List<Interval>>>  itr = chrNumber2OverlappingIntervalsListListMap.iterator();itr.hasNext();){
//			
//			itr.advance();
//			
//			chrNumber = itr.key();
//			overlappingIntervalListList = itr.value();
//			
//			for(int i=0; i<overlappingIntervalListList.size(); i++){
//				
//				overlappingIntervalList = overlappingIntervalListList.get(i);
//				
//				
//				//All overlapping intervals and resulting interval
//				out.write(ChromosomeName.convertInttoString(chrNumber) + "\t");
//				
//				for(j=0; j<overlappingIntervalList.size(); j++){
//
//					//Order is important
//					//First info
//					if (overlappingIntervalList.get(j).getInfo()!=null)
//						out.write(overlappingIntervalList.get(j).getInfo() + "\t");
//
//					//To write in bed format endPoint plus 1
//					out.write(overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  (overlappingIntervalList.get(j).getHigherEndPoint()+1) + "\t");
//					
//				}//End of for each Interval in intervalList
//			
//				out.write(System.getProperty("line.separator"));
//				
//										
//			}//End of for each intervalList in intervalListList
//			
//		}//End of for each chromosome
		
		//Close the Writer
		out.close();
		
		
	} catch (IOException e) {
		e.printStackTrace();
	}
				

	}
	

	
	//For Debugging 
	public static void writeToAFile(
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap, 
			int numberofIntervalSets, 
			String informativeFileName){
		
		int chrNumber;
		List<List<Interval>> overlappingIntervalListList = null;
		List<Interval> overlappingIntervalList = null;
		
		FileWriter searchResultsFileWriter =null;
		BufferedWriter searchResultsBufferedWriter = null;
		
		try {
			
			//For debug
			//searchResultsFileWriter = FileOperations.createFileWriter(informativeFileName + "_searchResults.txt");
			
			//For JOA GUI
			searchResultsFileWriter = FileOperations.createFileWriter(informativeFileName);
			//For JOA GUI
			
			searchResultsBufferedWriter = new BufferedWriter(searchResultsFileWriter);		
			
			//Write header line
			searchResultsBufferedWriter.write("chrNumber"+ "\t");
			
			//Write header line			
			for(int i=0; i<numberofIntervalSets;i++){
				searchResultsBufferedWriter.write("LowEndPoint\tHighEndPoint\t");
			}
			
			//Write header line
			searchResultsBufferedWriter.write("CommonLowEndPoint\tCommonHighEndPoint" + System.getProperty("line.separator"));			
			
			for(TIntObjectIterator<List<List<Interval>>>  itr = chrNumber2OverlappingIntervalsListListMap.iterator();itr.hasNext();){
				
				itr.advance();
				
				chrNumber = itr.key();
				overlappingIntervalListList = itr.value();
				
				for(int i=0; i<overlappingIntervalListList.size(); i++){
					overlappingIntervalList = overlappingIntervalListList.get(i);
					
					searchResultsBufferedWriter.write(ChromosomeName.convertInttoString(chrNumber) + "\t");
						
					//One way of output
					//All overlapping intervals and resulting interval
//					for(int j=0; j<overlappingIntervalList.size(); j++){
//
//						//Order is important
//						//First info
//						if (overlappingIntervalList.get(j).getInfo()!=null)
//							searchResultsBufferedWriter.write(overlappingIntervalList.get(j).getInfo() + "\t");
//
//						searchResultsBufferedWriter.write(overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  overlappingIntervalList.get(j).getHigherEndPoint() + "\t");
//						
//		
//					}//End of for each Interval in intervalList
					
					
					//Another way of output
					//Just Output the resulting interval 
					int j = overlappingIntervalList.size()-1;
					searchResultsBufferedWriter.write(overlappingIntervalList.get(j).getLowerEndPoint() + "\t" +  overlappingIntervalList.get(j).getHigherEndPoint() + "\t");

					searchResultsBufferedWriter.write(System.getProperty("line.separator"));
					
					
				}//End of for each intervalList in intervalListList
				
			}//End of for each chromosome
			
			//Close
			searchResultsBufferedWriter.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	//DEC 15, 2017 starts
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			int numberofRepetations,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter) throws IOException {
		
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
				
		long startTime1, startTime2, readConstructSearchTime, writeOutputTime, totalTime;
			
		SegmentTree.createForkJoinPool();
		
		for(int i=0; i < numberofRepetations; i++){
			
			startTime1 = System.nanoTime();		
			//Keeps list of overlapping intervals and their common overlap
			chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
					numberofIntervalSetInputFiles,
					intervalSetsFileNames,
					bufferedWriter);				
			readConstructSearchTime = System.nanoTime()-startTime1;
					
			
			startTime2 = System.nanoTime();		
			//write to std out
			writeToStdOut(chrNumber2ResultingIntervalListMap);		
			writeOutputTime = System.nanoTime()-startTime2;
			
			totalTime = System.nanoTime()-startTime1;
			bufferedWriter.write("SegmentTree" + "\t" +(i+1) + "\t" + readConstructSearchTime/Commons.ONE_MILLION_FLOAT + "\t" + writeOutputTime/Commons.ONE_MILLION_FLOAT + "\t" + totalTime/Commons.ONE_MILLION_FLOAT + System.getProperty("line.separator"));
			
			chrNumber2ResultingIntervalListMap= null;
		
		}//End of for each repetition
		
		bufferedWriter.write("##########################################################################" + System.getProperty("line.separator"));
			
		SegmentTree.shutdownForkJoinPool();		
		//System.gc();  
				
	}
	//DEC 15, 2017 ends

	
	
	//October 18, 2017 starts
	//Called for Segment Tree
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
			int numberofRepetations,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter,
			OutputType outputType) throws IOException {
		
		//Overlapping Intervals found by Segment Tree
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
		
				
		long timeBefore = Long.MIN_VALUE;
		long timeAfter = Long.MIN_VALUE;
				
		/***********************************************************************************************************/
		/****************** Segment Tree starts*******************************************************/
		/***********************************************************************************************************/			
		long segmentTreeTotalTime = 0l;
		long oneSegmentTreeTotalTime = 0l;
		long lowestOneSegmentTreeTotalTime = Long.MAX_VALUE;

		//For JOA GUI comment this
		if (bufferedWriter!=null) {
			bufferedWriter.write("***************************************************" + System.getProperty("line.separator"));
			bufferedWriter.flush();			
		}
		//For JOA GUI 
		
		//Segment Tree
		for(int j=0; j < numberofRepetations; j++){
			
			//FOR JOA GUI have this
			//JointOverlapAnalysis.appendNewTextToLogArea("Joint Overlap Analysis using Segment Tree started.");
			//FOR JOA GUI
			
			//Initialize
			oneSegmentTreeTotalTime=0l;
			
			timeBefore = System.currentTimeMillis();
			
			SegmentTree.createForkJoinPool();
							
			//Keeps list of overlapping intervals and their common overlap
			chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
					numberofIntervalSetInputFiles,
					intervalSetsFileNames);
			
			SegmentTree.shutdownForkJoinPool();

			timeAfter = System.currentTimeMillis();				
			segmentTreeTotalTime += timeAfter-timeBefore;
			
			oneSegmentTreeTotalTime = timeAfter-timeBefore;
			
			if(oneSegmentTreeTotalTime < lowestOneSegmentTreeTotalTime) {
				lowestOneSegmentTreeTotalTime = oneSegmentTreeTotalTime;
			}

			//For JOA GUI comment this
			if (bufferedWriter!=null) {
				bufferedWriter.write("For each repetion: "+ j +" ConstructSearch_Parallel_FileBased_ChromBased_SegmentTree" + "\t" + "" + "\t" + "" + "\t" + (oneSegmentTreeTotalTime*1.0f) + "\t" + "presetValue" + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + "IndexingLevelDecisionMode"  + System.getProperty("line.separator"));
				bufferedWriter.flush();
			}
			//For JOA GUI 
			
			
			//FOR JOA GUI have this
			//JointOverlapAnalysis.appendNewTextToLogArea("Joint Overlap Analysis using Segment Tree ended.");
			//FOR JOA GUI

			//System.gc(); 
										
		}//End of number of repetitions
										
		//For JOA GUI comment this
		if (bufferedWriter!=null) {			
			bufferedWriter.write("ConstructSearch_Parallel_FileBased_ChromBased_SegmentTree Avg Total Runtime:" + "\t" + "" + "\t" + "" + "\t" + (segmentTreeTotalTime*1.0f)/numberofRepetations + "\t" + "presetValue" + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + "IndexingLevelDecisionMode"  + System.getProperty("line.separator"));
			bufferedWriter.write("ConstructSearch_Parallel_FileBased_ChromBased_SegmentTree Min Runtime: " + "\t" + "" + "\t" + "" + "\t" + lowestOneSegmentTreeTotalTime + "\t" + "presetValue" + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + "IndexingLevelDecisionMode"  + System.getProperty("line.separator"));
			bufferedWriter.write("***************************************************" + System.getProperty("line.separator"));
			bufferedWriter.flush();
		}
		//For JOA GUI 
		
		
		//For JOA GUI have this
		//String userWorkingDirectory = System.getProperty("user.dir");
		//JointOverlapAnalysis.appendNewTextToLogArea("Output is provided under " + userWorkingDirectory + System.getProperty("file.separator") + "JointlyOverlappingIntervals.txt");
		//write(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length, userWorkingDirectory + System.getProperty("file.separator") + "JointlyOverlappingIntervals.txt");
		//JointOverlapAnalysis.appendNewTextToLogArea("#######################################################");
		//For JOA GUI


		//Debugging starts
		//Write overlapping intervals to file
		writeToStdOut(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length,outputType);
		//Debugging ends											
		/***********************************************************************************************************/
		/******************Segment Tree ends*********************************************************/
		/***********************************************************************************************************/
		
	}
	//October 18, 2017 ends
	
	
	//DEC 14, 2017 starts
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly(
			int presetValue,
			IndexingLevelDecisionMode mode,
			int numberofRepetations,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter,
			int numberofPercent,
			SearchMethod searchMethod) throws IOException {
				
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
		
		long startTime1, startTime2, readConstructSearchTime, writeOutputTime, totalTime;
				
		SegmentTree.createForkJoinPool();
		
		for(int i=0; i < numberofRepetations; i++){
			
			startTime1 = System.nanoTime();		
			//Keeps list of overlapping intervals and their common overlap
			//bufferedWriter is added for debug
			chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
					presetValue,
					mode,
					numberofIntervalSetInputFiles,
					intervalSetsFileNames,
					numberofPercent,
					searchMethod,
					bufferedWriter);
			readConstructSearchTime = System.nanoTime()-startTime1;
			
			startTime2 = System.nanoTime();					
			//write to std out
			writeToStdOut(chrNumber2ResultingIntervalListMap);
			writeOutputTime = System.nanoTime()-startTime2;
			
			totalTime = System.nanoTime()-startTime1;
			
			if (searchMethod.isNOT_SET()) {
				bufferedWriter.write("IndexedSegmentTreeForest" + "\t" + presetValue + "\t" + readConstructSearchTime/Commons.ONE_MILLION_FLOAT + "\t" + writeOutputTime/Commons.ONE_MILLION_FLOAT + "\t" + totalTime/Commons.ONE_MILLION_FLOAT + System.getProperty("line.separator"));
			} else if (searchMethod.isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED()) {
				bufferedWriter.write("IndexedSegmentTreeForest_STAR" + "\t" + presetValue + "\t" + readConstructSearchTime/Commons.ONE_MILLION_FLOAT + "\t" + writeOutputTime/Commons.ONE_MILLION_FLOAT + "\t" + totalTime/Commons.ONE_MILLION_FLOAT + System.getProperty("line.separator"));
			}				
			
			
			chrNumber2ResultingIntervalListMap= null;
			
		}//End of for each repetition
		
		bufferedWriter.write("##########################################################################" + System.getProperty("line.separator"));

			
		SegmentTree.shutdownForkJoinPool();
		//System.gc();  
		
	}	
	//DEC 14, 2017 ends
	
	//Nov 6, 2017 starts
	//Called for Indexed Segment Tree Forest
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
			int presetValue,
			IndexingLevelDecisionMode mode,
			int numberofRepetations,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter,
			int numberofPercent,
			SearchMethod searchMethod,
			OutputType outputType) throws IOException {	
		
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
					
		long timeBefore = Long.MIN_VALUE;
		long timeAfter = Long.MIN_VALUE;
				
		/***********************************************************************************************************/
		/******************Indexed Segment Tree Forest starts*******************************************************/
		/***********************************************************************************************************/			
		long indexedSegmentTreeForestTotalTime = 0l;
		long forOnceIndexedSegmentTreeForestTotalTime = 0l;
		long lowestOneIndexedSegmentTreeForestTotalTime = Long.MAX_VALUE;
		
		//For JOA GUI comment this
		if (bufferedWriter!=null) {
			bufferedWriter.write("***************************************************" + System.getProperty("line.separator"));
			bufferedWriter.flush();			
		}
		//For JOA GUI
				
		//Indexed Segment Tree Forest
		for(int j=0; j < numberofRepetations; j++){
			
			//FOR JOA GUI Have this
			//JointOverlapAnalysis.appendNewTextToLogArea("Joint Overlap Analysis using Indexed Segment Tree Forest started.");
			//FOR JOA GUI
			
			//Debug starts
			//System.out.println("Indexed Segment Tree Forest Repetition: " +  j + " " + mode.convertEnumtoString() + " starts+++++++++++++++++++++++++++++");
			//Debug ends
			
			//Initialize
			forOnceIndexedSegmentTreeForestTotalTime=0l;
			
			timeBefore = System.nanoTime();	
			
			SegmentTree.createForkJoinPool();
											
			//Keeps list of overlapping intervals and their common overlap
			chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
					presetValue,
					mode,
					numberofIntervalSetInputFiles,
					intervalSetsFileNames,
					numberofPercent,
					searchMethod);
						
			SegmentTree.shutdownForkJoinPool();

			timeAfter = System.nanoTime();		
			
			indexedSegmentTreeForestTotalTime += timeAfter-timeBefore;
			
			forOnceIndexedSegmentTreeForestTotalTime = timeAfter-timeBefore;
			
			if (forOnceIndexedSegmentTreeForestTotalTime < lowestOneIndexedSegmentTreeForestTotalTime) {
				lowestOneIndexedSegmentTreeForestTotalTime = forOnceIndexedSegmentTreeForestTotalTime;
			}
			
			//For Analysis Have this
			if (bufferedWriter!=null) {
				bufferedWriter.write("For each repetion: "+ j +" ConstructSearch_Parallel_FileBased_ChromBased_IndexedSegmentTree" + "\t" + searchMethod.convertEnumtoString() + "\t" + "" + "\t" + (forOnceIndexedSegmentTreeForestTotalTime/Commons.ONE_MILLION_FLOAT) + "\t" + presetValue + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + mode.convertEnumtoString()  + System.getProperty("line.separator"));
				bufferedWriter.flush();
			}
			//For Analysis Have this
			
			//debug starts
			//System.out.println("Indexed Segment Tree Forest Repetition: " +  j + " " + mode.convertEnumtoString()  + " ends+++++++++++++++++++++++++++++");
			//debug ends
			
			//FOR JOA GUI Have this
			//JointOverlapAnalysis.appendNewTextToLogArea("Joint Overlap Analysis using Indexed Segment Tree Forest ended.");
			//FOR JOA GUI
						
							
			 //System.gc();  
		}//End of number of repetitions
										
		
		//For JOA GUI comment this
		if (bufferedWriter!=null) {
			bufferedWriter.write("ConstructSearch_Parallel_FileBased_ChromBased_IndexedSegmentTree Avg Total Runtime:" + "\t" + "" + "\t" + "" + "\t" + (indexedSegmentTreeForestTotalTime/Commons.ONE_MILLION_FLOAT)/numberofRepetations + "\t" + presetValue + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + mode.convertEnumtoString()  + System.getProperty("line.separator"));
			bufferedWriter.write("ConstructSearch_Parallel_FileBased_ChromBased_IndexedSegmentTree Min Runtime: " + "\t" + "" + "\t" + "" + "\t" + (lowestOneIndexedSegmentTreeForestTotalTime/Commons.ONE_MILLION_FLOAT) + "\t" + presetValue + "\t" + numberofRepetations + "\t" + numberofIntervalSetInputFiles + "\t" + mode.convertEnumtoString()  + System.getProperty("line.separator"));
			bufferedWriter.write("***************************************************" + System.getProperty("line.separator"));
			bufferedWriter.flush();
		}
		//For JOA GUI
		
		//For JOA GUI Have this
		//String userWorkingDirectory = System.getProperty("user.dir");
		//JointOverlapAnalysis.appendNewTextToLogArea("Output is provided under " + userWorkingDirectory + System.getProperty("file.separator") + "JointlyOverlappingIntervals.txt");
		//write(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length, userWorkingDirectory + System.getProperty("file.separator") + "JointlyOverlappingIntervals.txt");
		//JointOverlapAnalysis.appendNewTextToLogArea("#######################################################");
		//For JOA GUI
		
		
		//Debugging starts
		//Write overlapping intervals to file
		writeToStdOut(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length,outputType);
		//Debugging ends				
		
		//Free space
		chrNumber2OverlappingIntervalsListListMap = null;
		/***********************************************************************************************************/
		/******************Indexed Segment Tree Forest ends*********************************************************/
		/***********************************************************************************************************/
		
		
	}	
	//Nov 6, 2017 ends
	
	
	
	
	//Nov 4, 2017
	public static TIntObjectMap<TIntIntMap> findOfflineFileBasedChrBasedPresetValues(String[] intervalSetsFileNames){
		
		TIntObjectMap<TIntIntMap> intervalSetFileNumber2ChrNumber2PresetValueMap = new TIntObjectHashMap<TIntIntMap>(intervalSetsFileNames.length);
		 
		TIntIntMap chrNumber2ChromSizeMap = new  TIntIntHashMap();
		OfflinePresetValueDecision.fillMap(chrNumber2ChromSizeMap);
		

		 for(int i=0; i<intervalSetsFileNames.length; i++) {
			 
			 TIntObjectMap<TIntObjectMap<TIntIntMap>> chrNumber2PresetValue2HashIndex2NumberOfElementsMap = new TIntObjectHashMap<TIntObjectMap<TIntIntMap>> ();
			 
			 OfflinePresetValueDecision.readtheFileAndFillTheMap(intervalSetsFileNames[i],chrNumber2PresetValue2HashIndex2NumberOfElementsMap);
			 OfflinePresetValueDecision.calculateMeanStdDev(i,intervalSetsFileNames[i],chrNumber2PresetValue2HashIndex2NumberOfElementsMap,chrNumber2ChromSizeMap,null);
			 OfflinePresetValueDecision.calculateFilebasedChrBasedPresetValue(i,chrNumber2PresetValue2HashIndex2NumberOfElementsMap,intervalSetFileNumber2ChrNumber2PresetValueMap);
			 
		 }
		 
		return intervalSetFileNumber2ChrNumber2PresetValueMap;
	}

	/*****************************************/
	/****Command  Line Parameter starts*******/
	/*****************************************/
    @Parameter(names={"--preset", "-p"})
    int preset=1000000;
    
    @Parameter(names={"--percentage", "-pe"})
    int percentage=1;
    
    @Parameter(names={"--numofRepeats", "-r"})
    int numberofRepetitions=1;
    
	@Parameter(names = {"--files","-f"}, variableArity = true)
	public List<String> filenames = new ArrayList<>();

    @Parameter(names={"--output", "-o"})
    String outputType = Commons.ONLY_RESULTING_INTERVAL;
	
    @Parameter(names={"--tree", "-t"})
    String treeType = Commons.INDEXED_SEGMENT_TREE_FOREST;
	/*****************************************/
	/****Command  Line Parameter ends*********/
	/*****************************************/

	
    public static void main(String[] args) {		
		
		//If there are command line arguments bypass GUI (-f f1 f1 ... fn)
		JointOverlapAnalysis joa = new JointOverlapAnalysis();
        JCommander.newBuilder().addObject(joa).build().parse(args);
        
        String[] filesArray = joa.filenames.toArray(new String[joa.filenames.size()]);
        
        FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			
			fileWriter = new FileWriter("/home/burcak/Documents/JOA_Runtime_Comparisons/JOA_Runtimes.txt",true);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			//HeaderLine
			//bufferedWriter.write("##################################################################"+ System.getProperty("line.separator"));
			
			//bufferedWriter.write("ReadFromFileTime" + "\t" + "ConstructFromFileTime"+ "\t" + "SearchTime" + "\t"+ "DataStructure" + "\t" + "Repetition" + "\t" + "ReadConstructSearchTime" + "\t" + "WriteOutputTime" + "\t" + "TotalTime"+ System.getProperty("line.separator"));			
			
	        if (FileOperations.checkIntervalSetFileNamesStringArray(filesArray)) {
	        	
	        	DataStructureType treeType = DataStructureType.convertStringtoEnum(joa.treeType);
	        	OutputType outputType = OutputType.convertStringtoEnum(joa.outputType);
	        	
	        	//Fastest one. Only resulting interval.	
				if (outputType.isONLY_RESULTING_INTERVAL()) {
					
					switch(treeType) {
					
						case INDEXED_SEGMENT_TREE_FOREST:
							
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly(
									joa.preset,
									IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
									joa.numberofRepetitions,
									joa.filenames.size(),
									filesArray,
									bufferedWriter,
									joa.percentage,
									SearchMethod.NOT_SET);
							break;
						
						case INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly(
									joa.preset,
									IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
									joa.numberofRepetitions,
									joa.filenames.size(),
									filesArray,
									bufferedWriter,
									joa.percentage,
									SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED);
							break;
							
						case SEGMENT_TREE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			    					joa.numberofRepetitions,
			    					joa.filenames.size(),
			    					filesArray,
			    					bufferedWriter);
							break;
						
						default:
							break;
						
							
					}//End of switch
					
				}
				//Requires more time and memory. All overlapping intervals and resulting interval.
				else {
					
					switch(treeType) {
					
						case INDEXED_SEGMENT_TREE_FOREST:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
								joa.preset,
								IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
								joa.numberofRepetitions,
								joa.filenames.size(),
								filesArray,
								null,
								joa.percentage,
								SearchMethod.NOT_SET,
								outputType);

							break;
						
						case INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
									joa.preset,
									IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
									joa.numberofRepetitions,
									joa.filenames.size(),
									filesArray,
									null,
									joa.percentage,
									SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED,
									outputType);
			
							break;
							
						case SEGMENT_TREE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
			    					joa.numberofRepetitions,
			    					joa.filenames.size(),
			    					filesArray,
			    					null,
			    					outputType);

							break;
						
						default:
							break;
						
							
					}//End of switch
					
				}//Mostly for debugging. All overlapping intervals and resulting interval.	        	
	        	
	        } //End of if filenames are valid
					
			//Close bufferedWriter
			//bufferedWriter.write("##################################################################"+ System.getProperty("line.separator"));
			bufferedWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}			
          
	}


}
