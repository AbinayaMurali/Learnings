/**
 * 
 */
/**
 * @author I863571
 *
 */
package com.concur.worksimple.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.springframework.stereotype.Service;

@Service
public class RuleEngineService{
	
	public Map<String, Object> getResult(HashMap<String, String> request) throws UnsupportedEncodingException, IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("rules.yml"), "utf-8"))) {
			writer.write(request.get("request_rules_config"));
		}
		
		File rulesDescriptor = new File("rules.yml");
		
		//define facts
		Facts facts = new Facts();
		facts.put("request", request);
		
		//define rules
		Rules rules = MVELRuleFactory.createRulesFrom(new FileReader(rulesDescriptor));
		Iterator<Rule> iterator = rules.iterator();
		Rules reqRules = new Rules();

		//register the rules
		while(iterator.hasNext()) {
			Rule rule = iterator.next();
			reqRules.register(rule);
		}
		
		//set parameter to rule engine 
		RulesEngineParameters parameters = new RulesEngineParameters()
			    .skipOnFirstFailedRule(false);
		
		//fire rules from rule engine
		RulesEngine reqRuleEngine = new DefaultRulesEngine(parameters);
		reqRuleEngine.fire(reqRules, facts);
		
		return facts.get("request");
	}
}
