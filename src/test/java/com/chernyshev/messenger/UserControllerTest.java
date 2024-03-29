package com.chernyshev.messenger;

import com.chernyshev.messenger.controllers.AuthenticationController;
import com.chernyshev.messenger.controllers.UserController;
import com.chernyshev.messenger.dtos.LoginDto;
import com.chernyshev.messenger.dtos.PasswordDto;
import com.chernyshev.messenger.dtos.RecoverTokenDto;
import com.chernyshev.messenger.dtos.UserDto;
import com.chernyshev.messenger.exceptions.CustomExceptionHandler;
import com.chernyshev.messenger.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/init.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/clear.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Test
    @WithUserDetails("test1234")
    public void getInfoTest() throws Exception{
        mockMvc.perform(get(UserController.USER.replaceAll("\\{username\\}","test1234")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname",is("test1")))
                .andExpect(jsonPath("$.firstname",is("test1")))
                .andExpect(jsonPath("$.username",is("test1234")))
                .andExpect(jsonPath("$.email",is("test1@gmail.com")))
                .andExpect(jsonPath("$.bio",nullValue()))
                .andExpect(jsonPath("$.status",nullValue()))
                .andExpect(jsonPath("$.avatar_url",nullValue()))
                .andExpect(jsonPath("$.is_receive_messages_friend_only",is(false)))
                .andExpect(jsonPath("$.is_friends_list_hidden",is(false)));

    }

    @Test
    @WithUserDetails("test1234")
    public void getUserInfoTest() throws Exception{
        mockMvc.perform(get(UserController.USER.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname",is("test2")))
                .andExpect(jsonPath("$.firstname",is("test2")))
                .andExpect(jsonPath("$.username",is("test2345")))
                .andExpect(jsonPath("$.email",is("test2@gmail.com")))
                .andExpect(jsonPath("$.bio",nullValue()))
                .andExpect(jsonPath("$.status",nullValue()))
                .andExpect(jsonPath("$.avatar_url",nullValue()))
                .andExpect(jsonPath("$.is_receive_messages_friend_only",is(true)))
                .andExpect(jsonPath("$.is_friends_list_hidden",is(true)));
    }
    @Test
    @WithUserDetails("test1234")
    public void getNonExistUserInfoTest() throws Exception{
        mockMvc.perform(get(UserController.USER.replaceAll("\\{username\\}","test4567")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error",is(CustomExceptionHandler.NOT_FOUND_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NOT_FOUND_MESSAGE)));
    }

    @Test
    @WithUserDetails("test1234")
    public void updateInfoTest() throws Exception{
        mockMvc.perform(
                patch(UserController.USER.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDto.builder()
                                                .firstname("new_firstname")
                                                .lastname("new_lastname")
                                                .bio("new_bio")
                                                .status("new_status")
                                                .avatarUrl("new_avatar_url")
                                                .username("new_username")
                                                .email("new_email@mail.ru")
                                                .isFriendsListHidden(true)
                                                .isReceiveMessagesFriendOnly(true)
                                                .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.access_token", notNullValue()));
        mockMvc.perform(get(UserController.USER.replaceAll("\\{username\\}","new_username")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname",is("new_firstname")))
                .andExpect(jsonPath("$.lastname",is("new_lastname")))
                .andExpect(jsonPath("$.username",is("new_username")))
                .andExpect(jsonPath("$.email",is("new_email@mail.ru")))
                .andExpect(jsonPath("$.bio",is("new_bio")))
                .andExpect(jsonPath("$.status",is("new_status")))
                .andExpect(jsonPath("$.avatar_url",is("new_avatar_url")))
                .andExpect(jsonPath("$.is_receive_messages_friend_only",is(true)))
                .andExpect(jsonPath("$.is_friends_list_hidden",is(true)));
    }

    @Test
    @WithUserDetails("test1234")
    public void updateUserInfoTest() throws Exception{
        mockMvc.perform(
                patch(UserController.USER.replaceAll("\\{username\\}","test2345"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                UserDto.builder()
                                        .firstname("new_firstname")
                                        .lastname("new_lastname")
                                        .bio("new_bio")
                                        .status("new_status")
                                        .avatarUrl("new_avatar_url")
                                        .username("new_username")
                                        .email("new_email@mail.ru")
                                        .isFriendsListHidden(true)
                                        .isReceiveMessagesFriendOnly(true)
                                        .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.FORBIDDEN_ERROR)))
                .andExpect(jsonPath(
                        "$.error_description",is(UserService.NO_PERMISSION_MESSAGE)
                        )
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void updateInfoUsernameAlreadyExistTest() throws Exception{
        mockMvc.perform(
                        patch(UserController.USER.replaceAll("\\{username\\}","test1234"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                UserDto.builder()
                                                        .username("test2345")
                                                        .build()
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath("$.error_description",is(String.format(UserService.USER_EXIST,"test2345")))
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void updateInfoEmailAlreadyExistTest() throws Exception{
        mockMvc.perform(
                        patch(UserController.USER.replaceAll("\\{username\\}","test1234"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                UserDto.builder()
                                                        .email("test2@gmail.com")
                                                        .build()
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath("$.error_description",is(String.format(UserService.EMAIL_EXIST,"test2@gmail.com")))
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void changePasswordTest() throws Exception{
        mockMvc.perform(
                patch(UserController.USER_CHANGE_PASSWORD.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                PasswordDto.builder()
                                        .oldPassword("test1234")
                                        .newPassword("new_password")
                                        .confirmPassword("new_password")
                                        .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(UserService.PASSWORD_HAS_CHANGED)));
        mockMvc.perform(
                post(AuthenticationController.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LoginDto.builder()
                                        .username("test1234")
                                        .password("new_password")
                                        .build()
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.refresh_token", notNullValue()));
    }

    @Test
    @WithUserDetails("test1234")
    public void changeUserPasswordTest() throws Exception{
        mockMvc.perform(
                        patch(UserController.USER_CHANGE_PASSWORD.replaceAll("\\{username\\}","test2345"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                PasswordDto.builder()
                                                        .oldPassword("test1234")
                                                        .newPassword("new_password")
                                                        .confirmPassword("new_password")
                                                        .build()
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.FORBIDDEN_ERROR)))
                .andExpect(jsonPath(
                                "$.error_description",is(UserService.NO_PERMISSION_MESSAGE)
                        )
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void changeInvalidPasswordTest() throws Exception{
        mockMvc.perform(
                        patch(UserController.USER_CHANGE_PASSWORD.replaceAll("\\{username\\}","test1234"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                PasswordDto.builder()
                                                        .oldPassword("invalid_password")
                                                        .newPassword("test12345")
                                                        .confirmPassword("test12345")
                                                        .build()
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath(
                                "$.error_description",is(UserService.INVALID_PASSWORD)
                        )
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void changeUnmatchedPasswordsTest() throws Exception{
        mockMvc.perform(
                        patch(UserController.USER_CHANGE_PASSWORD.replaceAll("\\{username\\}","test1234"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                PasswordDto.builder()
                                                        .oldPassword("test1234")
                                                        .newPassword("test12345")
                                                        .confirmPassword("test12346")
                                                        .build()
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath(
                                "$.error_description",is(UserService.PASSWORDS_NOT_MATCH)
                        )
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void deleteUserTest() throws Exception{
        mockMvc.perform(delete(UserController.USER.replaceAll("\\{username\\}","test1234")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recover_token", notNullValue()));
        mockMvc.perform(
                        post(AuthenticationController.LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                LoginDto.builder()
                                                        .username("test1234")
                                                        .password("test1234")
                                                        .build()
                                        )
                                )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.UNAUTHORIZED_ERROR)))
                .andExpect(jsonPath(
                                "$.error_description",is(UserService.NOT_FOUND_MESSAGE)
                        )
                );
    }

    @Test
    @WithUserDetails("test1234")
    public void deleteUserNoPermissionTest() throws Exception{
        mockMvc.perform(delete(UserController.USER.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.FORBIDDEN_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NO_PERMISSION_MESSAGE)));

    }

    @Test
    @WithUserDetails("test1234")
    public void recoverUserTest() throws Exception{
        MvcResult mvcResult=
                mockMvc.perform(delete(UserController.USER.replaceAll("\\{username\\}","test1234")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.recover_token", notNullValue()))
                        .andReturn();
        mockMvc.perform(put(UserController.USER.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                RecoverTokenDto.builder()
                                        .recoverToken(
                                                JsonPath
                                                        .read(
                                                                mvcResult.getResponse().getContentAsString(),
                                                                "$.recover_token"
                                                        )
                                        )
                                        .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.access_token", notNullValue()));

    }

    @Test
    @WithUserDetails("test2345")
    public void recoverUserNoPermissionTest() throws Exception{
        MvcResult mvcResult=
                mockMvc.perform(delete(UserController.USER.replaceAll("\\{username\\}","test2345")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.recover_token", notNullValue()))
                        .andReturn();
        mockMvc.perform(put(UserController.USER.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                        RecoverTokenDto.builder()
                                                .recoverToken(
                                                        JsonPath
                                                                .read(
                                                                        mvcResult.getResponse().getContentAsString(),
                                                                        "$.recover_token"
                                                                )
                                                )
                                                .build()
                                )
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.FORBIDDEN_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NO_PERMISSION_MESSAGE)));
    }

    @Test
    @WithUserDetails("test1234")
    public void recoverActiveUserTest() throws Exception{
        MvcResult mvcResult=
                mockMvc.perform(delete(UserController.USER.replaceAll("\\{username\\}","test1234")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.recover_token", notNullValue()))
                        .andReturn();
        mockMvc.perform(put(UserController.USER.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                        RecoverTokenDto.builder()
                                                .recoverToken(
                                                        JsonPath
                                                                .read(
                                                                        mvcResult.getResponse().getContentAsString(),
                                                                        "$.recover_token"
                                                                )
                                                )
                                                .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.access_token", notNullValue()));

        mockMvc.perform(put(UserController.USER.replaceAll("\\{username\\}","test1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                        RecoverTokenDto.builder()
                                                .recoverToken(
                                                        JsonPath
                                                                .read(
                                                                        mvcResult.getResponse().getContentAsString(),
                                                                        "$.recover_token"
                                                                )
                                                )
                                                .build()
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.NOT_FOUND_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NOT_FOUND_MESSAGE)));

    }

    @Test
    @WithUserDetails("test1234")
    public void sendFriendRequestTest() throws Exception{
        mockMvc.perform(post(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text",is(UserService.FRIEND_REQUEST_SUCCESS_SEND)));
    }

    @Test
    @WithUserDetails("test1234")
    public void sendExistFriendRequestTest() throws Exception{
        mockMvc.perform(post(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text",is(UserService.FRIEND_REQUEST_SUCCESS_SEND)));
        mockMvc.perform(post(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.FRIEND_REQUEST_EXIST)));
    }

    @Test
    @WithUserDetails("test1234")
    public void sendSelfFriendRequestTest() throws Exception{
        mockMvc.perform(post(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test1234")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.BAD_REQUEST_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.SELF_FRIEND_REQUEST)));
    }

    @Test
    @WithUserDetails("test1234")
    public void sendFriendRequestNonExistUserTest() throws Exception{
        mockMvc.perform(post(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test4567")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.NOT_FOUND_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NOT_FOUND_MESSAGE)));
    }

    @Test
    @WithUserDetails("test1234")
    public void getFriendListTest() throws Exception{
        List<UserDto> friends = new ArrayList<>();
        friends.add(
                UserDto.builder()
                        .firstname("test3")
                        .lastname("test3")
                        .email("test3@gmail.com")
                        .username("test3456")
                        .isFriendsListHidden(false)
                        .isReceiveMessagesFriendOnly(false)
                        .build()
        );
        mockMvc.perform(get(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test1234")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(friends)));

    }

    @Test
    @WithUserDetails("test1234")
    public void getUserFriendListTest() throws Exception{
        List<UserDto> friends = new ArrayList<>();
        friends.add(
                UserDto.builder()
                        .firstname("test1")
                        .lastname("test1")
                        .email("test1@gmail.com")
                        .username("test1234")
                        .isFriendsListHidden(false)
                        .isReceiveMessagesFriendOnly(false)
                        .build()
        );
        mockMvc.perform(get(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test3456")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(friends)));

    }

    @Test
    @WithUserDetails("test1234")
    public void getUserHiddenFriendListTest() throws Exception{
        mockMvc.perform(get(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test2345")))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.FORBIDDEN_ERROR)))
                .andExpect(jsonPath("$.error_description",is(
                        String.format(UserService.FRIEND_LIST_HIDDEN,"test2345")
                )));

    }

    @Test
    @WithUserDetails("test1234")
    public void getNonExistUserFriendListTest() throws Exception{
        mockMvc.perform(get(UserController.USER_FRIENDS.replaceAll("\\{username\\}","test4567")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(CustomExceptionHandler.NOT_FOUND_ERROR)))
                .andExpect(jsonPath("$.error_description",is(UserService.NOT_FOUND_MESSAGE)));
    }

}
