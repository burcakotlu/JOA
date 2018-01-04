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
	//Stdout Output All
	public static void writeToStdOutAll(
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap, 
			int numberofIntervalSets){
		
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
			
			
			//Close the Writer
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				

	}
	

	/**************************************************************************************************/
	/*************************** For JOA GUI STARTS ***************************************************/
	/**************************************************************************************************/	
	public static void writeToAFile(
			TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap, 
			String outputFile){
		
		List<Interval> overlappingIntervalList = null;
		
		try {
			
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fileWriter);
			
			//Write in an order
			//chr1 --> 1, chr2 --> 2, ..., chrX --> 23, chrY --> 24, chrM --> 25
			for(int i=1; i<=25;i++) {
				
				overlappingIntervalList=chrNumber2ResultingIntervalListMap.get(i);
				
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

	
	
	
	public static void writeToAFileAll(
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap, 
			int numberofIntervalSets, 
			String outputFile){
		
		List<List<Interval>> overlappingIntervalListList = null;
		List<Interval> overlappingIntervalList = null;
		
		int j;
		
		     
		try {
			
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fileWriter);

			
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
			
			
			//Close the Writer
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		

	}
	/**************************************************************************************************/
	/*************************** For JOA GUI ENDS *****************************************************/
	/**************************************************************************************************/
	
	
	/*****************************************************************************/
	/******************** FOR JOA GUI STARTS *************************************/
	/*****************************************************************************/
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly_GUI(
			String[] intervalSetsFileNames,
			String outputFile) throws IOException {
		
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
				
		SegmentTree.createForkJoinPool();		
		chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
				intervalSetsFileNames.length,
				intervalSetsFileNames);						
		SegmentTree.shutdownForkJoinPool();		
		
		writeToAFile(chrNumber2ResultingIntervalListMap, outputFile);
		chrNumber2ResultingIntervalListMap= null;
	
				
	}	
	
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly_GUI(
			int presetValue,
			IndexingLevelDecisionMode mode,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod,
			String outputFile) throws IOException {
				
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
		
		SegmentTree.createForkJoinPool();		
		chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
				presetValue,
				mode,
				intervalSetsFileNames.length,
				intervalSetsFileNames,
				numberofPercent,
				searchMethod);
		SegmentTree.shutdownForkJoinPool();

		writeToAFile(chrNumber2ResultingIntervalListMap, outputFile);
		chrNumber2ResultingIntervalListMap= null;
						
		
	}	
	
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_GUI(
			String[] intervalSetsFileNames,
			String outputFile) throws IOException {
		
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
		
				
		/*********************************************************************************************/
		/****************** Segment Tree starts*******************************************************/
		/*********************************************************************************************/			
		SegmentTree.createForkJoinPool();
		chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
				intervalSetsFileNames.length,
				intervalSetsFileNames);		
		SegmentTree.shutdownForkJoinPool();

		writeToAFileAll(chrNumber2OverlappingIntervalsListListMap, intervalSetsFileNames.length, outputFile);
		chrNumber2OverlappingIntervalsListListMap=null;
		/***********************************************************************************************************/
		/******************Segment Tree ends*********************************************************/
		/***********************************************************************************************************/
		
	}
	
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_GUI(
			int presetValue,
			IndexingLevelDecisionMode mode,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod,
			String outputFile) throws IOException {	
		
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
					
		/***********************************************************************************************************/
		/******************Indexed Segment Tree Forest starts*******************************************************/
		/***********************************************************************************************************/			
		SegmentTree.createForkJoinPool();
		chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
				presetValue,
				mode,
				intervalSetsFileNames.length,
				intervalSetsFileNames,
				numberofPercent,
				searchMethod);
		SegmentTree.shutdownForkJoinPool();

		writeToAFileAll(chrNumber2OverlappingIntervalsListListMap, intervalSetsFileNames.length, outputFile);
		chrNumber2OverlappingIntervalsListListMap = null;
		/***********************************************************************************************************/
		/******************Indexed Segment Tree Forest ends*********************************************************/
		/***********************************************************************************************************/
				
	}	

	/*****************************************************************************/
	/******************** FOR JOA GUI ENDS ***************************************/
	/*****************************************************************************/
	
	
	//DEC 15, 2017 starts
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames) throws IOException {
		
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
				
		SegmentTree.createForkJoinPool();		
		chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
				numberofIntervalSetInputFiles,
				intervalSetsFileNames);				
		SegmentTree.shutdownForkJoinPool();		

		writeToStdOut(chrNumber2ResultingIntervalListMap);		
		chrNumber2ResultingIntervalListMap= null;
						
	}
	//DEC 15, 2017 ends

	
	
	//October 18, 2017 starts
	//Called for Segment Tree
	//Output all overlapping intervals and resulting interval
	public static void constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames) throws IOException {
		
		//Overlapping Intervals found by Segment Tree
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
	
		SegmentTree.createForkJoinPool();
		chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
				numberofIntervalSetInputFiles,
				intervalSetsFileNames);
		SegmentTree.shutdownForkJoinPool();


		writeToStdOutAll(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length);
		chrNumber2OverlappingIntervalsListListMap = null;
		
	}
	//October 18, 2017 ends
	
	
	//DEC 14, 2017 starts
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly(
			int presetValue,
			IndexingLevelDecisionMode mode,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod) throws IOException {
				
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<Interval>> chrNumber2ResultingIntervalListMap = null;
						
		SegmentTree.createForkJoinPool();		
		chrNumber2ResultingIntervalListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
					presetValue,
					mode,
					numberofIntervalSetInputFiles,
					intervalSetsFileNames,
					numberofPercent,
					searchMethod);
		SegmentTree.shutdownForkJoinPool();
		
		writeToStdOut(chrNumber2ResultingIntervalListMap);
		chrNumber2ResultingIntervalListMap= null;
			
		
	}	
	//DEC 14, 2017 ends
	
	//Nov 6, 2017 starts
	//Called for Indexed Segment Tree Forest
	//Output all overlapping intervals and resulting interval
	public static void constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
			int presetValue,
			IndexingLevelDecisionMode mode,
		int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod) throws IOException {	
		
		//Overlapping Intervals found by Indexed Segment Tree Forest
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap = null;
					
		SegmentTree.createForkJoinPool();
		chrNumber2OverlappingIntervalsListListMap = SegmentTree.findCommonIntervals_Construct_Search_FileBased_ChromBased(
				presetValue,
				mode,
				numberofIntervalSetInputFiles,
				intervalSetsFileNames,
				numberofPercent,
				searchMethod);
		SegmentTree.shutdownForkJoinPool();

		writeToStdOutAll(chrNumber2OverlappingIntervalsListListMap,intervalSetsFileNames.length);
		chrNumber2OverlappingIntervalsListListMap = null;
				
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

	/*************************************************/
	/****Command  Line Default Parameter starts*******/
	/*************************************************/
    @Parameter(names={"--preset", "-p"})
    int preset=1000000;
    
    @Parameter(names={"--percentage", "-pe"})
    int percentage=1;
    
//    @Parameter(names={"--numofRepeats", "-r"})
//    int numberofRepetitions=1;
    
	@Parameter(names = {"--files","-f"}, variableArity = true)
	public List<String> filenames = new ArrayList<>();

    @Parameter(names={"--output", "-o"})
    String outputType = Commons.ONLY_RESULTING_INTERVAL;
	
    @Parameter(names={"--tree", "-t"})
    String treeType = Commons.SEGMENT_TREE;
	/*************************************************/
	/****Command  Line Default Parameter ends*********/
	/*************************************************/

	
    public static void main(String[] args) {		
		
		//If there are command line arguments bypass GUI (-f f1 f1 ... fn)
		JointOverlapAnalysis joa = new JointOverlapAnalysis();
        JCommander.newBuilder().addObject(joa).build().parse(args);
        
        String[] filesArray = joa.filenames.toArray(new String[joa.filenames.size()]);
        
        //FileWriter fileWriter = null;
		//BufferedWriter bufferedWriter = null;
		
		try {
			
			//fileWriter = new FileWriter("/home/burcak/Documents/JOA_Runtime_Comparisons/JOA_Runtimes.txt",true);
			//bufferedWriter = new BufferedWriter(fileWriter);
			
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
									joa.filenames.size(),
									filesArray,
									joa.percentage,
									SearchMethod.NOT_SET);
							break;
						
						case INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest_ResultingIntervalOnly(
									joa.preset,
									IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
									joa.filenames.size(),
									filesArray,
									joa.percentage,
									SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED);
							break;
							
						case SEGMENT_TREE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
			    					joa.filenames.size(),
			    					filesArray);
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
								//joa.numberofRepetitions,
								joa.filenames.size(),
								filesArray,
								joa.percentage,
								SearchMethod.NOT_SET);

							break;
						
						case INDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
									joa.preset,
									IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
									//joa.numberofRepetitions,
									joa.filenames.size(),
									filesArray,
									joa.percentage,
									SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED);
			
							break;
							
						case SEGMENT_TREE:
							JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
			    					//joa.numberofRepetitions,
			    					joa.filenames.size(),
			    					filesArray);

							break;
						
						default:
							break;
						
							
					}//End of switch
					
				}//Mostly for debugging. All overlapping intervals and resulting interval.	        	
	        	
	        } //End of if filenames are valid
					
			//Close bufferedWriter
			//bufferedWriter.write("##################################################################"+ System.getProperty("line.separator"));
			//bufferedWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}			
          
	}


}
