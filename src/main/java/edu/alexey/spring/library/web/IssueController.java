package edu.alexey.spring.library.web;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.services.IssueDescription;
import edu.alexey.spring.library.services.IssueService;
import lombok.RequiredArgsConstructor;

@Controller("web.IssueController")
@RequiredArgsConstructor
@RequestMapping("/ui")
@Secured("ROLE_ADMIN")
public class IssueController {

	private final IssueService issueService;

	@GetMapping("/issues")
	public String issues(Model model) {

		List<IssueDescription> items = issueService.getAll().stream()
				.mapToLong(Issue::getIssueId)
				.mapToObj(issueService::getDescriptionById).toList();

		model.addAttribute("items", items);
		return "issues";
	}
}
