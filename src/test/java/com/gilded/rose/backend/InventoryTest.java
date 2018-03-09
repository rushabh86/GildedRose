package com.gilded.rose.backend;

import com.gilded.rose.backend.Inventory;
import com.gilded.rose.model.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class InventoryTest {

    @Before
    public void setup() {
        Inventory.setDataFilePath("/TestData.csv");
    }

    @Test
    public void mustReturnInventory() {
        final List<Item> inventory = Inventory.getInventory();
        Assert.assertNotNull(inventory);
        Assert.assertEquals(2, inventory.size());
    }

    @Test
    public void mustSurgePriceBy10PercentWhenCalledMoreThan10Times() {
        for(int i=0; i<=10;i++){
            Inventory.getInventory();
        }
        final List<Item> inventory = Inventory.getInventory();
        Assert.assertNotNull(inventory);
        Assert.assertEquals(22, inventory.get(0).getPrice());
    }

    @Test
    public void mustNotSurgePriceWhenCalledLessThan10Times() {
        for(int i=0; i<5;i++){
            Inventory.getInventory();
        }
        final List<Item> inventory = Inventory.getInventory();
        Assert.assertNotNull(inventory);
        Assert.assertEquals(20, inventory.get(0).getPrice());
    }

    @Test
    public void mustReturnExistingItemWhenCheckingInInventory() {
        final Item item = Inventory.checkInInventory("apple");
        Assert.assertNotNull(item);
        Assert.assertEquals("apple", item.getName());
    }

    @Test
    public void mustReturnNullWhenItemNotPresentInInventory() {
        final Item item = Inventory.checkInInventory("random");
        Assert.assertNull(item);
    }
}
