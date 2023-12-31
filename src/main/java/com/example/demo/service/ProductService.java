package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.Status;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.repository.ProductRepo;

@Service
public class ProductService {
	@Autowired
	ProductRepo productRepo;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	CategoryRepo categoryRepo;

//	PropertyMap<ProductDTO, Product> skipModifiedFieldsMap = new PropertyMap<ProductDTO, Product>() {
//		protected void configure() {
//			skip().setCategories(null);
//		}
//	};

	public static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
	public void addProduct(CategoryDTO categoryDTO) {
		try {
			LOGGER.debug("Inside addProduct::"+categoryDTO.toString());
			
			if (categoryDTO.getId() == null) {
	            LOGGER.error("Category ID is required to add products.");
	            return; 
	        }

			Optional<Category> category=categoryRepo.findById(categoryDTO.getId());
			
			if(category.isPresent()) {
				
				//modelMapper.addMappings(skipModifiedFieldsMap);
				List<Product> products =  modelMapper.map(categoryDTO.getProducts(),new TypeToken<List<Product>>()  {}.getType());
				
				products.stream().forEach(prd->{
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();

					prd.setCreated_at(dtf.format(now));
					prd.setUpdated_at(dtf.format(now));
					prd.setStatus(Status.active);
				});
				
				category.get().getProducts().addAll(products);
				categoryRepo.save(category.get());
				LOGGER.debug("Product Added Successfully");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error("Exception in addProduct::"+ex.getMessage());
		}

	}

	public List<ProductDTO> getAllProduct(Long id) {
		try {
			LOGGER.info("Get All Product");
			Optional<Category> productCategory=categoryRepo.findById(id);
			if(productCategory.isPresent()) {
				Category category=productCategory.get();
				Set<Product> products=category.getProducts();
				List<ProductDTO> productdtoList=modelMapper.map(products,new TypeToken<List<ProductDTO>>() {}.getType() );
				return productdtoList;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
		return null;
	}
	
//	public ProductResponseDTO 
	public List<ProductDTO> getAllProducts(){
		try {
			LOGGER.info("Get All Products");  
			List<Product> products=productRepo.findAll();
			
			List<ProductDTO> productDTOlist = products.stream()
	                .filter(product -> product.getStatus() == Status.active)
	                .map(product -> modelMapper.map(product, ProductDTO.class))
	                .collect(Collectors.toList());

	        return productDTOlist;
			
	}catch (Exception ex) {
        ex.printStackTrace();
        LOGGER.error(ex.getMessage());
        return Collections.emptyList();}
	}



	public List<ProductDTO> getActiveProducts(Long id) {
		try {
			LOGGER.info("Get Active Products");
			Optional<Category> categoryOptional=categoryRepo.findById(id);
			if(categoryOptional.isPresent()) {
				Category category=categoryOptional.get();
				Set<Product> products = category.getProducts();
				List<ProductDTO> productDTOList =  modelMapper.map(products,new TypeToken<List<ProductDTO>>()  {}.getType());
				return productDTOList;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
		return null;
	}

//	public String getProductByProductNameToJson(String title) {
//	    try {
//	        LOGGER.info("Get Product By Product Name");
//	        Product product = productRepo.findByTitleAndStatus(title, Status.active);
//	        
//	        if (product != null) {
//	            ObjectMapper objectMapper = new ObjectMapper();
//	            String productJson = objectMapper.writeValueAsString(product);
//	            return productJson;
//	        } else {
//	            return null;
//	        }
//	    } catch (JsonProcessingException ex) {
//	        ex.printStackTrace();
//	        LOGGER.error(ex.getMessage());
//	    }
//	    return null;
//	}
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
	public void updateProductInCategories(CategoryDTO categoryDTO) {
		try {
			LOGGER.debug("Inside UpdateProduct::"+categoryDTO.toString());
			Optional<Category> productCategory = categoryRepo.findById(categoryDTO.getId());

			if (productCategory.isPresent()) {
				Category category=productCategory.get();
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	            LocalDateTime now = LocalDateTime.now();
				categoryDTO.getProducts().forEach(productDTO->{
					Optional<Product> product=category.getProducts().stream().filter(prd-> prd.getId().equals(productDTO.getId())).findAny();
					product.get().setDescription(productDTO.getDescription());
					product.get().setTitle(productDTO.getTitle());
					product.get().setUpdated_at(dtf.format(now));
				});
				
				categoryRepo.save(category);
				LOGGER.debug("Product Added Successfully");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error("Exception in update Product::"+ex.getMessage());
			
		}
		
	}

	public String inactiveProductById(Long id) {
		try {
			LOGGER.info("Get Inactive Products");
			Optional<Product> productOptional = productRepo.findById(id);
			if (productOptional.isPresent()) {
				Product product = productOptional.get();

				product.setStatus(Status.inactive);
				productRepo.save(product);
				return "Product Succesfully marked as inactive";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}  
		
		return "Product not found";
	}
	
//	public List<ProductResponseDTO> getAllProductsWithCategories() {
//		LOGGER.info("Get Product With Categories");
//		try {
//	    List<Product> products = productRepo.findByStatus(Status.active);
//	    List<ProductResponseDTO> productDTOList =  modelMapper.map(products,new TypeToken<List<ProductResponseDTO>>()  {}.getType());
//		return productDTOList;
//		}catch (Exception ex) {
//			ex.printStackTrace();
//			LOGGER.error(ex.getMessage());
//		}
//		return null;
//	}
//	

}
