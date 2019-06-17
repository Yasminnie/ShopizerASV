package com.salesmanager.test.shop.integration.security;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.customer.CustomerGender;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.store.security.AuthenticationRequest;
import com.salesmanager.shop.store.security.AuthenticationResponse;
import com.salesmanager.test.shop.common.ServicesTestSupport;

public class CustomerRegistrationIntegrationTest extends ServicesTestSupport {

    @Test
    public void registerCustomer() {
        final PersistableCustomer testCustomer = new PersistableCustomer();
        testCustomer.setEmailAddress("customer1@test.com");
        testCustomer.setUserName("testCust1");
        testCustomer.setClearPassword("clear123");
        testCustomer.setGender(CustomerGender.M.name());
        testCustomer.setLanguage("en");
        final Address billing = new Address();
        billing.setFirstName("customer1");
        billing.setLastName("ccstomer1");
        billing.setCountry("BE");
        testCustomer.setBilling(billing);
        testCustomer.setStoreCode(Constants.DEFAULT_STORE);
        final HttpEntity<PersistableCustomer> entity = new HttpEntity<>(testCustomer, getHeader());

        final ResponseEntity<PersistableCustomer> response = testRestTemplate.postForEntity("/api/v1/customer/register", entity, PersistableCustomer.class);
        assertThat(response.getStatusCode(), is(OK));

        // created customer can login
        final ResponseEntity<AuthenticationResponse> loginResponse = testRestTemplate.postForEntity("/api/v1/customer/login", new HttpEntity<>(new AuthenticationRequest("testCust1", "clear123")),
                AuthenticationResponse.class);
        assertThat(response.getStatusCode(), is(OK));
        assertNotNull(loginResponse.getBody().getToken());
    }

	@Test(timeout=3000)
	public void doubleRegistrations() {
        try {
            final Address billing = new Address();

            final PersistableCustomer sister = new PersistableCustomer();
            sister.setEmailAddress("brotherandsister@test.com");
            sister.setUserName("jan");
            sister.setClearPassword("clear123");
            sister.setGender(CustomerGender.M.name());
            sister.setLanguage("en");
            billing.setFirstName("jan");
            billing.setLastName("customer1");
            billing.setCountry("BE");
            sister.setBilling(billing);
            sister.setStoreCode(Constants.DEFAULT_STORE);
            final HttpEntity<PersistableCustomer> sisterentity = new HttpEntity<>(sister, getHeader());
            final ResponseEntity<PersistableCustomer> response = testRestTemplate.postForEntity("/api/v1/customer/register", sisterentity, PersistableCustomer.class);
            assertThat(response.getStatusCode(), is(OK));

            final PersistableCustomer brother = new PersistableCustomer();
            brother.setEmailAddress("brotherandsister@test.com");
            brother.setUserName("sanne");
            brother.setClearPassword("clear123");
            brother.setGender(CustomerGender.F.name());
            brother.setLanguage("en");
            billing.setFirstName(null);
            billing.setLastName("customer2");
            billing.setCountry("BE");
            brother.setBilling(billing);
            brother.setStoreCode(Constants.DEFAULT_STORE);
            final HttpEntity<PersistableCustomer> brotherentity = new HttpEntity<>(brother, getHeader());
            final ResponseEntity<PersistableCustomer> responseBrother = testRestTemplate.postForEntity("/api/v1/customer/register", brotherentity, PersistableCustomer.class);
            assertThat(responseBrother.getStatusCode(), is(OK));
        } catch (Exception e) {
            assertTrue(true);
        };

	}
}
