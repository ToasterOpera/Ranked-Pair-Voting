import java.io.BufferedReader
import java.io.File

fun main() {
    val choicesFile = "C:\\Users\\johnc\\IdeaProjects\\Ranked Pair Voting\\src\\choices.txt"
    val responsesFile = "C:\\Users\\johnc\\IdeaProjects\\Ranked Pair Voting\\src\\responses.txt"
    val choices = readChoices(choicesFile)
    var wins = arrayOf<Array<Int>>()
    for (i in 0..choices.size) {
        var row = arrayOf<Int>()
        for (j in 0..choices.size) {
            row += 0
        }
        wins += row
    }
    readResponses(responsesFile, choices, wins)
    var pairs = collectPairs(wins)
    pairs = lockPairs(pairs, choices.size)
    val winner = selectWinner(pairs)
    println(choices[winner])
}

////PASS EVERYTHING BY REFERENCE OR ELSE
////WELL... I GUESS YOU CAN'T SO GOOD LUCK
////Hopefully since arrays are objects everything will be fine...

//Reads Choices from Choices file.
fun readChoices(fileName: String): Array<String> {
    val reader = BufferedReader(File(fileName).inputStream().reader())
    var choices = arrayOf<String>()

    var line: String = reader.readLine()

    //Get the options from the first line, which is supposed to be the only line in this file.
    while (line.isNotEmpty()) {
        var nextWord: String
        val nextSpace = line.indexOf(' ')
        if (nextSpace == -1) {
            nextWord = line
            line = ""
        } else {
            nextWord = line.substring(0, nextSpace)
            line = line.substring(nextSpace + 1)
        }
        choices += nextWord
    }
    return choices
}

//Reads Responses from the Responses file and adds the results to the wins array.
fun readResponses(fileName: String, choices: Array<String>, wins: Array<Array<Int>>) {
    File(fileName).forEachLine {
        var response = arrayOf<Array<Int>>()
        var line = it

        //Each line is a response. Each word is a choice.
        //This code goes through the line word by word, deleting words behind it.
        while (line.isNotEmpty()) {
            var nextWord: String
            val nextSpace = line.indexOf(' ')
            if (nextSpace == -1) {
                nextWord = line
                line = ""
            } else {
                nextWord = line.substring(0, nextSpace)
                line = line.substring(nextSpace + 1)
            }
            var thisRow = arrayOf<Int>()

            //Each word may contain multiple choices separated by a slash.
            //These will be treated equally.
            //Go through the current word part by part, deleting parts as they are converted into integers.
            //Usually this loop will be very short, often even running only once,
            //but it has the potential to be as long as the responder makes it.
            //Any response that doesn't match an established choice is ignored.
            while (nextWord.isNotEmpty()) {
                var nextPart: String
                val also = nextWord.indexOf('/')
                if (also == -1) {
                    nextPart = nextWord
                    nextWord = ""
                } else {
                    nextPart = nextWord.substring(0, also)
                    nextWord = nextWord.substring(also + 1)
                }
                for (i in choices.indices) {
                    if (choices[i] == nextPart) {
                        thisRow += i
                    }
                }
            }
            //Once the word has been turned into an array of integers, usually of
            //length 1, that array is added to the response being prepared.
            response += thisRow

        }
        //Once the entire line has been parsed, the completed response is sent to the addWins function
        //to be counted and added to the wins array
        addWins(response, wins)
    }
}

//Takes responses in integer array form and updates the wins array accordingly.
fun addWins(resp: Array<Array<Int>>, wins: Array<Array<Int>>) {
    for (i in resp.indices) {
        for (j in (i + 1) until resp.size) {
            for (k in resp[i].indices) {
                for (z in resp[j].indices) {
                    wins[resp[i][k]][resp[j][z]]++
                }
            }
        }
    }
}

//Goes through the wins array and creates a list of majority pairs.
fun collectPairs(wins: Array<Array<Int>>): Array<Array<Int>> {
    var pairs = arrayOf<Array<Int>>()
    //Go through the entire wins array.
    for (i in wins.indices) {
        for (j in wins[i].indices) {
            //If wins[i][j] represents a majority of opinions, create a pair for it and sort it into the pairs array.
            if (wins[i][j] > wins[j][i]) {
                pairs = sortInto(i, j, pairs, wins)
            }
        }
    }
    return pairs
}

//Sort the pair into the pairs array in order based on priority
fun sortInto(win: Int, lose: Int, pairs: Array<Array<Int>>, wins: Array<Array<Int>>): Array<Array<Int>> {
    val majority = wins[win][lose] - wins[lose][win]
    for (k in pairs.indices) {
        if (majority > pairs[k][0]) {
            return insert(arrayOf(majority, win, lose), pairs, k)
        }
        else if (majority == pairs[k][0]) {
            if (breakTie(win, lose, pairs[k][1], pairs[k][2])) {
                return insert(arrayOf(majority, win, lose), pairs, k)
            }
        }
    }
    return pairs + arrayOf(majority, win, lose)
}

//Decides which pair has a higher priority if they each beat their opponent by the same amount.
//TODO: write tiebreakers.
fun breakTie(challengeWin: Int, challengeLose: Int, defendingWin: Int, defendingLose: Int): Boolean {
    return false
}

//Inserts an array at the given index of a 2D array and returns the new array
fun insert(element: Array<Int>, array: Array<Array<Int>>, index: Int): Array<Array<Int>> {
    var newArray = arrayOf<Array<Int>>()
    for (i in 0 until index)
        newArray += array[i]
    newArray += element
    for (i in index until array.size)
        newArray += array[i]
    return newArray
}

//Locks in pairs in order of priority, skipping pairs that will create loops.
fun lockPairs(pairs: Array<Array<Int>>, numC: Int): Array<Array<Int>> {
    var locked = arrayOf<Array<Int>>()
    for (i in pairs.indices) {
        locked = lock(pairs[i][1], pairs[i][2], locked, numC)
    }
    return locked
}

//Locks in the next pair after checking if it is valid. Returns the new array.
fun lock(w: Int, l: Int, locked: Array<Array<Int>>, numC: Int): Array<Array<Int>> {
    var newLocked = locked
    var ta = arrayOf<Array<Int>>()
    for (i in 0 until numC) {
        var row = arrayOf<Int>()
        for (j in 0 until numC) {
            row += 0
        }
        ta += row
    }
    if (loopLink(w, l, ta, locked)) {
        newLocked += arrayOf(w, l)
    }
    return newLocked
}

//Recursively checks to see if locking in the next pair will cause a loop.
fun loopLink(w: Int, l: Int, ta: Array<Array<Int>>, locked: Array<Array<Int>>): Boolean {
    //If the given pair has already been checked for, return false.
    if (ta[w][l] > 0) {
        return false
    }

    //Indicate to future loopLinks that this pair has already been checked for.
    ta[w][l]++

    //Check to see if the minority in this pair is the majority in a pair that has already been locked in.
    for (i in locked.indices) {
        if (locked[i][0] == l) {
            //If another loopLink returns false, collapse the recursion.
            if (!loopLink(l, locked[i][1], ta, locked)) {
                return false
            }
        }
    }
    return true
}

//Determine the winner and return it in integer form.
fun selectWinner(pairs: Array<Array<Int>>): Int {
    var winner = pairs[0][0]
    //Go through each pair.
    for (i in pairs.indices) {
        //If the current pair indicates that a candidate beat the current winner, that candidate is the new winner.
        if (winner == pairs[i][1]) {
            winner = pairs[i][0]
        }
    }
    return winner
}