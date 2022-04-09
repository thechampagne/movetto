/*
 * Copyright (c) 2022 XXIV
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.groovy.json.internal.LazyMap

final def MOVIES_FILE = "database/movies.json"
final def SERIES_FILE = "database/series.json"

if (!indexExists(args, 0)) {
    println("Flags:")
    println("       --movie")
    println("       --series")
    System.exit(2)
}

if (args[0] == "--movie") {
    try {
        File file = new File(MOVIES_FILE)
        print("Movie name: ")
        def name = System.console().readLine();
        if (name.trim().empty) {
            print("Movie name is required")
            System.exit(2)
        }
        print("Description: ")
        def description = System.console().readLine();
        print("Rating: ")
        def rating= System.console().readLine().toDouble()
        if (rating == Math.floor(rating)) {
            rating = (int) rating;
        }
        if (rating > 10) {
            print("you can't rate more than 10")
            System.exit(2)
        }
        if (String.valueOf(rating).length() > 3) {
            rating = String.valueOf(rating).substring(0,3).toDouble()
        }
        def map = [
                id: generateId(file),
                name: name.trim(),
                description: description.trim().empty ? null : description.trim(),
                rating: rating
        ]
        writeJson(file, map)
    } catch (Exception ex) {
        println("Something went wrong: $ex.message")
        System.exit(2)
    }
} else if (args[0] == "--series") {
    try {
        File file = new File(SERIES_FILE)
        print("Series name: ")
        def name = System.console().readLine();
        if (name.trim().empty) {
            print("Series name is required")
            System.exit(2)
        }
        print("Description: ")
        def description = System.console().readLine();
        print("Rating: ")
        def rating= System.console().readLine().toDouble()
        print("finished: (true) ")
        def finished= System.console().readLine()
        if (finished.trim().empty || finished.equalsIgnoreCase("true") || finished.equalsIgnoreCase("yes")) {
            finished = true
        } else {
            finished = false
        }
        if (rating == Math.floor(rating)) {
            rating = (int) rating;
        }
        if (rating > 10) {
            print("you can't rate more than 10")
            System.exit(2)
        }
        if (String.valueOf(rating).length() > 3) {
            rating = String.valueOf(rating).substring(0,3).toDouble()
        }
        def map = [
                id: generateId(file),
                name: name.trim(),
                description: description.trim().empty ? null : description.trim(),
                rating: rating,
                finished: finished
        ]
        writeJson(file, map)
    } catch (Exception ex) {
        println("Something went wrong: $ex.message")
        System.exit(2)
    }
} else {
    println("Flags:")
    println("       --movie")
    println("       --series")
}


static def indexExists(String[] list, int index) {
    return index >= 0 && index < list.size()
}


static def generateId(file) {
    def json = new JsonSlurper().parseText(file.text)
    def list = []
    json.each { LazyMap keys ->
        if (keys.get("id") == null) {
            return
        }
        list.add(keys.get("id"))
    }
    if (!list.empty)
        list.max() + 1
    else
        1
}

static def writeJson(file, map) {
    def json = new JsonSlurper().parseText(file.text)
    def list = []
    json.each {
        list << it
    }
    list << map
    file.write(JsonOutput.prettyPrint(JsonOutput.toJson(list)))
}