package com.example.hxds.bff.driver.service.impl;

import com.example.hxds.bff.driver.controller.form.StartCommentWorkflowForm;
import com.example.hxds.bff.driver.feign.OdrServiceApi;
import com.example.hxds.bff.driver.feign.WorkflowServiceApi;
import com.example.hxds.bff.driver.service.OrderCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OrderCommentServiceImpl implements OrderCommentService {
    @Resource
    private WorkflowServiceApi workflowServiceApi;


//    @Override
//    @Transactional
// 
//    public void startCommentWorkflow(StartCommentWorkflowForm form) {
//        workflowServiceApi.startCommentWorkflow(form);
//    }
}
