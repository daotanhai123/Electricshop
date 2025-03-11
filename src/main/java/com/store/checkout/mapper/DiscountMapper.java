package com.store.checkout.mapper;

import com.store.checkout.dto.DiscountDTO;
import com.store.checkout.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "products", ignore = true)
    Discount dtoToEntity(DiscountDTO dto);

    @Mapping(target = "productIds", ignore = true)
    DiscountDTO entityToDto(Discount entity);
}
