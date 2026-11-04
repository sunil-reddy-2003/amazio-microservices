package com.amazio.inventory_service.controller;

import com.amazio.inventory_service.entity.Inventory;
import com.amazio.inventory_service.exception.InventoryNotFoundException;
import com.amazio.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryApi {

    private final InventoryService inventoryService;

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Map<String,Object>> updateStock(
            @PathVariable int productId,
            @RequestParam int quantity) {

        try{
            Inventory inventory=inventoryService.addStock(productId, quantity);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message","Inventory updated successfully",
                                    "data",inventory
                            )
            );
        }catch (IllegalArgumentException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()
                            )
                    );
        }catch (InventoryNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()
                            )
                    );
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String,Object>> getInventory(
            @PathVariable int productId) {

        try {
            Inventory inventory = inventoryService.getInventory(productId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Inventory retrieved successfully",
                                    "data", inventory
                            )
                    );
        }catch (InventoryNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
    }
}