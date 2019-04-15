package me.xwbz.flowable.controller;

import me.xwbz.flowable.MockBaseTest;
import me.xwbz.flowable.common.api.ApiResult;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class FormControllerTest extends MockBaseTest {

    @Override
    public String getPath() {
        return "/form";
    }

    @Test
    public void getForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(getPath())
                .param("formId", "formRequest")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ApiResult.Code.SUCCESS.getCode()));
    }

    @Test
    public void start() throws Exception {
        // {"fields":[
        // {"id":"startTime","name":"开始时间","overrideId":false,"placeholder":"empty","readOnly":false,"required":true,"type":"date"},
        // {"id":"endTime","name":"结束时间","overrideId":false,"placeholder":"empty","readOnly":false,"required":true,"type":"date"},
        // {"id":"reason","name":"请假原因","overrideId":false,"placeholder":"empty","readOnly":false,"required":true,"type":"text"}],
        // "key":"test","name":"请假流程",
        // "outcomes":[{"id":"sendToParent","name":"发给上级"},{"id":"sendToHr","name":"发给人事"}],
        // "version":0}
        mockMvc.perform(MockMvcRequestBuilders.post(getPath())
                .param("startTime", "2019-01-01")
                .param("endTime", "2019-02-01")
                .param("reason", "回家过年")
                .param("formId", "formRequest")
                .param("outcome", "sendToParent")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ApiResult.Code.SUCCESS.getCode()));
    }
}