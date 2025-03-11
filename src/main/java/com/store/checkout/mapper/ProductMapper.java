package com.store.checkout.mapper;

import com.store.checkout.dto.ProductDTO;
import com.store.checkout.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product dtoToEntity(ProductDTO dto);

    ProductDTO entityToDto(Product product);
}
