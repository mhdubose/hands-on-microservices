package dev.michaeldubose.recommendationservice;

import dev.michaeldubose.api.core.recommendation.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests {

  @Autowired
  private WebTestClient client;

  @Autowired
  private RecommendationService recommendationService;

  @Test
  void contextLoads() {
    assertThat(recommendationService).isNotNull();
  }

  @Test
  public void getRecommendationsByProductId() {

    int productId = 1;

    client.get()
      .uri("/recommendation?productId=" + productId)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.length()").isEqualTo(3)
      .jsonPath("$[0].productId").isEqualTo(productId);
  }

  @Test
  public void getRecommendationsMissingParameter() {

    client.get()
      .uri("/recommendation")
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(BAD_REQUEST)
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.path").isEqualTo("/recommendation")
      .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
  }

  @Test
  public void getRecommendationsInvalidParameter() {

    client.get()
      .uri("/recommendation?productId=no-integer")
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(BAD_REQUEST)
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.path").isEqualTo("/recommendation")
      .jsonPath("$.message").isEqualTo("Type mismatch.");
  }

  @Test
  public void getRecommendationsNotFound() {

    int productIdNotFound = 113;

    client.get()
      .uri("/recommendation?productId=" + productIdNotFound)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.length()").isEqualTo(0);
  }

  @Test
  public void getRecommendationsInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    client.get()
      .uri("/recommendation?productId=" + productIdInvalid)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.path").isEqualTo("/recommendation")
      .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
  }
  
}
