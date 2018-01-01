============
JOA Tutorial
============

.. JOA includes both graphical user interface (GUI) and command-line interface.
You can run JOA using its graphical user interface (GUI) or command line arguments.

By double clicking the joa.jar you can open JOA's GUI. In this case, GUI will be opened with default amount of memory allocated for JVM which is 256MB.
In order to allocate specific amount of memory for joa.jar and run it through its GUI, one should write the following basic command on Terminal (Linux or Mac OS X) or on Command Prompt (Windows)\*::

	$ java -Xms8G -Xmx8G −jar ~path/to/joa.jar

Note that with this command above, 8GM of memory is allocated for joa.jar. Depending on the number of intervals in the sets and number of interval sets you may need to increase the memory accordingly.

Throughout the guide, we will use *~path/to/joa.jar* to indicate your absolute path to **joa.jar**.

----------------------------
JOA Graphical User Interface
----------------------------

JOA provides joint overlapping intervals of n interval sets.

1)	**Input File Names**: (Mandatory)

	You have to provide input files in bed format. 

2)	**Data Structure Type**: (Optional)

	JOA finds overlapping intervals of n interval sets using Segment Tree and Indexed Segment Tree Forest.

	* *Segment Tree*
	* *Indexed Segment Tree Forest*
	
3)	**Output Type**: (Optional)

	JOA outputs the found overlapping intervals of n interval sets in two options.

	* *Only the resulting interval per each found overlap*
	* *All the overlapping intervals and the resulting interval per each found overlap* 
	

-----------------------------------------------------
JOA Command-Line Interface and Command-Line Arguments
-----------------------------------------------------

In the following table, command-line arguments are specified. 

+-------------------------+---------------+-------------------+----------------------+
| Description             | Parameter     | Optional/Required | Default Parameter    |
+=========================+===============+===================+======================+
| Input Files             | `-f`_         |  Required         | None ("path/to/file")|
+-------------------------+---------------+-------------------+----------------------+
| Data Structure          | `-t`_         |  Optional         | `st`_                |
|                         |               |                   +----------------------+
|                         |               |                   | `istf`_              |
+-------------------------+---------------+-------------------+----------------------+
| Output                  | `-o`_         |  Optional         | `all`_               |
|                         |               |                   +----------------------+
|                         |               |                   | `only`_              |
+-------------------------+---------------+-------------------+----------------------+


------------------------------------
Command-Line Parameters Descriptions
------------------------------------

There are several parameters that are either required or optional to make JOA run in Terminal or in Command Prompt. 
The order of parameters is not fixed. One may set the parameters in any order. 
You can see the preconditions of a parameter as shown in `GLANET Command-Line Interface and Command-Line Arguments`_


-f
^^

**Required** Absolute paths to input files must be specified just after :option:`-f` option.


-t
^^

**Optional** This option specifies the data structure type used in finding the overlapping intervals. If you do not set anything, :option:`-"ST"` segment tree is set as default.

-o
^^

**Optional** This option specifies the output type. If you do not set anything, :option:`-"only"` is set as default.

----------------------------
JOA Command-Line Sample Runs
----------------------------

 Example sample run that outputs to stdout::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed

 Example sample run that outputs to a file with redirection operator::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed /path/to/input3.bed /path/to/input4.bed /path/to/input5.bed  > /path/to/joa_output_5files.txt

 Example sample run Using Indexed Segment Tree Forest::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed /path/to/input3.bed /path/to/input4.bed /path/to/input5.bed  -t "istf" > /path/to/joa_output_5files.txt


 Example sample run for very large BED files::

	$ java Xms16G -Xmx16G -jar /path/to/joa.jar -f  /path/to/bigInput1.bed  /path/to/bigInput2.bed  -t "istf" > /path/to/joa_output_big2files.txt

