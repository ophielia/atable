package com.meg.listshop.auth.api;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtAuthorizationRequest;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.api.model.Dish;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceType;
import org.springframework.mobile.device.LiteDevice;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class AuthenticationRestControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;

    @Autowired
    private DishService dishService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new JwtUser(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                "Passw0rd",
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    public void testCreateAuthenticationToken() throws Exception {
        Device device = new LiteDevice(DeviceType.NORMAL);
        JwtAuthorizationRequest jwtAuthenticationRequest = new JwtAuthorizationRequest(TestConstants.USER_3_NAME, "Passw0rd");

                String deviceJson = json(device);
                String authReqJson = json(jwtAuthenticationRequest);

                mockMvc.perform(post("/auth")
                .content(deviceJson)
                .content(authReqJson))
                .andDo(print());


                //.with(user(userDetails)))
                //.andExpect(status().isOk())
                //.andExpect(content().contentType(contentType));
    }

    @Test
    @WithMockUser
    public void readDishes() throws Exception {
        mockMvc.perform(get("/dish")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));
    }

    @Test
    @WithMockUser
    public void createDish() throws Exception {

        String dishJson = json(new Dish(
                TestConstants.USER_3_ID, "created dish"));

        this.mockMvc.perform(post("/dish")
                .with(user(userDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser
    public void updateDish() throws Exception {
        DishEntity toUpdate = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        String updateName = "updated:" + toUpdate.getDishName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setDishName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setUserId(TestConstants.USER_3_ID);
        toUpdate.setTags(new ArrayList<TagEntity>());
        String dishJson = json(toUpdate);

        this.mockMvc.perform(put("/dish/" + toUpdate.getId())
                .with(user(userDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void testGetTagsByDishId() throws Exception {
        mockMvc.perform(get("/dish/" + TestConstants.DISH_2_ID + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));

    }

    @Test
    @WithMockUser
    public void testAddTagToDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/" + TestConstants.TAG_CARROTS;
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/344";
        this.mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddAndRemoveTags() throws Exception {
        List<Long> addTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long> deleteTags = Arrays.asList(55L, 104L);

        String addList = FlatStringUtils.flattenListOfLongsToString(addTags, ",");
        String deleteList = FlatStringUtils.flattenListOfLongsToString(deleteTags, ",");
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag?addTags=" + addList + "&removeTags=" + deleteList;
        this.mockMvc.perform(put(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testFindDishes() throws Exception {
        List<Long> excludedTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long>  includedTags = Arrays.asList(TestConstants.TAG_MEAT, TestConstants.TAG_PASTA);

        String includedList = FlatStringUtils.flattenListOfLongsToString(includedTags, ",");
        String excludedList = FlatStringUtils.flattenListOfLongsToString(excludedTags, ",");
        String url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList;
        this.mockMvc.perform(get(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(content().contentType(contentType));

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
