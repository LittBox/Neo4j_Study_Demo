package com.medical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.medical.service.DeepseekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AiController {

	private final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private DeepseekService deepseekService;

	@PostMapping("/chat")
	public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
		try {
			String prompt = body.getOrDefault("prompt", "");

			if (prompt == null || prompt.trim().isEmpty()) {
				ObjectNode errorResponse = mapper.createObjectNode();
				errorResponse.put("success", false);
				errorResponse.put("error", "Prompt cannot be empty");
				return ResponseEntity.badRequest().body(errorResponse);
			}

			// Call Deepseek service to generate text
			String content = deepseekService.generateText(prompt);

			// Build success response
			ObjectNode result = mapper.createObjectNode();
			result.put("success", true);
			result.put("content", content);

			return ResponseEntity.ok(result);

		} catch (IllegalArgumentException e) {
			ObjectNode errorResponse = mapper.createObjectNode();
			errorResponse.put("success", false);
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		} catch (Exception e) {
			ObjectNode errorResponse = mapper.createObjectNode();
			errorResponse.put("success", false);
			errorResponse.put("error", e.getMessage());
			errorResponse.put("errorType", e.getClass().getSimpleName());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

}