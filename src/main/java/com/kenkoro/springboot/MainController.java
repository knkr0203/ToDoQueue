package com.kenkoro.springboot;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kenkoro.springboot.repository.TaskRepository;

@Controller
public class MainController {

	@Autowired
	TaskRepository repository;

	@Autowired
	HttpSession session;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("index");
		return mav;
	}

	@RequestMapping(value = "/toPush", method = RequestMethod.POST)
	public ModelAndView toPushPage(@ModelAttribute("formModel") Task task, ModelAndView mav) {
		session.setAttribute("name", task.getName());
		mav.setViewName("redirect:/push");
		return mav;
	}

	@RequestMapping(value = "/toPop", method = RequestMethod.POST)
	public ModelAndView toPopPage(@ModelAttribute("formModel") Task task, ModelAndView mav) {
		session.setAttribute("name", task.getName());
		mav.setViewName("redirect:/pop");
		return mav;
	}

	@RequestMapping(value = "/push", method = RequestMethod.GET)
	public ModelAndView getPushPage(ModelAndView mav) {
		mav.setViewName("push");
		mav.addObject("name", session.getAttribute("name"));
		session.invalidate();
		return mav;
	}

	@RequestMapping(value = "/pop", method = RequestMethod.GET)
	public ModelAndView getPopPage(@ModelAttribute("formModel") Task task, ModelAndView mav) {
		mav.setViewName("pop");
		mav.addObject("name", session.getAttribute("name"));
		task.setName((String) session.getAttribute("name"));
		mav.addObject("content", session.getAttribute("content"));
		task.setContent((String) session.getAttribute("content"));

		session.invalidate();

		// to debug
		Iterable<Task> list = repository.findByNameLike(task.getName());
		mav.addObject("datalist", list);

		return mav;
	}

	@RequestMapping(value = "/push", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView push(@ModelAttribute("formModel") Task task, RedirectAttributes redirectAttributes,
			ModelAndView mav) {
		repository.saveAndFlush(task);
		redirectAttributes.addFlashAttribute("flash", "Push successed!");
		session.setAttribute("name", task.getName());
		return new ModelAndView("redirect:/push");
	}

	@RequestMapping(value = "/pop", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView pop(@ModelAttribute("formModel") Task task, RedirectAttributes redirectAttributes,
			ModelAndView mav) {
		List<Task> results = repository.findByNameLike(task.getName());
		if (results.size() == 0) {
			redirectAttributes.addFlashAttribute("flash", "This user has no contents.");
		} else {
			session.setAttribute("content", results.get(0).getContent());
			repository.deleteById(results.get(0).getId());
		}
		session.setAttribute("name", task.getName());
		return new ModelAndView("redirect:/pop");
	}
}
