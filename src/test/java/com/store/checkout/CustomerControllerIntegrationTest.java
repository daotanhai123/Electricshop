package com.store.checkout;

import com.store.checkout.dto.BasketDTO;
import com.store.checkout.dto.BasketItemDTO;
import com.store.checkout.dto.ProductDTO;
import com.store.checkout.dto.ReceiptDTO;
import com.store.checkout.entity.Product;
import com.store.checkout.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(100);
        product.setDescription("Test Product");
        product = productRepository.save(product);
    }

    @Test
    public void givenValidBasketItem_whenAddProductToBasket_thenReturnUpdatedBasket() {
        // given
        BasketItemDTO basketItemDTO = new BasketItemDTO(new ProductDTO(product.getId(), product.getName(), product.getDescription() ,product.getPrice(), product.getQuantity()), 2);

        // when
        ResponseEntity<BasketDTO> response = restTemplate.postForEntity("/v1/basket/{customerId}/item", basketItemDTO, BasketDTO.class, 1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getBasketItems()).isNotNull();
        assertThat(response.getBody().getBasketItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    public void givenValidBasketItem_whenRemoveProductFromBasket_thenReturnUpdatedBasket() {
        // given
        givenValidBasketItem_whenAddProductToBasket_thenReturnUpdatedBasket();
        BasketItemDTO basketItemDTO = new BasketItemDTO(new ProductDTO(product.getId(), product.getName(), product.getDescription() ,product.getPrice(), product.getQuantity()), 2);


        HttpEntity<BasketItemDTO> requestEntity = new HttpEntity<>(basketItemDTO);

        // when
        ResponseEntity<BasketDTO> response = restTemplate.exchange(
                "/v1/basket/{customerId}/item",
                HttpMethod.DELETE,
                requestEntity,
                BasketDTO.class,
                1L
        );

        // then
        System.out.println("Response: " + response.getBody());
        System.out.println("Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenBasketWithProducts_whenCalculateReceipt_thenReturnValidReceipt() {
        // given
        givenValidBasketItem_whenAddProductToBasket_thenReturnUpdatedBasket();

        // when
        ResponseEntity<ReceiptDTO> response = restTemplate.getForEntity("/v1/basket/{customerId}/receipt", ReceiptDTO.class, 1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getReceiptItemDTOS()).isNotNull();
        assertThat(response.getBody().getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    }
}
