package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ItemDTO;
import com.example.demo.service.ItemService;

@RestController
@RequestMapping("/api/item")
@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*")
public class ItemController {
	@Autowired
	ItemService itemService;
	
	@PostMapping("/additem")
	public ResponseEntity<ItemDTO> addItem(@RequestBody CategoryDTO categoryDTO) {
		
		itemService.addItem(categoryDTO);
		
		
		return new ResponseEntity<ItemDTO>(HttpStatus.CREATED);
	}
	@GetMapping("/getAllItems/{id}")
	public List<ItemDTO> getAllItems(@PathVariable("id") Long id){
		return itemService.getAllItems(id);
	}
	
	@GetMapping("/getAllItems")
	public List<ItemDTO> getAllItems(){
		return itemService.getAllItems();
	}
	
	
	@PutMapping("/updateItem")
	public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDTO){
		itemService.updateItem(itemDTO);
		return new ResponseEntity<ItemDTO>(HttpStatus.OK);
	}
	
	@DeleteMapping("/inactiveItem/{id}")
	public String inactiveItem(@PathVariable("id") Long id) {
	itemService.inActiveItemById(id);
	    return "Item successfully marked as inactive";
	     }
	
	@PutMapping("/updateItemPrice")
	public ResponseEntity<ItemDTO> updateItemPrice(@RequestBody ItemDTO itemDTO) {
		itemService.updateItemPrice(itemDTO);
		return new ResponseEntity<ItemDTO>(HttpStatus.OK);
	}

}
