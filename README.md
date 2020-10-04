# Ranked Pair Voting
A program to process ballots for the Ranked Pair voting system (or Tideman method) for group decision making. 

The program reads from two files: a file containing a list of candidates on one line separated by a single space, and a file containing ballots, with each ballot on one line and consisting of choices ordered from most preferred to least preferred. Each choice should be separated by a single space. To indicate that two candidates are both equally preferred, write them both separated only by a '/' character and no space, then continue the ballot normally. Not every candidate must be included on every ballot. Any candidate not included on your ballot will be considered less preferred than the least preferred candidate on your ballot and equally preferred to other unlisted candidates.

Can be run by any Kotlin compiler. I use Intellij Idea. Simply open the project in Intellij Idea and open "main.kt" in the "src" folder. You will have to change the filepaths in the main method (choicesFile and responsesFile) to the filepaths of the files you are using. choicesFile should lead to a file with the list of candidates and responsesFile should lead to a file containing ballots as described above.

https://en.wikipedia.org/wiki/Ranked_pairs

Fun fact: I wrote the first commit of this program in two days and only had to fix two typos to get it to run successfully on my third try.
