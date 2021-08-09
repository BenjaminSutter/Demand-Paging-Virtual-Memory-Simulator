/*
 * File: Final.java
 * Author: Ben Sutter
 * Date: April 18th, 2021
 * Purpose: Read a reference string from user input or generate one based on length given by user.
 * Then perform selected page replacement algorithms (FIFO, OPT, LRU, and LFU) on the current referenc string.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Final {

    final int MIN_PHYSICAL_FRAMES = 1;
    final int MAX_PHYSICAL_FRAMES = 8;
    final int MIN_VIRTUAL_FRAMES = 0;
    final int MAX_VIRTUAL_FRAMES = 9;
    final int PHYSICAL_FRAMES;//Determines how many physical frames are used

    ArrayList<Integer> referenceString = new ArrayList<>();
    //Create scanner to read user input. Used in multiple methods so declare here
    Scanner scan = new Scanner(System.in);

    //Determine number of physical frames using a constructor
    public Final(int frames) {
        PHYSICAL_FRAMES = frames;
    }

    //Stores the menu options in a method for easy access.
    public void displayMenu() {
        System.out.println("\nPlease select an option from the menu below"
                + "\n0. Set reference string to the one used in Homework 5"
                + "\n1. Read reference string"
                + "\n2. Generate reference string"
                + "\n3. Display current reference string"
                + "\n4. Simulate FIFO"
                + "\n5. Simulate OPT"
                + "\n6. Simulate LRU"
                + "\n7. Simulate LFU"
                + "\nQ. Quit program");
    }

    /*Displays the arraylist passed as a parameter as its contents spaced out by one space.
    If the index passed as a parameter matches i, use it to signify that it is the current frame*/
    public void listToString(ArrayList<Integer> list, int index) {
        for (int i = 0; i < list.size(); i++) {
            //If index is not negative (used to not print arrows) use it to signify current frame
            if (index >= 0 && index == i) {
                System.out.print("->" + list.get(i) + "<- ");
            } else {
                System.out.print(list.get(i) + " ");
            }

        }
        System.out.println("");//Extra space after
    }

    public void initializeMenu() {

        displayMenu(); //Display menu choices
        String userInput = scan.nextLine();//Grab user input
        //While user input isn't equal to Q (quit) then loop
        while (!userInput.trim().equalsIgnoreCase("q")) {

            switch (userInput) {

                case "0"://This is used to make it easier to use the reference string from homework 5
                    referenceString = readReferenceString("2 4 5 6 1 5 	3 4 5 2 3 6 5 3 4 7 3 5 6");
                    break;

                case "1"://Read reference string
                    System.out.println("\nPlease enter the reference string as integers (0-9) seperated by spaces");
                    referenceString = readReferenceString(scan.nextLine());
                    break;

                case "2"://Generate reference string
                    System.out.println("\nPlease enter the length you would like the reference string to be");
                    while (true) {
                        try {
                            referenceString = generateReferenceString(Integer.parseInt(scan.nextLine().trim()));
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("\nInvalid selection for length of string, please try again");
                        }
                    }
                    break;

                case "3"://Display current reference string
                    if (referenceString.isEmpty()) {
                        System.out.println("\nERROR. The reference string hasn't been initialized");
                    } else {
                        System.out.println("\nThe current reference string is: ");
                        listToString(referenceString, -1);
                    }
                    break;

                case "4"://Simulate FIFO
                    if (referenceString.isEmpty()) {
                        System.out.println("\nERROR. The reference string hasn't been initialized");
                    } else {
                        simulateFIFO();
                    }

                    break;

                case "5"://OPT
                    if (referenceString.isEmpty()) {
                        System.out.println("\nERROR. The reference string hasn't been initialized");
                    } else {
                        simulateOPT();
                    }
                    break;

                case "6"://LRU
                    if (referenceString.isEmpty()) {
                        System.out.println("\nERROR. The reference string hasn't been initialized");
                    } else {
                        simulateLRU();
                    }
                    break;

                case "7"://LFU
                    if (referenceString.isEmpty()) {
                        System.out.println("\nERROR. The reference string hasn't been initialized");
                    } else {
                        simulateLFU();
                    }
                    break;

                default:
                    System.out.println("That was not a vailid selection, please try again");
                    break;
            }//End switch
            displayMenu(); //Display menu choices again for user
            userInput = scan.nextLine();//Grab user input
        }//End while
        System.out.println("Exiting program");
    }

    public ArrayList<Integer> readReferenceString(String string) {
        String s[] = string.split("\\s+");
        ArrayList<Integer> list = new ArrayList<>();
        int parsedInt;
        boolean errorFound = false;
        for (int i = 0; i < s.length; i++) {
            try {
                parsedInt = Integer.parseInt(s[i]);
                if (parsedInt < MIN_VIRTUAL_FRAMES) {
                    //Don't parse frame if it is below the minimum
                    System.out.println('"' + s[i] + '"' + " was rejected (less than minimum value for virtual frame)");
                    errorFound = true;
                } else if (parsedInt > MAX_VIRTUAL_FRAMES) {
                    //Don't parse frame if it is above the maximum
                    System.out.println('"' + s[i] + '"' + " was rejected (greater than maximum value for virtual frame)");
                    errorFound = true;
                } else if (i > 0 && s[i].equals(s[i - 1])) {
                    //Don't parse frame if it is identical to previous
                    System.out.println('"' + s[i] + '"' + " at index " + i + " was rejected (duplicate value to previous frame)");
                    errorFound = true;
                } else {
                    list.add(parsedInt);
                }
            } catch (NumberFormatException e) {
                System.out.println('"' + s[i] + '"' + " was rejected (not an integer)");
                errorFound = true;
            }
        }
        if (errorFound && list.size() > 0) {
            System.out.println("\nErrors found so given string was not fully parseable, here is what was parsed: ");
            listToString(list, -1);//Pass -1 as a parameter so it doesn't print the index arrows
        } else if (errorFound) {
            System.out.println("\nGiven reference string did not contain any valid input so it was not parsed");
        } else {
            System.out.println("Successfully parsed given reference string!");
        }
        return list;
    }

    public ArrayList<Integer> generateReferenceString(int length) {
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomInt = random.nextInt(MAX_VIRTUAL_FRAMES + 1);
            //If i > 0, then check to make sure the incoming virtual frame isn't a duplicate
            if (i > 0) {
                //While it is a duplicate, generate another random integer until it is different
                while (randomInt == list.get(i - 1)) {
                    randomInt = random.nextInt(MAX_VIRTUAL_FRAMES + 1);
                }
            }
            list.add(randomInt);//Add the random integer to the list
        }
        System.out.println("A reference string of size " + length + " was successfully generated!");
        return list;
    }

    public void simulateFIFO() {
        LinkedList<Integer> frames = new LinkedList<Integer>();
        int hits = 0;
        System.out.println("\n**Starting FIFO algorithm**\n");
        for (int i = 0; i < referenceString.size(); i++) {
            listToString(referenceString, i);
            //If the current virtual frame isn't in a physical frame then add it
            if (!frames.contains(referenceString.get(i))) {
                frames.add(referenceString.get(i));//Add current virtual to physical
                //If the vector is larger than the physical frames, remove the first element in the vector
                if (frames.size() > PHYSICAL_FRAMES) {
                    System.out.println("Fault | Victim frame: " + frames.getFirst() + " (first in/oldest)");
                    frames.removeFirst();
                } else {
                    System.out.println("Fault");
                }

            } else {
                //If the current virtual frame is in a physical frame then it is a hit
                System.out.println("Hit");
                hits++;
            }
            System.out.println("Current frames held: " + frames);
            System.out.println("\n*Press enter to continue*");
            scan.nextLine();
        }
        System.out.println("**FIFO Algorithm finished** \nHits: " + hits
                + "\nFaults: " + (referenceString.size() - hits));
    }//End simulateFIFO

    public void simulateOPT() {
        //Use a vector to keep track of the signs so that it is easily reized.
        Vector<Integer> frames = new Vector<>();
        int hits = 0;
        System.out.println("\n**Starting OPT algorithm**\n");
        for (int i = 0; i < referenceString.size(); i++) {
            listToString(referenceString, i);
            //If the current virtual frame isn't in a physical frame then add it
            if (!frames.contains(referenceString.get(i))) {
                //If the current number of frames is equal to the physical frames, see which virtual frame to boot.
                if (frames.size() == PHYSICAL_FRAMES) {
                    //Keeps track of already visited virtual frames so they are only counted once
                    ArrayList<Integer> visited = new ArrayList<>();
                    int furthest = 0;//used to keep track of the index of the furthest element
                    //Iterates through each physical frame to find the next closest virtual frame with the same value
                    for (int j = 0; j < frames.size(); j++) {

                        //Iterates through the all of the virtual frames still to be processed
                        for (int k = i; k < referenceString.size(); k++) {
                            //If there is a match, then see if it has been visited
                            if (frames.get(j) == referenceString.get(k)) {
                                //If it has not been visited, make it visited and mark it as the current furthest virtual frame
                                if (!visited.contains(referenceString.get(k))) {
                                    visited.add(referenceString.get(k));
                                    //System.out.println(referenceString.get(k) +" is at" + k);
                                    if (furthest < k) {
                                        furthest = k;
                                    }
                                }
                            }
                        }
                    }
                    //System.out.println(visited);
                    //If all of the current frames occur later, remove the one that appears the latest (furthest away)
                    if (visited.size() == frames.size()) {
                        frames.remove(referenceString.get(furthest));
                        System.out.println("Fault: victim frame: " + referenceString.get(furthest)
                                + "\nAll frames reoccur: " + referenceString.get(furthest) + " was furthest away (" + (furthest - i) + " places away)");
                    } else {
                        //If not all were visited, determine the most recent one.
                        for (int j = 0; j < frames.size(); j++) {

                            //If the current frame is not visited that means it is the first in so kick it
                            if (!visited.contains(frames.get(j))) {
                                System.out.println("Fault | Victim frame: " + frames.get(j)
                                        + "\nFrame was removed because it does not appear again");
                                frames.remove(frames.get(j));
                                break;
                            }

                        }
                    }
                } else {
                    System.out.println("Fault");
                }
                //Add the current virtual frame to physical frames
                if (!frames.contains(referenceString.get(i))) {
                    frames.add(referenceString.get(i));
                }
            } else {
                //If the virtual frame is already in the phyiscal then it is a hit
                System.out.println("Hit");
                hits++;
            }
            System.out.println("Current frames held: " + frames);
            System.out.println("\n*Press enter to continue*");
            scan.nextLine();
        }
        System.out.println("**OPT Algorithm finished** \nHits: " + hits
                + "\nFaults: " + (referenceString.size() - hits));
    }//End simulateOPT

    //COMMENTS
    public void simulateLRU() {

        Vector<Integer> frames = new Vector<>();
        int hits = 0;
        System.out.println("\n**Starting LRU algorithm**\n");
        for (int i = 0; i < referenceString.size(); i++) {
            listToString(referenceString, i);
            //If the current virtual frame isn't in a physical frame then add it
            if (!frames.contains(referenceString.get(i))) {
                //If the current number of frames is equal to the physical frames, see which virtual frame to boot.
                if (frames.size() == PHYSICAL_FRAMES) {
                    //Create a new arraylist each time to find the recently used
                    LinkedList<Integer> recentlyUsed = new LinkedList<>();
                    for (int j = 0; j < i; j++) {
                        //Only did this to make it easier to follow
                        int recent = referenceString.get(i - (j + 1));//Increment to traverse backwards
                        //If recent is not in recently used list, add it
                        if (!recentlyUsed.contains(recent)) {
                            recentlyUsed.add(recent);
                            //If the size of the list is equal to the amount of physical frames, then break out
                            if (recentlyUsed.size() == PHYSICAL_FRAMES) {
                                break;
                            }
                        }

                    }
                    //This is an object because vector was counting it as index when it was an int
                    Object leastRecentlyUsed = recentlyUsed.removeLast();//The last item in arraylist is the least recntly used
                    frames.remove(leastRecentlyUsed);//Remove the most recently used from frames
                    System.out.println("Fault | Victim frame: " + leastRecentlyUsed + " (least recently used)");
                } else {
                    System.out.println("Fault");
                }
                frames.add(referenceString.get(i));
            } else {
                //If the virtual frame is already in the phyiscal then it is a hit
                System.out.println("Hit");
                hits++;
            }
            System.out.println("Current frames held: " + frames);
            System.out.println("\n*Press enter to continue*");
            scan.nextLine();
        }
        System.out.println("**LRU Algorithm finished** \nHits: " + hits
                + "\nFaults: " + (referenceString.size() - hits));
    }

    public void simulateLFU() {

        Vector<Integer> frames = new Vector<>();
        int hits = 0;
        System.out.println("\n**Starting LFU algorithm**\n");
        //Use a hashtable to keep track of frequency
        Hashtable<Integer, Integer> frequency = new Hashtable<Integer, Integer>();
        for (int i = 0; i < referenceString.size(); i++) {
            listToString(referenceString, i);
            //If the current virtual frame isn't in a physical frame then add it
            if (!frames.contains(referenceString.get(i))) {
                //If the current number of frames is equal to the physical frames, see which virtual frame to boot.
                if (frames.size() == PHYSICAL_FRAMES) {

                    //Keeps track of the lowest frequency count, high value so it will always get lower
                    int leastFrequent = 100;
                    for (int j = 0; j < frames.size(); j++) {
                        //Declare it as a variable once so it is easier to track
                        //When given a virtual frame number, it will return its frequency from the hashtable
                        int freqCount = frequency.get(frames.get(j));
                        if (freqCount < leastFrequent) {
                            leastFrequent = freqCount;
                        }
                    }
                    /*Iterate through all frames. If the frequency of the frame is the least frequent, remove it.
                    By using a vector, and iterating through it. When the current frequency = lowest frequency the first frame in is removed (FIFO)*/
                    for (int j = 0; j < frames.size(); j++) {
                        if (frequency.get(frames.get(j)) == leastFrequent) {
                            //Remove from hashtable
                            frequency.remove(frames.get(j));
                            //Set to object so the frames vector can process it easily
                            Object remove = frames.get(j);
                            System.out.println("Fault | Victim frame: " + frames.get(j));
                            frames.remove(remove);
                            break;
                        }
                    }

                } else {
                    System.out.println("Fault");
                }
                //Add the virtual frame as a key to the hashtable and to the frames vector
                frequency.put(referenceString.get(i), 1);
                frames.add(referenceString.get(i));
            } else {
                frequency.put(referenceString.get(i), frequency.get(referenceString.get(i)) + 1);
                //If the virtual frame is already in the phyiscal then it is a hit
                System.out.println("Hit");
                hits++;
            }
            System.out.print("Current frequencies are: ");
            for (int j = 0; j < frames.size(); j++) {

                System.out.print(frames.get(j) + "(" + frequency.get(frames.get(j)) + ") ");
            }
            System.out.println("\n\n*Press enter to continue*");
            scan.nextLine();
        }
        System.out.println("**LFU Algorithm finished** \nHits: " + hits
                + "\nFaults: " + (referenceString.size() - hits));
    }

    public static void main(String[] args) {

        //Try to parse physical frames from command line arguments
        try {
            int PHYSICAL_FRAMES = Integer.parseInt(args[0]);
            if (PHYSICAL_FRAMES < 1 || PHYSICAL_FRAMES > 8) {
                System.out.println("Physical frames from command line argument was not within the range (1-8)");
            } else {
                //If physical frames are within the range then create a instance of class and initalize menu
                Final test = new Final(PHYSICAL_FRAMES);
                test.initializeMenu();
            }
        } catch (NumberFormatException e) {
            System.out.println("Passed argument was not an integer");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No arguments were passed");
        }

    }

}
