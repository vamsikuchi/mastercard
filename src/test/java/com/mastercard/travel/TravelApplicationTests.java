package com.mastercard.travel;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class TravelApplicationTests {

    @Autowired
    private MockMvc mockMvc;


	/**
	 *  test with Blank inputs.
	 *  chceks status code and response value
	 */
	@Test
	public void testBlanks() throws Exception {
        ResultActions perform = mockMvc.perform(get("/connected?origin=&destination=")).
				andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("no")));
    }

	/**
	 *  test with Same City Names.
	 *  chceks status code and response value
	 */
	@Test
	public void testSameCitiesConnected() throws Exception {
		ResultActions perform = mockMvc.perform(get("/connected?origin=Boston&destination=Boston")).
				andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("yes")));
	}

	/**
	 *  test with Same City Names with different Cases.
	 *  chceks status code and response value
	 */
	@Test
	public void testSameCitiesUpperCaseConnected() throws Exception {
		ResultActions perform = mockMvc.perform(get("/connected?origin=BOSTON&destination=boston")).
				andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("yes")));
	}

	/**
	 *  test with Same City Names with POST.
	 *  chceks status code as Method not allowed
	 */
	@Test
	public void testCitiesConnectedWithPost() throws Exception {
		ResultActions perform = mockMvc.perform(post("/connected?origin=BOSTON&destination=boston")).
				andDo(print()).andExpect(status().isMethodNotAllowed());
		//dExpect(content().string(containsString("")));
	}

	/**
	 *  test with Same City Names with Put.
	 *  chceks status code as Method not allowed
	 */
	@Test
	public void testCitiesConnectedWithPut() throws Exception {
		ResultActions perform = mockMvc.perform(put("/connected?origin=BOSTON&destination=boston")).
				andDo(print()).andExpect(status().isMethodNotAllowed());
		//dExpect(content().string(containsString("")));
	}

	/**
	 *  test with Same City Names with Delete.
	 *  chceks status code as Method not allowed
	 */
	@Test
	public void testCitiesConnectedWithDelete() throws Exception {
		ResultActions perform = mockMvc.perform(delete("/connected?origin=BOSTON&destination=boston")).
				andDo(print()).andExpect(status().isMethodNotAllowed());
		//dExpect(content().string(containsString("")));
	}

	/**
	 *  test with cities connection
	 *  chceks status code and response value
	 */
	@Test
	public void testDifferentCitiesConnection() throws Exception {
		ResultActions perform = mockMvc.perform(get("/connected?origin=Boston&destination=Albany")).
				andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("yes")));
	}




}
