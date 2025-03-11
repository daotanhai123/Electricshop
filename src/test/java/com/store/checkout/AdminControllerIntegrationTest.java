package com.store.checkout;

import com.store.checkout.dto.DiscountDTO;
import com.store.checkout.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void givenValidProductDTO_whenCreateProduct_thenReturnCreatedProduct() {
        // given
        ProductDTO productDto = new ProductDTO();
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(BigDecimal.valueOf(100));
        productDto.setQuantity(10);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/admin/product", productDto, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void givenInvalidProductDTO_whenCreateProduct_thenReturnBadRequest() {
        // given
        ProductDTO productDto = new ProductDTO();
        productDto.setName("");
        productDto.setDescription("Test Description");
        productDto.setPrice(BigDecimal.valueOf(100));
        productDto.setQuantity(10);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/admin/product", productDto, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenExistingProduct_whenRemoveProduct_thenReturnNoContent() {
        // given: first create a product
        ProductDTO productDto = new ProductDTO();
        productDto.setName("Product To Delete");
        productDto.setDescription("Description");
        productDto.setPrice(BigDecimal.valueOf(50));
        productDto.setQuantity(5);
        restTemplate.postForEntity("/v1/admin/product", productDto, String.class);
        long productId = 1L;
        // when
        ResponseEntity<Void> response = restTemplate.exchange("/v1/admin/product/" + productId,
                HttpMethod.DELETE, null, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenNonExistingProduct_whenRemoveProduct_thenReturnNotFound() {
        // when
        ResponseEntity<Void> response = restTemplate.exchange("/v1/admin/product/99999",
                HttpMethod.DELETE, null, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenValidDiscountDTO_whenAddDiscount_thenReturnCreatedDiscount() {
        // given
        ProductDTO productDto = new ProductDTO();
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(BigDecimal.valueOf(100));
        productDto.setQuantity(10);
        restTemplate.postForEntity("/v1/admin/product", productDto, String.class);

        DiscountDTO discountDto = new DiscountDTO();
        discountDto.setDiscountType("BUY_ONE_GET_HALF_OFF");
        discountDto.setParameters("20");
        discountDto.setDescription("20% off flash sale");
        discountDto.setStartDate(LocalDate.now());
        discountDto.setEndDate(LocalDate.now().plusDays(1));
        discountDto.setUsageLimit(100);
        discountDto.setProductIds(Collections.singleton(1L));

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/admin/discount", discountDto, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void givenInvalidDiscountDTO_whenAddDiscount_thenReturnBadRequest() {
        // given
        DiscountDTO discountDto = new DiscountDTO();
        discountDto.setParameters("20");
        discountDto.setDescription("20% off flash sale");
        discountDto.setStartDate(LocalDate.now());
        discountDto.setEndDate(LocalDate.now().plusDays(1));
        discountDto.setUsageLimit(100);
        discountDto.setProductIds(Collections.singleton(1L));

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/admin/discount", discountDto, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
