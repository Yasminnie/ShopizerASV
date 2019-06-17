package com.salesmanager.core.business.utils;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import com.amazonaws.util.Base64;
import com.salesmanager.core.constants.MeasureUnit;
import com.salesmanager.core.model.merchant.MerchantStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.security.crypto.codec.Base64;

public class DataUtils {
	
	/**
	 * Removes dashes
	 * @param postalCode
	 * @return
	 */
	public static String trimPostalCode(String postalCode) {

		String pc = postalCode.replaceAll("[^a-zA-Z0-9]", "");

		return pc;

	}
	
	
	/**
	 * Get the measure according to the appropriate measure base. If the measure
	 * configured in store is LB and it needs KG then the appropriate
	 * calculation is done
	 * 
	 * @param weight
	 * @param store
	 * @param base
	 * @return
	 */
	public static double getWeight(double weight, MerchantStore store,
			String base) {

		double weightConstant = 2.2;
		if (base.equals(MeasureUnit.LB.name())) {
			if (store.getWeightunitcode().equals(MeasureUnit.LB.name())) {
				return new BigDecimal(String.valueOf(weight)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {// pound = kilogram
				double answer = weight * weightConstant;
				BigDecimal w = BigDecimal.valueOf(answer);
				return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		} else {// need KG
			if (store.getWeightunitcode().equals(MeasureUnit.KG.name())) {
				return new BigDecimal(String.valueOf(weight)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {
				double answer = weight / weightConstant;
				BigDecimal w = BigDecimal.valueOf(answer);
				return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			}
		}
	}
	
	/**
	 * Get the measure according to the appropriate measure base. If the measure
	 * configured in store is IN and it needs CM or vise versa then the
	 * appropriate calculation is done
	 * 
	 * @param measure
	 * @param store
	 * @param base
	 * @return
	 */
	public static double getMeasure(double measure, MerchantStore store,
			String base) {

		if (base.equals(MeasureUnit.IN.name())) {
			if (store.getSeizeunitcode().equals(MeasureUnit.IN.name())) {
				return new BigDecimal(String.valueOf(measure)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {// centimeter (inch to centimeter)
				double measureConstant = 2.54;

				double answer = measure * measureConstant;
				BigDecimal w = BigDecimal.valueOf(answer);
				return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		} else {// need CM
			if (store.getSeizeunitcode().equals(MeasureUnit.CM.name())) {
				return new BigDecimal(String.valueOf(measure)).setScale(2)
						.doubleValue();
			} else {// in (centimeter to inch)
				double measureConstant = 0.39;

				double answer = measure * measureConstant;
				BigDecimal w = BigDecimal.valueOf(answer);
				return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		}
	}

	public static HttpHeaders getHeader(){
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		//MediaType.APPLICATION_JSON //for application/json
		headers.setContentType(mediaType);
		//Basic Authentication
		String authorisation = "admin" + ":" + "password";
		byte[] encodedAuthorisation = Base64.encode(authorisation.getBytes());
		headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
		return headers;
	}
}
