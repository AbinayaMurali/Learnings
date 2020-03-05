/**
 * 
 */
/**
 * @author I863571
 *
 */
package com.concur.worksimple.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.concur.worksimple.pojo.Request;
import com.concur.worksimple.service.RuleEngineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value="Rule Engine",description="API for Rule Engine integration - jeasy",tags="rule-engine")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "You are not authorized access the resource"),
        @ApiResponse(code = 404, message = "The resource not found")
})
public class RuleEngineController{
	
	@Autowired
	private RuleEngineService ruleEngineService;
	
	@RequestMapping(value="/rules", method=RequestMethod.POST)
	@ApiOperation(value="Rule engine determines values based on input parameters, the parameter should be the .yml file content", response = Request.class)
	public Map<String, Object> getResult(@RequestBody HashMap<String, String> request) throws IOException {	
		return this.ruleEngineService.getResult(request);
	}
}