package com.gilded.rose.controllers;

import com.gilded.rose.backend.Inventory;
import com.gilded.rose.model.Item;
import com.gilded.rose.model.Items;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "inn", description = "Rest API for gilded inn operations", tags = "Gilded Inn API")
public class InnController {

    @RequestMapping(value = "/inventory", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Retrieve list of all items at the Inn", response = Items.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    }
    )
    public ResponseEntity<Items> getItems() {
        final List<Item> allItems = Inventory.getInventory();
        return new ResponseEntity<Items>(new Items(allItems), HttpStatus.OK);
    }

    @RequestMapping(value = "/purchase/{itemName}", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Purchase an Item from the Inn", response = Items.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Item not found in Inn")
    }
    )
    public  ResponseEntity<Item> buyItem(@PathVariable String itemName) {
        final Item item = Inventory.checkInInventory(itemName);
        if(item != null) {
            // Can do other things in here or in inner function, like reduce quantity or some other things.
            return new ResponseEntity<Item>(item, HttpStatus.OK);
        }
        return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
    }
}
