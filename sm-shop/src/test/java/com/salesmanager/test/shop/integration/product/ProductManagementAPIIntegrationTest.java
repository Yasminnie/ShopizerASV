package com.salesmanager.test.shop.integration.product;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.ajax.AjaxResponse;
import com.salesmanager.shop.model.catalog.product.*;
import com.salesmanager.shop.model.shop.ContactForm;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionType;
import com.salesmanager.shop.model.catalog.category.Category;
import com.salesmanager.shop.model.catalog.category.CategoryDescription;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.manufacturer.Manufacturer;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.test.shop.common.ServicesTestSupport;

public class ProductManagementAPIIntegrationTest extends ServicesTestSupport {

    private RestTemplate restTemplate;

    private Long testCategoryID;

    private Long testProductID;
    private LanguageService languageService;


    @Test
    public void createProductWithCategory() throws Exception {

        final PersistableCategory newCategory = new PersistableCategory();
        newCategory.setCode("test-cat");
        newCategory.setSortOrder(1);
        newCategory.setVisible(true);
        newCategory.setDepth(4);

        final Category parent = new Category();

        newCategory.setParent(parent);

        final CategoryDescription description = new CategoryDescription();
        description.setLanguage("en");
        description.setName("test-cat");
        description.setFriendlyUrl("test-cat");
        description.setTitle("test-cat");

        final List<CategoryDescription> descriptions = new ArrayList<>();
        descriptions.add(description);

        newCategory.setDescriptions(descriptions);

        final HttpEntity<PersistableCategory> categoryEntity = new HttpEntity<>(newCategory, getHeader());

        final ResponseEntity<PersistableCategory> categoryResponse = testRestTemplate.postForEntity("/api/v1/private/category?store=" + Constants.DEFAULT_STORE, categoryEntity,
                PersistableCategory.class);
        final PersistableCategory cat = categoryResponse.getBody();
        assertThat(categoryResponse.getStatusCode(), is(OK));
        assertNotNull(cat.getId());

        final PersistableProduct product = new PersistableProduct();
        final ArrayList<Category> categories = new ArrayList<>();
        categories.add(cat);
        product.setCategories(categories);
        product.setManufacturer(createManufacturer());
        product.setPrice(BigDecimal.TEN);
        product.setSku("123");
        final HttpEntity<PersistableProduct> entity = new HttpEntity<>(product, getHeader());

        final ResponseEntity<PersistableProduct> response = testRestTemplate.postForEntity("/api/v1/private/products?store=" + Constants.DEFAULT_STORE, entity, PersistableProduct.class);
        assertThat(response.getStatusCode(), is(CREATED));
    }

    /**
     * Creates a ProductReview
     * requires an existing Customer and an existing Product
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void createProductReview() throws Exception {


        final PersistableProductReview review = new PersistableProductReview();
        review.setCustomerId(1L);

        review.setProductId(1L);
        review.setLanguage("en");
        review.setRating(2D);// rating is on 5
        review.setDescription(
                "Not as good as expected. From what i understood that was supposed to be premium quality but unfortunately i had to return the item after one week... Verry disapointed !");
        review.setDate("2013-06-06");
        final HttpEntity<PersistableProductReview> entity = new HttpEntity<>(review, getHeader());

        final ResponseEntity<PersistableProductReview> response = testRestTemplate.postForEntity("/api/v1/private/products/1/reviews?store=" + Constants.DEFAULT_STORE,
                entity,
                PersistableProductReview.class);

        final PersistableProductReview rev = response.getBody();
        assertThat(response.getStatusCode(), is(CREATED));
        assertNotNull(rev.getId());

    }

    /**
     * Creates a product option value that can be used to create a product attribute
     * when creating a new product
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void createOptionValue() throws Exception {

        final ProductOptionValueDescription description = new ProductOptionValueDescription();
        description.setLanguage("en");
        description.setName("Red");

        final List<ProductOptionValueDescription> descriptions = new ArrayList<>();
        descriptions.add(description);

        final PersistableProductOptionValue optionValue = new PersistableProductOptionValue();
        optionValue.setOrder(1);
        optionValue.setCode("colorred");
        optionValue.setDescriptions(descriptions);

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(optionValue);

        restTemplate = new RestTemplate();

        final HttpEntity<String> entity = new HttpEntity<>(json, getHeader());

        final ResponseEntity<PersistableProductOptionValue> response = restTemplate.postForEntity("http://localhost:8080/sm-shop/services/private/DEFAULT/product/optionValue", entity, PersistableProductOptionValue.class);
        assertThat(response.getStatusCode(), is(OK));
    }

    /**
     * Creates a new ProductOption
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void createOption() throws Exception {

        final ProductOptionDescription description = new ProductOptionDescription();
        description.setLanguage("en");
        description.setName("Color");

        final List<ProductOptionDescription> descriptions = new ArrayList<>();
        descriptions.add(description);

        final PersistableProductOption option = new PersistableProductOption();
        option.setOrder(1);
        option.setCode("color");
        option.setType(ProductOptionType.Select.name());
        option.setDescriptions(descriptions);

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(option);

        System.out.println(json);

        /**
         * {
         * "descriptions" : [ {
         * "name" : "Color",
         * "description" : null,
         * "friendlyUrl" : null,
         * "keyWords" : null,
         * "highlights" : null,
         * "metaDescription" : null,
         * "title" : null,
         * "language" : "en",
         * "id" : 0
         * } ],
         * "type" : SELECT,
         * "order" : 1,
         * "code" : "color",
         * "id" : 0
         * }
         */

        restTemplate = new RestTemplate();

        final HttpEntity<String> entity = new HttpEntity<>(json, getHeader());

        final ResponseEntity<PersistableProductOption> response = restTemplate.postForEntity("http://localhost:8080/sm-shop/services/private/DEFAULT/product/option", entity, PersistableProductOption.class);

        final PersistableProductOption opt = response.getBody();
        System.out.println("New option ID : " + opt.getId());

    }

    @Test
    @Ignore
    public void getProducts() throws Exception {
        restTemplate = new RestTemplate();

        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());

        final ResponseEntity<ReadableProduct[]> response = restTemplate.exchange("http://localhost:8080/sm-shop/services/rest/products/DEFAULT/en/" + testCategoryID, HttpMethod.GET, httpEntity,
                ReadableProduct[].class);
        assertThat(response.getStatusCode(), is(OK));
    }

    @Test
    @Ignore
    public void putProduct() throws Exception {
        restTemplate = new RestTemplate();

        // TODO: Put Product

    }

    @Test
    @Ignore
    public void postProduct() throws Exception {
        restTemplate = new RestTemplate();

        final PersistableProduct product = new PersistableProduct();

        final String code = "abcdef";

        final String categoryCode = "ROOT";// root category

        final Category category = new Category();
        category.setCode(categoryCode);
        final List<Category> categories = new ArrayList<>();
        categories.add(category);

        final String manufacturer = "temple";
        final Manufacturer collection = new Manufacturer();
        collection.setCode(manufacturer);

        // core properties

        product.setSku(code);
        product.setSortOrder(0);// set iterator as sort order
        product.setAvailable(true);// force availability
        product.setProductVirtual(false);// force tangible good
        product.setQuantityOrderMinimum(1);// force to 1 minimum when ordering
        product.setProductShipeable(true);// all items are shipeable

        /** images **/
        final String image = "/Users/carlsamson/Documents/csti/IMG_4626.jpg";

        final File imgPath = new File(image);

        product.setProductHeight(new BigDecimal(20));
        product.setProductLength(new BigDecimal(20));
        product.setProductWeight(new BigDecimal(20));
        product.setProductWidth(new BigDecimal(20));
        product.setQuantity(5);
        product.setQuantityOrderMaximum(2);

        final PersistableProductPrice productPrice = new PersistableProductPrice();
        productPrice.setDefaultPrice(true);

        productPrice.setOriginalPrice(new BigDecimal(250));
        productPrice.setDiscountedPrice(new BigDecimal(125));

        final List<PersistableProductPrice> productPriceList = new ArrayList<>();
        productPriceList.add(productPrice);

        product.setProductPrices(productPriceList);

        final List<ProductDescription> descriptions = new ArrayList<>();

        // add english description
        ProductDescription description = new ProductDescription();
        description.setLanguage("en");
        description.setTitle("Buddha Head");
        description.setName("Buddha Head");
        description.setDescription("Buddha Head");
        description.setFriendlyUrl("buddha-head");

        // description.setHighlights(record.get("highlights_en"));

        descriptions.add(description);

        // add french description
        description = new ProductDescription();
        description.setLanguage("fr");
        description.setTitle("Tête de Buddha");
        description.setName("Tête de Buddha");
        description.setDescription(description.getName());
        description.setFriendlyUrl("tete-de-buddha");
        //

        descriptions.add(description);

        product.setDescriptions(descriptions);

        // RENTAL
        final RentalOwner owner = new RentalOwner();
        // need to create a customer first
        owner.setId(1L);
        product.setOwner(owner);

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(product);

        System.out.println(json);

        final HttpEntity<String> entity = new HttpEntity<>(json, getHeader());

        // post to create category web service
        final ResponseEntity<PersistableProduct> response = restTemplate.postForEntity("http://localhost:8080/api/v1/product", entity, PersistableProduct.class);

        final PersistableProduct prod = response.getBody();

        System.out.println("---------------------");
    }

    @Test (timeout = 1500)
    public void deleteProduct() {
        restTemplate = new RestTemplate();
        //        Maak eerst een nieuw product aan en voeg deze toe

        RentalOwner owner = new RentalOwner();
        owner.setEmailAddress("testByYasmin@gmail.com");

        final PersistableProduct product = new PersistableProduct();
        product.setOwner(owner);
        product.setManufacturer(createManufacturer());
        product.setPrice(BigDecimal.TEN);
        final HttpEntity<PersistableProduct> entity = new HttpEntity<>(product, getHeader());
        final ResponseEntity<PersistableProduct> response = testRestTemplate.postForEntity("/api/v1/private/products?store=" + Constants.DEFAULT_STORE, entity, PersistableProduct.class);
        assertThat(response.getStatusCode(), is(CREATED));

        //        Verwijder het nieuw aangemaakte product nadat deze is aangemaakt
        if (response.getStatusCode() == HttpStatus.OK) {
            final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());
            restTemplate.delete("http://localhost:8080/sm-shop/services/rest/products/DEFAULT/en/" + testCategoryID + "/" + testProductID, HttpMethod.DELETE, httpEntity, ReadableProduct.class);

            final HttpEntity<String> productHttpEntity = new HttpEntity<>(product.getOwner().getEmailAddress(), getHeader());

            final ResponseEntity<PersistableProduct> responseEntity = testRestTemplate.postForEntity("/api/v1/private/products?store=" + Constants.DEFAULT_STORE, productHttpEntity, PersistableProduct.class);
            assertThat(responseEntity.getStatusCode(), is(CREATED));
        }
    }

    /**
     * private helper methods
     **/
    public byte[] extractBytes(final File imgPath) throws Exception {
        final FileInputStream fis = new FileInputStream(imgPath);

        final BufferedInputStream inputStream = new BufferedInputStream(fis);
        final byte[] fileBytes = new byte[(int) imgPath.length()];
        inputStream.read(fileBytes);
        inputStream.close();

        return fileBytes;
    }


    /**
     * Contact us email
     *
     * @throws Exception
     */
    @Test(timeout = 500)
    public void contactUs() throws Exception {
        restTemplate = new RestTemplate();

        ContactForm contact = new ContactForm();
        contact.setComment("Test comment");
        contact.setEmail(null);
        contact.setName("Yasmin de Roo");
        contact.setSubject("Test contactform");

        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = writer.writeValueAsString(contact);

        System.out.println(json);

        HttpEntity<String> httpEntity = new HttpEntity<String>(json, getHeader());

        ResponseEntity<AjaxResponse> response = restTemplate.exchange("http://localhost:8080/sm-shop/services/public/DEFAULT/contact", HttpMethod.POST, httpEntity, AjaxResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception();
        } else {
            System.out.println(response.getBody() + " Success ");
        }
    }

    @Test(timeout = 3000)
    public void addProductWithoutCategory() throws ServiceException {
        PersistableProduct product = new PersistableProduct();

        product.setProductHeight(new BigDecimal(4));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(1));
        product.setSku("YASMIN TEST");
        product.setManufacturer(createManufacturer());
        product.setCategories(null);
        product.setPrice(new BigDecimal(39.99));

        ProductDescription desc = new ProductDescription();
        desc.setName("Yasmin's favourite bag");
        desc.setLanguage("en");
        product.getDescriptions().add(desc);

        final HttpEntity<PersistableProduct> entity = new HttpEntity<>(product, getHeader());
        final ResponseEntity<PersistableProduct> response = testRestTemplate.postForEntity("/api/v1/private/products?store=" + Constants.DEFAULT_STORE, entity, PersistableProduct.class);
        assertThat(response.getStatusCode(), is(CREATED));

        final PersistableProduct persistableProduct = new PersistableProduct();
        persistableProduct.setCategories(null);
        persistableProduct.setManufacturer(createManufacturer());
        persistableProduct.setPrice(BigDecimal.TEN);
        persistableProduct.setSku("123");
        final HttpEntity<PersistableProduct> productHttpEntity = new HttpEntity<>(persistableProduct, getHeader());

        final ResponseEntity<PersistableProduct> responseEntity = testRestTemplate.postForEntity("/api/v1/private/products?store=" + Constants.DEFAULT_STORE, productHttpEntity, PersistableProduct.class);
        assertThat(responseEntity.getStatusCode(), is(CREATED));

    }
}
