package com.gilded.rose.backend;

import com.gilded.rose.controllers.InnController;
import com.gilded.rose.model.Item;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Inventory {

    private static List<Item> inventory = null;
    /*
        Will contain the timestamp when get request are called and based on number,
        it would decide if the price must be surged or not
     */
    private static Queue<LocalDateTime> surgeCounterQueue = new LinkedList<>();
    /*
     variable to check if the given data is already surged or not so that
     we can provide the data unless it is required to change.
     */
    private static boolean isSurged = false;

    /*
        Defaults to Data.csv unless set otherwise.
     */
    private static String dataFilePath = "/Data.csv";

    protected Inventory() {
    }

    public static List<Item> getInventory() {
        final LocalDateTime now = LocalDateTime.now();
        surgeCounterQueue.add(now);
        removeOlderGetCalls(now);
        /*
            If get call are more than 10 and price are not surged, then surge the price of the inventory
         */
        if (surgeCounterQueue.size() > 10 && !isSurged) {
            isSurged = true;
            inventory = readDataFromFile(true);
        }
        /*
            If the get calls have reduced in last hr to be less than or equal to 10 & prices were surged,
            then provide the unsurged/ original prices.
         */
        if (inventory == null || (surgeCounterQueue.size() <= 10 && isSurged)) {
            isSurged = false;
            inventory = readDataFromFile(false);
        }
        return inventory;
    }

    public static Item checkInInventory(final String itemName) {
        // If someone buys before a get Request.
        if(inventory == null) {
            inventory = readDataFromFile(false);
        }
        for(Item item: inventory) {
            if(item.getName().equalsIgnoreCase(itemName))
                return item;
        }
        return null;
    }

    /**
     * Removed get call timestamp older than an Hour or when there are multiple frequent calls.
     * This is to ensure that the queue doesn't grow too big.
     *
     * @param now
     */
    private static void removeOlderGetCalls(final LocalDateTime now) {
        while (Duration.between(surgeCounterQueue.peek(), now).toHours() > 1 || surgeCounterQueue.size() > 15) {
            surgeCounterQueue.remove();
        }
    }

    /**
     * Reading the inventory from Data.csv file under resources and if needs to be surged,
     * it would increment the price by 10%
     *
     * @param surge
     * @return
     */
    private static List<Item> readDataFromFile(final boolean surge) {
        try {
            final InputStream ipStream = InnController.class.getResourceAsStream(dataFilePath);
            final Reader inputReader = new InputStreamReader(ipStream);
            final CSVReader csvReader = new CSVReader(inputReader);
            final List<Item> inventory = Lists.newArrayList();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                int price;
                if (surge) {
                    price = Integer.parseInt(line[2]) * 110 / 100;
                } else {
                    price = Integer.parseInt(line[2]);
                }
                final Item item = new Item(line[0], line[1], price);
                inventory.add(item);
            }
            return inventory;
        } catch (final IOException e) {
            //Would log the error
            return Lists.newArrayList();
        }
    }

    public static void setDataFilePath(String dataFilePath) {
        Inventory.dataFilePath = dataFilePath;
    }
}
