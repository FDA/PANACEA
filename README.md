# PANACEA
============
= PANACEA
============
= Pattern-based and Advanced
= Network Analyzer for Clinical
= Evaluation and Assessment
============

PANACEA is a visualization and analysis tool for network data.  It was originally developed at the Center for Biologics Evaluation and Research to assist in safety review of large numbers of spontaneous adverse event reports regarding vaccines and other biologic products.  PANACEA supports the creation of networks from data sets based around 'documents' which can contain two types of 'elements'.  For the FDA's purposes, these documents were adverse event reports, and the elements were the administered vaccines and the reported symptoms; however, many other types of data can be represented in this manner.

PANACEA is capable of creating multiple types of networks from the provided data.  In a Report Network, the documents from the data set are visualized as nodes, and two documents are connected if they share common elements.  In an Element Network, the roles of nodes and edges are reversed, with nodes representing the elements that were found in documents.  Two elements are then connected by an edge if they appeared together in the same document.  These visualizations allow users to explore the interconnections between documents and search for interesting patterns or relationships.

PANACEA also includes a suite of functionalities for analyzing networks, including network reduction methods, community detection algorithms, and nodal metric calculations, as well as a robust set of network layout, visualization, and manipulation options.

============
= Getting Started
============

To run PANACEA, you'll need the contents of the 'dist' folder.  This includes the main executable for PANACEA (a JAR file) as well as some sample data and the supporting libraries.  Once the 'dist' folder is on your hard drive, simply run the batch file 'PANACEA.bat' to start the program.

============
= System Requirements
============

- Windows operating system (tested on Windows 7 64-bit)
- RAM: 4.0GB. Additional memory can support larger networks.
- Disk Space: Approximately 40 MB.
- Java SE 6 or higher
- OpenGL version 1.5 or higher

============
= FAQ / Troubleshooting
============

* How do I load a network in PANACEA?
	There are several ways to create a new network in PANACEA, depending on the format of the input data files.  All of these methods can be found in the 'File->New Project' menu, and sample data files are provided for each.  For example, the two files 'RV_Vax.txt' and 'RV_Sym.txt' should be used with the 'Create Full PANACEA Network' command.  Please view the PANACEA help system ('Help->Documentation') for more details, and examine the sample files to see the data formats supported by PANACEA.

* I tried running the 'PANACEA.bat' file, but the program doesn't start.
	Please make sure you have Java installed and that the java command is properly aliased.  You can test this by opening a command prompt and typing 'java -version'.  If you do not see information about your current Java version (which must be 1.6 or higher), try reinstalling Java.  If you find Java is already configured correctly, you can also try to run the 'PANACEA.jar' file directly.  This will launch the program without the larger initial memory allocation that the batch file provides, which may be too high for machines without much free RAM.

* Is PANACEA available for other operating systems?
	PANACEA is currently Windows only and the distributed version will not run on any other operating system.  This mainly stems from the inclusion of the Windows version of the Java OpenGL (JOGL) library.  If support for another operating system is vital for your use, you can try recompiling PANACEA to use a different JOGL version, which could potentially allow it to run.

