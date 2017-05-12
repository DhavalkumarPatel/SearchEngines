Steps to build and run SearchEngines project:

1) You can find the SearchEngines project inside DPatel_DGanguly_OContractor_CS6200_Project\SourceCode folder.
2) Import the project in your Eclipse.(The Project name is also SearchEngines)
3) The 4 libraries that are required to be imported are placed inside the lib directory of thie project.
4) If there are error seen with the usage of libraries, please have the libraries added in the build path of the project by specifying the path of lib directory.
5) The SearchEngine can be started by running Main.java. Main method is already configured to run the project. Please update the input paths as per below notes.


Notes:

- The folder structure is already created for input and output inside data directory.
- The program can be run on any corpus (Stemmed or Unstemmed) and on any queries (stemmed and unstemmed).
- All paths are configured in FilePaths class.
- Copy the input data into related data\input folder like corpus, queries, relevance judgments and stop list before executing.
- Enable the specific task to run in Main class (All parameters are already configured).
- Output files will be generates in data\output folder.

External libraries used for this project:

1. commons-lang3-3.5 
2. lucene-analyzers-common-4.7.2
3. lucene-core-4.7.2
4. lucene-queryparser-4.7.2

