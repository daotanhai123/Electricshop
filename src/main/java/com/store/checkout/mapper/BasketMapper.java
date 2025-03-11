package com.store.checkout.mapper;

import com.store.checkout.dto.BasketDTO;
import com.store.checkout.entity.Basket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BasketMapper {
    Basket dtoToEntity(BasketDTO basketDTO);
    BasketDTO entityToDto(Basket basket);
}
