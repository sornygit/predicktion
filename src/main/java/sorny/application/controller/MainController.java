package sorny.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sorny.domain.user.UserEntity;
import sorny.domain.MainApplicationService;

/**
 * Main controller for index, login mappings
 */
@Controller
public class MainController {
    @Autowired
    MainApplicationService mainApplicationService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        model.addAttribute("user", user);
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="username", required=false, defaultValue="admin") String username,
                        @RequestParam(value="password", required=false, defaultValue="admin") String password, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        return "login";
    }
}
