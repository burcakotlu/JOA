/**
 * 
 */
package findcommonoverlaps;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import common.Commons;
import enumtypes.DataStructureType;
import enumtypes.IndexingLevelDecisionMode;
import enumtypes.OutputType;
import enumtypes.SearchMethod;


/**
 * @author Burcak Otlu 
 * @date Nov 16, 2017
 * @project JOA
 */
public class JointOverlapAnalysisGUI extends JPanel{
	
	private static final long serialVersionUID = 5537906678749479177L;
	
	private static JComboBox<String> dataStructureColumn;
	
	static int numberofIntervalSetFiles = 2;
	
	
	static JTextArea logArea = new JTextArea(5,50);
	
	
	
	public static void appendNewTextToLogArea( String text) {

		logArea.append( text + System.getProperty( "line.separator"));
		logArea.setCaretPosition( logArea.getDocument().getLength());
		logArea.update(logArea.getGraphics());
		
	}

	public static void appendNewTextToLogArea( int text) {

		logArea.append( text + System.getProperty( "line.separator"));
		logArea.setCaretPosition( logArea.getDocument().getLength());
		logArea.update(logArea.getGraphics());
	}

	public static void appendNewTextToLogArea( float text) {

		logArea.append( text + System.getProperty( "line.separator"));
		logArea.setCaretPosition( logArea.getDocument().getLength());
		logArea.update(logArea.getGraphics());
	}
	


	
	public static void addNewIntervalSetPanel(
			JPanel mainPanel, 
			JPanel intervalSetFilesPanel,
			GridBagConstraints constraints, 
			JFrame frame) {
		
		
		JPanel intervalSetFilePanel = new JPanel();
		intervalSetFilePanel.setLayout(new GridBagLayout());
	
		JTextField inputFileTextField = new JTextField(30);
	
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
            	
            	JFileChooser fc = new JFileChooser();
            	int returnVal;

            	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            	
            	returnVal = fc.showOpenDialog(mainPanel);
            	if( returnVal == JFileChooser.APPROVE_OPTION){
            		
            		File file = fc.getSelectedFile();
            		inputFileTextField.setText(file.getPath() + System.getProperty( "file.separator"));
            							
            	}

            }
        });
    	
		
		JButton removeButton = new JButton("-");
		removeButton.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
            	
            	intervalSetFilesPanel.remove(intervalSetFilePanel);
            	frame.pack();
                

            }
        });	
		
		
		constraints.gridx = 0;
        constraints.gridy = 0;    
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        intervalSetFilePanel.add(browseButton, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0; 
        constraints.gridwidth = 1;        
        constraints.gridheight = 1;
        intervalSetFilePanel.add(inputFileTextField, constraints);
        
        constraints.gridx = 2;
        constraints.gridy = 0; 
        constraints.gridwidth = 1;        
        constraints.gridheight = 1;
        intervalSetFilePanel.add(removeButton, constraints);
        
        
        //Where to put?
        constraints.gridx = 0;
        constraints.gridy = numberofIntervalSetFiles++; 
        constraints.gridheight =1;
        constraints.gridwidth = 3;
        
        intervalSetFilesPanel.add(intervalSetFilePanel,constraints);
        intervalSetFilesPanel.revalidate();
        
        frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true); 
		
	}
	
	static void prepareGUI() {
		
        /**************************************************/
        /**********GUI starts******************************/
        /**************************************************/
		JFrame frame = new JFrame("Joint Overlap Analysis Framework");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		//How graphics draws to mainPanel? How is it decided? Since mainPanel is the current instance that extends JPanel
		//Therefore graphics are drawn on mainPanel
		frame.setLayout(new GridBagLayout());

	
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setMaximumSize(new Dimension(7000,200));
		
		
	
        /**************************************************/
        /**********Interval Set Files Panel starts*********/
        /**************************************************/
		JPanel intervalSetFilesPanel = new JPanel();
		intervalSetFilesPanel.setLayout(new GridBagLayout());
		
		
		
		/**************************************************/
		/***********1st IntervalSetFile Panel starts ******/
		/**************************************************/
		JPanel intervalSetFilePanel = new JPanel();
		intervalSetFilePanel.setLayout(new GridBagLayout());
		
		//JLabel Load Interval Set Files
		JLabel loadIntervalSetFilesLabel = new JLabel("Load Interval Set Files");
        
		/*******************************************************************************/
		/**********************ADD BUTTON starts****************************************/
		/*******************************************************************************/
		JButton addButton = new JButton("+");
		addButton.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
				
				addNewIntervalSetPanel(mainPanel,intervalSetFilesPanel,constraints,frame);
				frame.pack();
								
            }
        });
		/*******************************************************************************/
		/**********************ADD BUTTON ends******************************************/
		/*******************************************************************************/
		
		constraints.gridx = 0;
        constraints.gridy = 0;     
        intervalSetFilePanel.add(loadIntervalSetFilesLabel, constraints);
        
      
        constraints.gridx = 1;
        constraints.gridy = 0; 
        intervalSetFilePanel.add(addButton, constraints);
		/**************************************************/
		/***********1st IntervalSetFile Panel ends ********/
		/**************************************************/
		

		/**************************************************/
		/***********2nd IntervalSetFile Panel starts ******/
		/**************************************************/
        JPanel otherIntervalSetFilePanel = new JPanel();
        otherIntervalSetFilePanel.setLayout(new GridBagLayout());
		

		JTextField inputFileTextField = new JTextField(30);
		
		/*******************************************************************************/
		/**********************BROWSE BUTTON starts*************************************/
		/*******************************************************************************/
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
            	
            	JFileChooser fc = new JFileChooser();
            	int returnVal;

            	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            	
            	returnVal = fc.showOpenDialog(mainPanel);
            	if( returnVal == JFileChooser.APPROVE_OPTION){
            		
            		File file = fc.getSelectedFile();
            		inputFileTextField.setText(file.getPath() + System.getProperty( "file.separator"));
            		
             	}

            }
        });
		/*******************************************************************************/
		/**********************BROWSE BUTTON ends***************************************/
		/*******************************************************************************/


		
		
		/*******************************************************************************/
		/**********************REMOVE BUTTON starts*************************************/
		/*******************************************************************************/
		JButton removeButton = new JButton("-");
		removeButton.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
				
				intervalSetFilesPanel.remove(otherIntervalSetFilePanel);
            	frame.pack();
							
            }
        });
		/*******************************************************************************/
		/**********************REMOVE BUTTON ends***************************************/
		/*******************************************************************************/
		
		
		constraints.gridx = 0;
        constraints.gridy = 0;     
        otherIntervalSetFilePanel.add(browseButton, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0; 
        otherIntervalSetFilePanel.add(inputFileTextField, constraints);
        
        constraints.gridx = 2;
        constraints.gridy = 0; 
        otherIntervalSetFilePanel.add(removeButton, constraints);
		/**************************************************/
		/***********2nd IntervalSetFile Panel ends ********/
		/**************************************************/

		
	
        constraints.gridx = 0;
        constraints.gridy = 0; 
        intervalSetFilesPanel.add(intervalSetFilePanel, constraints);
      
        constraints.gridx = 0;
        constraints.gridy = 1;     
        intervalSetFilesPanel.add(otherIntervalSetFilePanel,constraints);
        
		// set border for the panel
        intervalSetFilesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ""));
		/**************************************************/
        /**********Interval Set Files Panel ends***********/
        /**************************************************/
        
        
 		
		
        /******************************************/
        /**********Parameter Panel starts**********/
        /******************************************/
		JPanel parameterPanel = new JPanel();
		parameterPanel.setLayout(new GridBagLayout());
				
				
		JLabel dataStructureLabel = new JLabel("Data Structure");
		
		List<String> columns = new ArrayList<String>();
		columns.add(Commons.INDEXED_SEGMENT_TREE_FOREST);
		columns.add(Commons.SEGMENT_TREE);
		
	    String[] options = {"Indexed Segment Tree Forest", "Segment Tree"};
		dataStructureColumn = new JComboBox<String>(options);

		
		//Populate data structure columns
		dataStructureColumn.removeAllItems();
		for(int i=0;i<columns.size();i++){
			dataStructureColumn.addItem(columns.get(i));                		
		}
		
		
		JLabel presetLabel = new JLabel("Preset Value");
		JTextField presetValue = new JTextField(8);
		presetValue.setText("1000000");

		dataStructureColumn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	//Get selected item
            	if (dataStructureColumn.getSelectedItem().toString()==Commons.INDEXED_SEGMENT_TREE_FOREST) {
            		presetValue.setEnabled(true);
            	}else if (dataStructureColumn.getSelectedItem().toString()==Commons.SEGMENT_TREE) {
            		presetValue.setEnabled(false);
            	}
            	
            }
        });
		
			
	    //Data Structure Label
	    constraints.gridx = 0;
	    constraints.gridy = 0; 
	    constraints.gridwidth =1;
	    parameterPanel.add(dataStructureLabel,constraints);
	    
	    //dataStructureColumn
	    constraints.gridx = 1;
	    constraints.gridy = 0; 
	    constraints.gridwidth =1;
	    parameterPanel.add(dataStructureColumn,constraints);

	   
		//Preset Label
		constraints.gridx = 2;
	    constraints.gridy = 0; 
	    constraints.gridwidth =1;
	    parameterPanel.add(presetLabel,constraints);
		
		//Preset Value
		constraints.gridx = 3;
	    constraints.gridy = 0; 
	    constraints.gridwidth =1;
	    parameterPanel.add(presetValue,constraints);

		
		// set border for the panel
	    parameterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
        /******************************************/
        /**********Parameter Panel ends************/
        /******************************************/
		


		
	    /******************************************/
        /**********Run Panel starts****************/
        /******************************************/
		JPanel runPanel =  new JPanel();
		runPanel.setLayout(new GridBagLayout());
		
		// Button submit
        JButton runButton = new JButton("Find Jointly Overlapping Intervals");
        
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	/*******************************************************/
            	/*******************************************************/
            	/*******************************************************/
            	//Freeze all except log area
                disableAllExceptLog(mainPanel);
                frame.pack();
                frame.setVisible(true); 
            	/*******************************************************/
            	/*******************************************************/
            	/*******************************************************/
               
            	int preset = Integer.parseInt(presetValue.getText());
            	
            	List<String> intervalSetFiles =  new ArrayList<String>();
            	
            	Component[] components = intervalSetFilesPanel.getComponents();
                for (Component comp : components) {
                    // Cast comp to JComboBox / JTextField to get the values
                	if (comp instanceof JPanel) {
                		
                		Component[] innerComponents = ((JPanel)comp).getComponents();
                        
                		for (Component innerComp : innerComponents) {
                			 if (innerComp instanceof JTextField) {
                                 JTextField textField = (JTextField) innerComp;
                                 intervalSetFiles.add(textField.getText());
                                 System.out.println(textField.getText());
                             }
                		}//End of JTextField
                            
                	}
                   
                }//End of Panels
                
                String[] intervalSetFilesArray = new String[intervalSetFiles.size()];
                intervalSetFiles.toArray(intervalSetFilesArray);
               
                //ParallelOneSegmentTreeForEachIntervalSet.findCommonOverlapsInParallel(null, preset,1, intervalSetFiles.size(), intervalSetFilesArray, "/home/burcak/Developer/Java/JOA/test.txt");            	                             

                //Check whether interval set files are entered
                boolean valid = checkIntervalSetFileNamesStringArray(intervalSetFilesArray);
               
                //Internally set
                int numberofPercent = 1;
                
                try {
                	
                	if (valid) {
                		
                		if (dataStructureColumn.getSelectedItem().toString().equalsIgnoreCase(Commons.INDEXED_SEGMENT_TREE_FOREST)) {

        					JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
        							preset,
        							IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
        							1,
        							intervalSetFiles.size(),
        							intervalSetFilesArray,
        							numberofPercent,
        							SearchMethod.NOT_SET,
        							OutputType.ONLY_RESULTING_INTERVAL);

                        } else if (dataStructureColumn.getSelectedItem().toString().equalsIgnoreCase(Commons.SEGMENT_TREE)) {
                        	
                        	JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
                					1,
                					intervalSetFiles.size(),
        							intervalSetFilesArray,
                					OutputType.ONLY_RESULTING_INTERVAL);

                        }
                		
                	}
                	
                						
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
                
                
            	/*******************************************************/
            	/*******************************************************/
            	/*******************************************************/
                //When run is finished enable all
                enableAll(mainPanel);
                frame.pack();
                frame.setVisible(true);
            	/*******************************************************/
            	/*******************************************************/
            	/*******************************************************/
                
             }
        });
        
		constraints.gridx = 0;
	    constraints.gridy = 0; 
	    runPanel.add(runButton,constraints);		
	    /******************************************/
        /**********Run Panel ends******************/
        /******************************************/
	    
	    
	   
	    /******************************************/
        /******Add Panels to Main Panel starts*****/
        /******************************************/
	    
	    //new starts
	    JScrollPane intervalSetFilesPanelScrollPane = new JScrollPane(intervalSetFilesPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    //intervalSetFilesPanelScrollPane.getViewport().setMinimumSize(new Dimension(80, 120));
	    intervalSetFilesPanelScrollPane.getViewport().setPreferredSize(new Dimension(80, 120)); 
	    //new ends
	    
	    
		constraints.gridx = 0;
        constraints.gridy = 0; 
        //formerly it was as below
        mainPanel.add(intervalSetFilesPanel, constraints);
        
        //To put intervalSetFilesPanel in a intervalSetFilesPanelScrollPane
        //mainPanel.add(intervalSetFilesPanelScrollPane, constraints);
		
		constraints.gridx = 0;
        constraints.gridy = 1; 
		mainPanel.add(parameterPanel, constraints);
		
         
        constraints.gridx = 0;
        constraints.gridy = 2; 
        constraints.gridwidth = 2;
		mainPanel.add(runPanel, constraints);
		
	    /******************************************/
        /**********Log Area starts*****************/
        /******************************************/
		 JScrollPane logAreaScrollPane = new JScrollPane(logArea);
		 logAreaScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		 //logAreaScrollPane.setPreferredSize( new Dimension( 250, 250));
		 logArea.setEditable(false);
		 logArea.setLineWrap(true);
		 logArea.setWrapStyleWord(true);
		 
		 JPanel logPanel =  new JPanel();
		 logPanel.setLayout(new GridBagLayout());
		 logPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "JOA Log"));
		 //logPanel.setMaximumSize(new Dimension(250,250));
			
		 constraints.gridx = 0;
	     constraints.gridy = 0; 
	     constraints.gridwidth = 3;
		 logPanel.add(logAreaScrollPane,constraints);
	        
		 constraints.gridx = 0;
	     constraints.gridy = 3; 
	     //constraints.gridwidth = 3;
		 mainPanel.add(logPanel,constraints);
		 /******************************************/
	     /**********Log Area ends*****************/
     	 /******************************************/
			
		
		// set border for the panel
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Joint Overlap Analysis"));
	    /******************************************/
        /******Add Panels to Main Panel ends********/
        /******************************************/
       
	    //I have added two panels two my frame.
		//How to give upper panel a shorter height and lower panel a higher height?
		
		constraints.gridx = 0;
        constraints.gridy = 0;     
        constraints.weighty=1;
        frame.add(mainPanel,constraints);		
		
       constraints.gridx = 0;
       constraints.gridy = 1;
       constraints.gridheight=4;
       constraints.weighty=10;
       constraints.fill= GridBagConstraints.VERTICAL;     
       
       
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true); 
        /**************************************************/
        /**********GUI ends********************************/
        /**************************************************/

	}
	
	public static void enableAll(JPanel mainPanel) {
		
		Component[] components = mainPanel.getComponents();
    	//MainPanel contains 	intervalSetFilesPanel contains intervalSetFilePanel
    	//						parameterPanel
    	//						runPanel
    	//						logPanel
    	
        for (Component comp : components) {
            
        	if (comp instanceof JPanel) {
        		
        		Component[] innerComponents = ((JPanel)comp).getComponents();
                
        		for (Component innerComp : innerComponents) {

        			if (innerComp instanceof JTextField) {
                         JTextField textField = (JTextField) innerComp;
                         textField.setEnabled(true);
                    } else if (innerComp instanceof JButton) {
                    	JButton button = (JButton) innerComp;
                    	button.setEnabled(true);
                    } else if (innerComp instanceof JPanel) {
                    	
                    	JPanel panel = (JPanel) innerComp;
                    	Component[] panelInnerComponents = ((JPanel)panel).getComponents();
                        
                    	for(Component panelInnerComponent : panelInnerComponents) {
                    		
                    		if (panelInnerComponent instanceof JTextField) {
                                JTextField textField = (JTextField) panelInnerComponent;
                                textField.setEnabled(true);
                           } else if (panelInnerComponent instanceof JButton) {
                           		JButton button = (JButton) panelInnerComponent;
                           		button.setEnabled(true);
                           }
                    		
                    	}//End of intervalSetFilePanels 
                    	
                    }else if (innerComp instanceof JComboBox<?>) {
                    	JComboBox<?> combobox= (JComboBox<?>) innerComp;
                    	combobox.setEnabled(true);
                    }
        			
        		}//End of for components within JPanel
                    
        	}//End of JPanels
           
        }//End of Panels
		
	}
	
	public static void disableAllExceptLog(JPanel mainPanel) {
		
       	
    	Component[] components = mainPanel.getComponents();
    	//MainPanel contains 	intervalSetFilesPanel contains intervalSetFilePanel
    	//						parameterPanel
    	//						runPanel
    	//						logPanel
    	
        for (Component comp : components) {
            
        	if (comp instanceof JPanel) {
        		
        		Component[] innerComponents = ((JPanel)comp).getComponents();
                
        		for (Component innerComp : innerComponents) {

        			if (innerComp instanceof JTextField) {
                         JTextField textField = (JTextField) innerComp;
                         textField.setEnabled(false);
                    } else if (innerComp instanceof JButton) {
                    	JButton button = (JButton) innerComp;
                    	button.setEnabled(false);
                    } else if (innerComp instanceof JPanel) {
                    	
                    	JPanel panel = (JPanel) innerComp;
                    	Component[] panelInnerComponents = ((JPanel)panel).getComponents();
                        
                    	for(Component panelInnerComponent : panelInnerComponents) {
                    		
                    		if (panelInnerComponent instanceof JTextField) {
                                JTextField textField = (JTextField) panelInnerComponent;
                                textField.setEnabled(false);
                           } else if (panelInnerComponent instanceof JButton) {
                           		JButton button = (JButton) panelInnerComponent;
                           		button.setEnabled(false);
                           }
                    		
                    	}//End of intervalSetFilePanels 
                    	
                    }else if (innerComp instanceof JComboBox<?>) {
                    	JComboBox<?> combobox= (JComboBox<?>) innerComp;
                    	combobox.setEnabled(false);
                    }
        			
        		}//End of for components within JPanel
                    
        	}//End of JPanels
           
        }//End of Panels
        

		
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
		
		//If there are command line arguments bypass GUI (-c -f f1 f1 ... fn -preset 100000)
		JointOverlapAnalysisGUI joa = new JointOverlapAnalysisGUI();
        JCommander.newBuilder().addObject(joa).build().parse(args);
        
        String[] filesArray = joa.filenames.toArray(new String[joa.filenames.size()]);
        
        if (joa.preset>0 && checkIntervalSetFileNamesStringArray(filesArray)) {
        	
        	System.out.println(joa.preset);
        	System.out.println(joa.percentage);
        	System.out.println(joa.numberofRepetitions);
        	
        	System.out.println(joa.filenames);
        	System.out.println(joa.treeType);
        	System.out.println(joa.outputType);
        	        	
        	DataStructureType treeType = DataStructureType.convertStringtoEnum(joa.treeType);
        	OutputType outputType = OutputType.convertStringtoEnum(joa.outputType);
        	
			try {
				
				if (treeType.isINDEXED_SEGMENT_TREE_FOREST()) {
					
					
					JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
							joa.preset,
							IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
							joa.numberofRepetitions,
							joa.filenames.size(),
							filesArray,
							joa.percentage,
							SearchMethod.NOT_SET,
							outputType);
					
				} else if (treeType.isINDEXED_SEGMENT_TREE_FOREST_USING_LAST_OVERLAPPING_LINKED_NODE()) {
					
					JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_IndexedSegmentTreeForest(
							joa.preset,
							IndexingLevelDecisionMode.DURING_SEGMENT_TREE_CONSTRUCTION,
							joa.numberofRepetitions,
							joa.filenames.size(),
							filesArray,
							joa.percentage,
							SearchMethod.USING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED,
							outputType);

					
				}else if (treeType.isSEGMENT_TREE()) {
					
					JointOverlapAnalysis.constructParallel_searchParallel_FileBased_ChromBased_SegmentTree(
        					joa.numberofRepetitions,
        					joa.filenames.size(),
        					filesArray,
        					outputType);

					
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }else {
    		prepareGUI();
        	
        }
			
	}
	
	public static boolean checkIntervalSetFileNamesStringArray(String[] intervalSetFilesArray) {
		
		if (intervalSetFilesArray == null ) {
			appendNewTextToLogArea("Interval Set File is null");
	        return false;
	    }
		
		if (intervalSetFilesArray.length == 0 ) {
	        appendNewTextToLogArea("Interval Set Files is empty, meaning it has no element");
	        return false; 
	    }
		
	    for ( String s : intervalSetFilesArray ) {
	        if (s == null) {
		        appendNewTextToLogArea("Interval Set Files contain null element");
	            return false;
	        }
	        if (s.length() == 0) {
		        appendNewTextToLogArea("Interval Set Files contain empty string");
	            return false; 
	        }
	        // or
	        if (s.isEmpty()) {
		        appendNewTextToLogArea("Interval Set Files contain empty string");
	            return false;
	        }
	    }

	    return true;
		
		
	}
            

}
