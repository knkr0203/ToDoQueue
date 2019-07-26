package com.kenkoro.springboot;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
		mav.addObject("name", session.getAttribute("name"));
		session.invalidate();
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
	
	@RequestMapping(value = "/toTop", method = RequestMethod.POST)
	public ModelAndView toTopPage(@ModelAttribute("formModel") Task task, ModelAndView mav) {
		session.setAttribute("name", task.getName());
		mav.setViewName("redirect:/");
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
		mav.addObject("content", session.getAttribute("content"));
		session.invalidate();
		return mav;
	}

	@RequestMapping(value = "/push", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView push(@ModelAttribute("formModel") @Validated Task task, BindingResult result,
			RedirectAttributes redirectAttributes, ModelAndView mav) {
		ModelAndView res = null;
		if (!result.hasErrors()) {
			res = new ModelAndView("redirect:/push");
			repository.saveAndFlush(task);
			redirectAttributes.addFlashAttribute("flash", "Push successed!");
			session.setAttribute("name", task.getName());
		} else {
			mav.setViewName("push");
			mav.addObject("error_flash", "Invalid input.");
			res = mav;
		}
		return res;
	}

	@RequestMapping(value = "/pop", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView pop(@ModelAttribute("formModel") Task task, RedirectAttributes redirectAttributes,
			ModelAndView mav) {
		List<Task> results = repository.findByNameLike(task.getName());
		if (results.size() == 0) {
			redirectAttributes.addFlashAttribute("error_flash", "This user has no contents.");
			session.setAttribute("content", "");
		} else {	
			redirectAttributes.addFlashAttribute("flash", "Popped!");
			session.setAttribute("content", results.get(0).getContent());
			repository.deleteById(results.get(0).getId());
		}
		session.setAttribute("name", task.getName());
		return new ModelAndView("redirect:/pop");
	}
}
